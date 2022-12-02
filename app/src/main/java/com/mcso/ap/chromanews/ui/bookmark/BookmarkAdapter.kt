package com.mcso.ap.chromanews.ui.bookmark

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mcso.ap.chromanews.Glide
import com.mcso.ap.chromanews.databinding.SavedNewsBinding
import com.mcso.ap.chromanews.model.MainViewModel
import com.mcso.ap.chromanews.model.savedNews.NewsMetaData

class BookmarkAdapter(private val viewModel: MainViewModel)
    : ListAdapter<NewsMetaData, BookmarkAdapter.VH>(NewsDiff()) {
    companion object {
        val TAG = "BookmarkAdapter"
    }

    private fun getPos(holder: RecyclerView.ViewHolder): Int {
        val pos = holder.adapterPosition

        if (pos != RecyclerView.NO_POSITION) {

            return pos
        }
        return  RecyclerView.NO_POSITION
    }

    inner class VH(val rowBinding: SavedNewsBinding) : RecyclerView.ViewHolder(rowBinding.root) {
        init {

            rowBinding.root.setOnClickListener {
                val position = getPos(this)
                var item = viewModel.getSavedNewsList().value!![position]
                MainViewModel.openSavedNewsPost(rowBinding.root.context, item)
            }

            rowBinding.delete.setOnClickListener {
                val position = getPos(this)
                viewModel.removeSavedNews(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val rowBinding = SavedNewsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val adapterPosition = getPos(holder)

        val item = viewModel.getNewsMeta(adapterPosition)

        val binding = holder.rowBinding

        binding.title.text = item.title

        binding.description.text = item.description
        binding.link.text = item.link
        binding.PubdateVal.text = item.pubDate

        if (item.imageURL != null) {
            Glide.glideFetch(item.imageURL, null, binding.image)
        }
    }

    class NewsDiff : DiffUtil.ItemCallback<NewsMetaData>() {
        override fun areItemsTheSame(oldItem: NewsMetaData, newItem: NewsMetaData): Boolean {
            return oldItem.firestoreID == newItem.firestoreID
        }

        override fun areContentsTheSame(oldItem: NewsMetaData, newItem: NewsMetaData): Boolean {
            return oldItem.firestoreID == newItem.firestoreID
                    && oldItem.newsID == newItem.newsID
        }
    }

    override fun getItemCount(): Int {
        return viewModel.getSavedNewsCount()!!
    }
}
