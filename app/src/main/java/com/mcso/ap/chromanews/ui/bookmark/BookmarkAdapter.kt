package com.mcso.ap.chromanews.ui.bookmark

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mcso.ap.chromanews.model.MainViewModel

import com.mcso.ap.chromanews.databinding.FragmentRvBinding

/**
 * Created by witchel on 8/25/2019
 */

class BookmarkAdapter(private val viewModel: MainViewModel)
    : RecyclerView.Adapter<BookmarkAdapter.VH>() {
    companion object {
        val TAG = "BookmarkAdapter"
    }

    // https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.ViewHolder#getBindingAdapterPosition()
    // Getting the position of the selected item is unfortunately complicated
    // This always returns a valid index.
    private fun getPos(holder: VH) : Int {
        val pos = holder.adapterPosition
        // notifyDataSetChanged was called, so position is not known
        if( pos == RecyclerView.NO_POSITION) {
            return holder.adapterPosition
        }
        return pos
    }
    // ViewHolder pattern
    inner class VH(val rowBinding: FragmentRvBinding)
        : RecyclerView.ViewHolder(rowBinding.root) {
        init {
            /*
            rowBinding.rowFav.setOnClickListener {
                val position = getPos(this)
                // Toggle Favorite
                val local = viewModel.getFavoriteAt(position)
                local.let {
                    if (viewModel.isFavorite(it)) {
                        viewModel.removeFavorite(it)
                    } else {
                        viewModel.addFavorite(it)
                    }
                    notifyItemChanged(position)
                }
            }
             */
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val rowBinding = FragmentRvBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val adapterPosition = getPos(holder)
        /*
        val item = viewModel.getFavoriteAt(adapterPosition)
        val binding = holder.rowBinding
        binding.rowText.text = item.name
        if (item.rating) {
            binding.rowPic.setImageResource(R.drawable.emoticon_excited)
        } else {
            binding.rowPic.setImageResource(R.drawable.emoticon_dead)
        }
        if (viewModel.isFavorite(item)) {
            binding.rowFav.setImageResource(R.drawable.ic_favorite_black_24dp)
        } else {
            binding.rowFav.setImageResource(R.drawable.ic_favorite_border_black_24dp)
        }
         */
    }

    override fun getItemCount() = viewModel.getFavoriteCount()
}

