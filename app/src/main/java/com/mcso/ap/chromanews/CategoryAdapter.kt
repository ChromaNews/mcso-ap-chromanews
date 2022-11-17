package com.mcso.ap.chromanews

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.mcso.ap.chromanews.api.RecyclerData
import com.mcso.ap.chromanews.databinding.RowLayoutBinding

// import kotlinx.android.synthetic.main.photo_layout.view.*

// class PhotoAdapter(var context: Context) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {
class CategoryAdapter(private val viewModel: MainViewModel)
    : RecyclerView.Adapter<CategoryAdapter.VH>() {

    lateinit var imgid: ImageView
    lateinit var title: TextView

    var dataList = emptyList<RecyclerData>()

    internal fun setDataList(dataList: List<RecyclerData>) {
        this.dataList = dataList
    }

    private fun getPos(holder: RecyclerView.ViewHolder) : Int {
        val pos = holder.adapterPosition

        // notifyDataSetChanged was called, so position is not known
        if( pos == RecyclerView.NO_POSITION) {
            return holder.adapterPosition
        }
        return pos
    }
    // Provide a direct reference to each of the views with data items

    inner class VH(val rowBinding: RowLayoutBinding)
        : RecyclerView.ViewHolder(rowBinding.root) {

        // class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
        //    imgid = itemView.findViewById(R.id.categoryImage)
            imgid = rowBinding.categoryImage.findViewById(R.id.categoryImage)
        //    title = itemView.findViewById(R.id.categoryText)
            title = rowBinding.categoryText.findViewById(R.id.categoryText)
        }

    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH  {

        // Inflate the custom layout
       //  var view = LayoutInflater.from(parent.context).inflate(R.layout.row_layout, parent,
        //    false)
        val rowBinding = RowLayoutBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return VH(rowBinding)
    }

    // Involves populating data into the item through holder
    // override fun onBindViewHolder(holder: PhotoAdapter.ViewHolder, position: Int) {
    override fun onBindViewHolder(holder: VH, position: Int) {
        // Get the data model based on position
        var data = dataList[position]

        // val item = viewModel.getItemAt(position)
        // val binding = holder.rowBinding
        // Set item views based on your views and data model
        // if (item != null) {
        //    binding.categoryText.text = item.title
        //    binding.categoryImage.setImageDrawable(item.)
        // }
        holder.rowBinding.categoryText.text = data.title
        // holder.title.text = data.title
        // holder.imgid.setImageResource(data.imgid)
        holder.rowBinding.categoryImage.setImageResource(data.imgid)

        holder.itemView.setOnClickListener{
            Log.d("ANBU: title clicked", data.title)
            Log.d("ANBU: image clicked", data.imgid.toString())

            viewModel.setCategory(data.title.toString())
            it.isSelected = true
            it.setBackgroundColor(Color.RED)

           //  val activity = it.context as AppCompatActivity
            //val appCompatActivity = context as AppCompatActivity
           // activity.supportFragmentManager
           //     .beginTransaction()
           //     .replace(R.id.main_frame, NewsFeedFragment())
           //     .addToBackStack(null)
           //     .commit()

            // val manager: FragmentManager = (context as AppCompatActivity).supportFragmentManager
            // val Ft: FragmentTransaction = manager.beginTransaction()
        }
    }

    //  total count of items in the list
    override fun getItemCount() = dataList.size
    // : Int {
    //    return viewModel.getItemCount()
    // }
    // dataList.size
}