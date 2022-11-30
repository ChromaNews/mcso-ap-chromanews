package com.mcso.ap.chromanews.ui.newsfeed

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcso.ap.chromanews.R
import com.mcso.ap.chromanews.databinding.FragmentRvBinding
import com.mcso.ap.chromanews.model.MainViewModel


class BusinessFragment: Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentRvBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): BusinessFragment {
            Log.d("ANBU: ", "instance")
            return BusinessFragment()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "ANBU BusinessFragment onViewCreated")

        binding.recyclerRVView.layoutManager = LinearLayoutManager(binding.recyclerRVView.context)
        val adapter = NewsFeedAdapter(viewModel)
        binding.recyclerRVView.adapter = adapter

        viewModel.observeLiveData().observe(viewLifecycleOwner) {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getFeedForCategory()
        }

        viewModel.fetchDone.observe(viewLifecycleOwner) {
            binding.swipeRefreshLayout.isRefreshing = false
        }

        viewModel.observeSearchPostLiveData().observe(viewLifecycleOwner,
            Observer { filterList ->
                adapter.submitList(filterList)
                adapter.notifyDataSetChanged()
            }
        )

        // sentiment analyzer
        viewModel.observeSentimentScore().observe(viewLifecycleOwner) { sentimentData ->
            run {
                val score = String.format(
                    "%.6f", sentimentData.score.toDouble()
                ).toDouble()
                viewModel.updateUserSentiment(score)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (view != null && menuVisible){

        }
    }
}
