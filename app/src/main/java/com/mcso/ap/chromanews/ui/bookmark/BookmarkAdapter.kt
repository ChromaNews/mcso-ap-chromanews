package com.mcso.ap.chromanews.ui.bookmark

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mcso.ap.chromanews.databinding.SavedNewsBinding
import com.mcso.ap.chromanews.model.savedNews.NewsMetaData
import com.mcso.ap.chromanews.model.MainViewModel
import com.mcso.ap.chromanews.Glide
import com.mcso.ap.chromanews.databinding.FragmentRvBinding


class BookmarkAdapter(private val viewModel: MainViewModel)
    : ListAdapter<NewsMetaData, BookmarkAdapter.VH>(RedditDiff()) {
    companion object {
        val TAG = "BookmarkAdapter"
    }

    var searchText: String = ""
    var booklist: List<NewsMetaData>? = null

    private fun getPos(holder: RecyclerView.ViewHolder): Int {
        val pos = holder.adapterPosition
        Log.d("ANBU: calling getPos position", pos.toString())
        if (pos == RecyclerView.NO_POSITION) {
            return holder.adapterPosition
        }
        return pos
    }

    inner class VH(val rowBinding: SavedNewsBinding) : RecyclerView.ViewHolder(rowBinding.root) {
        init {

            rowBinding.root.setOnClickListener {
                Log.d("ANBU", "ItemSelected ")
                val position = getPos(this)
                var item = viewModel.getSavedNewsList().value!![position]
                MainViewModel.openSavedNewsPost(rowBinding.root.context, item)
            }

            rowBinding.delete.setOnClickListener {
                val position = getPos(this)
                Log.d("ANBU: calling", "delete OnListener position")
                Log.d("ANBU: ", position.toString())
                viewModel.removeSavedNews(position)
                // notifyDataSetChanged()
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

        Log.d("ANBU: ", "calling onBindViewHolder adapterPosition")
        Log.d("ANBU: ", adapterPosition.toString())

        val item = viewModel.getNewsMeta(adapterPosition)

        val binding = holder.rowBinding

        if (searchText.isNotBlank()){
            val highlightedText = item.title.replace(searchText, "<font color='red'>$searchText</font>", true)
            binding.title.text = HtmlCompat.fromHtml(highlightedText, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
        else{
            binding.title.text = item.title
        }

        binding.description.text = item.description
        // binding.authors.text = item.authors
        binding.link.text = item.link
        binding.PubdateVal.text = item.pubDate

        if (item.imageURL != null) {
            Glide.glideFetch(item.imageURL, null, binding.image)
        }
    }

    class RedditDiff : DiffUtil.ItemCallback<NewsMetaData>() {
        override fun areItemsTheSame(oldItem: NewsMetaData, newItem: NewsMetaData): Boolean {
            return oldItem.firestoreID == newItem.firestoreID
        }

        override fun areContentsTheSame(oldItem: NewsMetaData, newItem: NewsMetaData): Boolean {
            // return  NewsPost.spannableStringsEqual(oldItem.title.toString(),
            //                                       newItem.title.toString())
            return oldItem.firestoreID == newItem.firestoreID
                    && oldItem.newsID == newItem.newsID
        }
    }

    override fun getItemCount(): Int {
        return viewModel.getSavedNewsCount()!!
    }

    fun filterList(filterList: List<NewsMetaData>, searchText: String) {
        this.searchText = searchText
        booklist = filterList
        notifyDataSetChanged()
    }
}
