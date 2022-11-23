package com.mcso.ap.chromanews.ui.category

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.util.SparseBooleanArray
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mcso.ap.chromanews.model.MainViewModel
import com.mcso.ap.chromanews.R
import com.mcso.ap.chromanews.api.RecyclerData
import com.mcso.ap.chromanews.databinding.RowLayoutBinding
import java.util.*

// import kotlinx.android.synthetic.main.photo_layout.view.*

// class PhotoAdapter(var context: Context) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {
class CategoryAdapter(private val viewModel: MainViewModel)
    : RecyclerView.Adapter<CategoryAdapter.VH>() {

    lateinit var imgid: ImageView
    lateinit var title: TextView
    private lateinit var context: Context
    private val selectedItems = SparseBooleanArray()
    private val selectedCategory = mutableListOf<String>()

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
            imgid = rowBinding.categoryImage.findViewById(R.id.categoryImage)
            title = rowBinding.categoryText.findViewById(R.id.categoryText)
        }
    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        // Inflate the custom layout
       //  var view = LayoutInflater.from(parent.context).inflate(R.layout.row_layout, parent,
        //    false)
        val rowBinding = RowLayoutBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        context = parent.context
        return VH(rowBinding)
    }

    // Involves populating data into the item through holder
    // override fun onBindViewHolder(holder: PhotoAdapter.ViewHolder, position: Int) {
    override fun onBindViewHolder(holder: VH, position: Int) {
        // Get the data model based on position
        val highlightCategories = viewModel.getCategories().value
        var data = dataList[position]

        Log.d("ANBU: highlightCategories", highlightCategories.toString())

        holder.rowBinding.categoryText.text = data.title
        holder.rowBinding.categoryImage.setImageResource(data.imgid)

        if (highlightCategories != null) {
            for (category in highlightCategories){
                if (category == data.title.lowercase(Locale.ROOT)){
                    holder.itemView.isSelected = true
                    selectedItems.put(position, true)
                    val item = data.title.lowercase(Locale.ROOT)
                    if(! selectedCategory.contains(item)){
                        selectedCategory.add(data.title.lowercase(Locale.ROOT))
                    }
                    holder.itemView.setBackgroundColor(Color.RED)
                }
            }
        }

        holder.itemView.setOnClickListener{
            if (selectedItems[position, false]) {
                Log.d("ANBU:",  "Its in false condition")
                selectedItems.delete(position)
                it.isSelected = false
                selectedCategory.remove(data.title.lowercase(Locale.ROOT))
                it.setBackgroundColor(Color.WHITE)
            }
            else {
                Log.d("ANBU:", "Its in true condition")
                selectedItems.put(position, true)
                it.isSelected = true
                if(! selectedCategory.contains(data.title.lowercase(Locale.ROOT))){
                    selectedCategory.add(data.title.lowercase(Locale.ROOT))
                }
                it.setBackgroundColor(Color.RED)
            }

            Log.d("ANBU: CombinedCategory", selectedCategory.toString())
            if (selectedCategory.isEmpty()) {
                Toast.makeText(context, "No category is selected, " +
                        "so, displaying news feed with all the categories",
                    Toast.LENGTH_LONG).show()

                viewModel.setDefaultCategory()
            }
            else{
                viewModel.setCategory(selectedCategory)
            }
            // viewModel.setCategory(selectedCategory)
            //}
            Log.d("ANBU: ConfiguredCategory", viewModel.getCategories().value.toString())
        }
    }

    //  total count of items in the list
    override fun getItemCount() = dataList.size
    // : Int {
    //    return viewModel.getItemCount()
    // }
    // dataList.size
}