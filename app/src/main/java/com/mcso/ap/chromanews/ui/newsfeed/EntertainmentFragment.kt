package com.mcso.ap.chromanews.ui.newsfeed

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcso.ap.chromanews.R

import com.mcso.ap.chromanews.databinding.FragmentRvBinding
import com.mcso.ap.chromanews.model.MainViewModel


class EntertainmentFragment: Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentRvBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): EntertainmentFragment {
            Log.d("ANBU: ", "instance")
            return EntertainmentFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(javaClass.simpleName, " ANBU NewsFeedFragment onCreateView")
        _binding = FragmentRvBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater!!.inflate(R.menu.search_menu, menu)

        val item = menu?.findItem(R.id.action_search)
        val searchView = item?.actionView as SearchView


        // search queryTextChange Listener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                Log.d("ANBU onQueryTextChangeNewsFeed", "query: " + query)
                viewModel.setSearchTerm(query.toString())
                return true
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "ANBU NewsFeedFragment onViewCreated")

        // (requireActivity() as AppCompatActivity).supportActionBar?.title = "News Feed"
        binding.recyclerRVView.layoutManager = LinearLayoutManager(binding.recyclerRVView.context)
        val adapter = NewsFeedAdapter(viewModel)
        binding.recyclerRVView.adapter = adapter

        viewModel.observeCategory().observe(viewLifecycleOwner){
       //     //if (viewModel.getCategories().value?.isEmpty() == false){
            viewModel.netPosts()
            //}
            // adapter.notifyDataSetChanged()
        }

        viewModel.observeLiveData().observe(viewLifecycleOwner){
            Log.d("ANBU: ", "ObserveLiveData")
            adapter.submitList(it){
                binding.recyclerRVView.scrollToPosition(0)
            }
            adapter.notifyDataSetChanged()
        }

        binding.swipeRefreshLayout.setOnRefreshListener{
            viewModel.netPosts()
            // adapter.notifyDataSetChanged()
        }

        viewModel.fetchDone.observe(viewLifecycleOwner) {
            binding.swipeRefreshLayout.isRefreshing = false
        }

        // Observing the search post
        viewModel.observeSearchPostLiveData().observe(viewLifecycleOwner){
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
       }

        // sentiment analyzer
        viewModel.observeSentimentScore().observe(viewLifecycleOwner){ sentimentData ->
            run {
                val score = String.format(
                    "%.6f", sentimentData.score.toDouble()
                ).toDouble()
                viewModel.updateUserSentiment(score)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        Log.d("ANBU: ", "Inside ONRESUME" )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}