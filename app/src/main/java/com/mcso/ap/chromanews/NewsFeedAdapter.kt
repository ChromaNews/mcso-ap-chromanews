package com.mcso.ap.chromanews

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mcso.ap.chromanews.api.NewsPost
import com.mcso.ap.chromanews.databinding.RowPostBinding


// https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter
// Slick adapter that provides submitList, so you don't worry about how to update
// the list, you just submit a new one when you want to change the list and the
// Diff class computes the smallest set of changes that need to happen.
// NB: Both the old and new lists must both be in memory at the same time.
// So you can copy the old list, change it into a new list, then submit the new list.
//
// You can call adapterPosition to get the index of the selected item
class NewsFeedAdapter(private val viewModel: MainViewModel)
    : ListAdapter<NewsPost, NewsFeedAdapter.VH>(RedditDiff()) {

    companion object {
        val TAG = "NewsFeedAdapter"
    }

    // ViewHolder pattern holds row binding
    inner class VH(val rowPostBinding : RowPostBinding)
        : RecyclerView.ViewHolder(rowPostBinding.root) {
        init {
            //XXX Write me.

            rowPostBinding.root.setOnClickListener {
                Log.d("ANBU", "ItemSelected ")

                var item = getItem(adapterPosition)

                Log.d("ANBU: Item", item.toString())

                // MainViewModel.doOnePost(rowPostBinding.root.context, item)
            }

            rowPostBinding.share.setOnClickListener {
                Log.d("ANBU: ", "clicking share button")

                val local = viewModel.getItemAt(position)

                val sharebody: String = local!!.title

                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, getItem(adapterPosition).link)
                startActivity(it.context,
                    Intent.createChooser(shareIntent, "Share link using"),
                    null)
            }

            rowPostBinding.bookmarkFav.setOnClickListener {
                Log.d("ANBU: rowFav Selected", "rowFav Selected")
                val position = adapterPosition
                // Toggle Favorite
                val local = viewModel.getItemAt(position)
                Log.d("ANBU: favorite-1", local.toString())

                if(viewModel.isFav(getItem(position))) {
                    viewModel.removeFav(getItem(position))
                    // notifyDataSetChanged()
                } else {
                    viewModel.addFav(getItem(position))
                    // notifyDataSetChanged()
                }
                /*
                local?.let {
                    Log.d("ANBU: favorite-2", viewModel.isFav(it).toString())
                    if (viewModel.isFav(getItem(it)) {
                        viewModel.removeFav(getItem(it))
                        //rowPostBinding.rowFav.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                    } else {
                        viewModel.addFav(it)
                    }
                  notifyItemChanged(position)
                 */
                // viewModel.repoFetch()
                notifyDataSetChanged()
                // notifyItemChanged(position)

                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        val rowBinding = RowPostBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)

        return VH(rowBinding)

    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        val binding = holder.rowPostBinding

        var item = getItem(position)

        Log.d("ANBU title: ", item.title.toString())
        Log.d("ANBU description:  ", item.description.toString())
        Log.d("ANBU content: ", item.content.toString())

        binding.title.text = item.title

        binding.selfText.setLines(4)
        binding.selfText.text = item.description
        // binding.selfText.text = item.content

        binding.title.setTextColor(Color.BLACK)
        binding.PubdateVal.setTextColor(Color.BLACK)
        binding.selfText.setTextColor(Color.BLACK)
        val separator = ","
        binding.Category.text = item.category.joinToString(separator)
        binding.Category.setTextColor(Color.GREEN)

        // binding.author.text = item.creator.toString()
        //binding.author.setTextColor(Color.BLUE)

        if (item.imageURL != null){
            Glide.glideFetch(item.imageURL, null , binding.image)
        }

        if ( item.link.length > 60 ){
            binding.link.text = item.link.substring(0,60)+"\u2026"
        }
        else{
            binding.link.text = item.link
        }
        binding.PubdateVal.text = item.pubDate

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
            // return  NewsPost.spannableStringsEqual(oldItem.title.toString(),
            //                                       newItem.title.toString())
            return oldItem.title == newItem.title
        }
    }
}


