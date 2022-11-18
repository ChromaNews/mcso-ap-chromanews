package com.mcso.ap.chromanews

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mcso.ap.chromanews.api.Repository
import com.mcso.ap.chromanews.databinding.ActivityMainBinding
import com.mcso.ap.chromanews.databinding.FragmentRvBinding


class CategoryFragment : Fragment() {
    companion object {
        val TAG : String = CategoryFragment::class.java.simpleName
    }
    // https://developer.android.com/topic/libraries/view-binding#fragments
    private lateinit var binding: FragmentRvBinding
    // This property is only valid between onCreateView and
    private var dataList = Repository.fetchData()
    private lateinit var recyclerView: RecyclerView
    private lateinit var  adapter: CategoryAdapter
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRvBinding.inflate(inflater, container, false)
        val root: View = binding.root
        Log.d(TAG, "onCreateView $viewModel")
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //SSS
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Categories"
        binding.recyclerRVView.layoutManager = GridLayoutManager(binding.recyclerRVView.context,
        2)
        adapter = CategoryAdapter(viewModel)
        binding.recyclerRVView.adapter = adapter
        //EEE // XXX Write me, action bar title,
        // recycler view layout manager, and adapter


        // added data from arraylist to adapter class.
        // recyclerView = binding.recyclerView
        // recyclerView.layoutManager = GridLayoutManager(applicationContext, 2)
        // adapter = CategoryAdapter(viewModel)
        // recyclerView.adapter = adapter

        adapter.setDataList(dataList)

        binding.swipeRefreshLayout.isRefreshing = false

        /*
        dataList.add(RecyclerData("Business",R.drawable.business_image))
        dataList.add(RecyclerData("Environment",R.drawable.env_image))
        dataList.add(RecyclerData("Entertainment",R.drawable.entertainment_image))
        dataList.add(RecyclerData("Food",R.drawable.food_news_image))
        dataList.add(RecyclerData("Health",R.drawable.health_news_image))
        dataList.add(RecyclerData("Politics",R.drawable.political_news_image))
        dataList.add(RecyclerData("Science",R.drawable.science_news_image))
        dataList.add(RecyclerData("Sports",R.drawable.sports_imagee))
        dataList.add(RecyclerData("Technology",R.drawable.tech_news_image))
        dataList.add(RecyclerData("Top",R.drawable.topnewsimage))
        dataList.add(RecyclerData("World",R.drawable.world_news_image))
         */


        // test newsdata
        //viewModel.netNewsData()
    }
}