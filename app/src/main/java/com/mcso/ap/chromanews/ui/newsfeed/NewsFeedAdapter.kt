package com.mcso.ap.chromanews.ui.newsfeed

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mcso.ap.chromanews.Glide
import com.mcso.ap.chromanews.model.MainViewModel
import com.mcso.ap.chromanews.R
import com.mcso.ap.chromanews.api.NewsPost
import com.mcso.ap.chromanews.databinding.NewsPostBinding
import java.util.*

class NewsFeedAdapter(private val viewModel: MainViewModel)
    : ListAdapter<NewsPost, NewsFeedAdapter.VH>(RedditDiff()) {

    companion object {
        val TAG = "NewsFeedAdapter"
    }

    inner class VH(val rowPostBinding : NewsPostBinding)
        : RecyclerView.ViewHolder(rowPostBinding.root) {
        init {

            rowPostBinding.root.setOnClickListener {
                var item = getItem(adapterPosition)

                MainViewModel.doOnePost(rowPostBinding.root.context, item)
            }

            rowPostBinding.share.setOnClickListener {
                val local = viewModel.getItemAt(adapterPosition)

                val sharebody: String = local!!.title
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, getItem(adapterPosition).link)
                startActivity(
                    it.context,
                    Intent.createChooser(shareIntent, "Share link using"),
                    null
                )
            }

            rowPostBinding.bookmarkFav.setOnClickListener {
                val position = adapterPosition
                // Toggle Bookmark
                val local = viewModel.getItemAt(position)

                if (viewModel.isFav(getItem(position))) {
                    Log.d("NewsFeedAdapter", "Already bookmarked")
                } else {
                    viewModel.addFav(getItem(position))

                    val uuid = UUID.randomUUID().toString()
                    // for storing in firebase database
                    val item = viewModel.getItemAt(position)

                    if (item != null) {
                        item.description?.let { it1 ->
                            item.imageURL?.let { it2 ->
                                    viewModel.createNewsMetadata(
                                        uuid,
                                        item.title,
                                        item.pubDate,
                                        it1,
                                        it2,
                                        item.link
                                    )
                                }
                        }
                    }
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        val rowBinding = NewsPostBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)

        return VH(rowBinding)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VH, position: Int) {

        val binding = holder.rowPostBinding
        var item = getItem(position)

        binding.title.text = item.title
        binding.description.text = item.description
        binding.title.setTextColor(Color.BLACK)
        binding.PubdateVal.setTextColor(Color.BLACK)
        binding.description.setTextColor(Color.BLACK)
        binding.authors.text = item.author
        binding.authors.setTextColor(Color.BLUE)

        if (item.imageURL != null){
            Glide.glideFetch(item.imageURL, null, binding.image)
        }

        if ( item.link.length > 60 ){
            binding.link.text = item.link.substring(0,60)+"\u2026"
        }
        else{
            binding.link.text = item.link
        }

        var delimeter_1 = "T"
        var delimeter_2 = "Z"

        var modified_datetime = item.pubDate.split(delimeter_1, delimeter_2)

        binding.PubdateVal.text = modified_datetime.joinToString(" ")

        if (viewModel.isFav(item)) {
            binding.bookmarkFav.setImageResource(R.drawable.baseline_bookmark_24)
        } else {
            binding.bookmarkFav.setImageResource(R.drawable.baseline_bookmark_border_24)
        }
    }


    class RedditDiff : DiffUtil.ItemCallback<NewsPost>() {
        override fun areItemsTheSame(oldItem: NewsPost, newItem: NewsPost): Boolean {
            return oldItem.title == newItem.title
        }
        override fun areContentsTheSame(oldItem: NewsPost, newItem: NewsPost): Boolean {
            return oldItem.title == newItem.title
        }
    }

}


