package com.benny.bookapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.benny.bookapp.MyApplication
import com.benny.bookapp.activities.PdfDetailActivity
import com.benny.bookapp.databinding.RowPdfFavoriteBinding
import com.benny.bookapp.models.ModelPdf
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AdapterPdfFavorite  :RecyclerView.Adapter<AdapterPdfFavorite.HolderPdfFavorite> {

    private val context: Context

    private var booksArrayList: ArrayList<ModelPdf>

    private lateinit var binding: RowPdfFavoriteBinding



    constructor(context: Context, booksArrayList: ArrayList<ModelPdf>) {
        this.context = context
        this.booksArrayList = booksArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfFavorite {
        binding = RowPdfFavoriteBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderPdfFavorite(binding.root)
    }

    override fun onBindViewHolder(holder: HolderPdfFavorite, position: Int) {
            val model = booksArrayList[position]

        loadBookDetails(model, holder)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PdfDetailActivity::class.java)
            intent.putExtra("bookId", model.id)
            context.startActivity(intent)
        }
    }

    private fun loadBookDetails(model: ModelPdf, holder: AdapterPdfFavorite.HolderPdfFavorite) {
        val bookId = model.id

        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    val categoryId = "${snapshot.child("categoryId").value}"
                    val description = "${snapshot.child("description").value}"
                    val downloadsCount = "${snapshot.child("downloadsCount").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    val title = "${snapshot.child("title").value}"
                    val uid = "${snapshot.child("uid").value}"
                    val url = "${snapshot.child("url").value}"
                    val viewsCount = "${snapshot.child("viewsCount").value}"

                    model.isFavorite = true
                    model.title = title
                    model.description = description
                    model.categoryId = categoryId
                    model.timestamp = timestamp.toLong()
                    model.uid = uid
                    model.url = url
                    model.viewsCount = viewsCount.toLong()
                    model.downloadsCount = downloadsCount.toLong()


                    MyApplication.loadCategory("$categoryId", holder.categoryTv)
                    MyApplication.loadPdfFromUrlSinglePage("$url", "$title", holder.pdfView, holder.progressBar, null)
                    MyApplication.loadPdfSize("$url", "$title", holder.sizeTv)

                    holder.titleTv.text = title
                    holder.descriptionTv.text = description
                    //holder.dateTv.text = date

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    override fun getItemCount(): Int {
        return booksArrayList.size
    }

    inner class HolderPdfFavorite(itemView: View) : RecyclerView.ViewHolder(itemView){

        var pdfView = binding.pdfView
        var progressBar = binding.progressBar
        var titleTv = binding.titleTv
        var removeFavBtn = binding.removeFavBtn
        var descriptionTv = binding.descriptionTv
        var categoryTv = binding.categoryTv
        var sizeTv = binding.sizeTv
        var dateTv = binding.dateTv
    }


}