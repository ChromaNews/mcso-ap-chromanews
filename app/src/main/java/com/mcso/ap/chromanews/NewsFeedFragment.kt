package com.mcso.ap.chromanews

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager

import com.mcso.ap.chromanews.databinding.FragmentRvBinding
import kotlin.math.abs


class NewsFeedFragment: Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentRvBinding? = null
    private var default_category = mutableListOf<String>("top")

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): NewsFeedFragment {
            Log.d("ANBU: ", "instance")
            return NewsFeedFragment()
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
        Log.d(javaClass.simpleName, "ANBU NewsFeedFragment onViewCreated")
        Log.d("NewsFeedFragment onViewCreated", viewModel.subreddit.value.toString())

        // (requireActivity() as AppCompatActivity).supportActionBar?.title = "News Feed"
        binding.recyclerRVView.layoutManager = LinearLayoutManager(binding.recyclerRVView.context)
        val adapter = NewsFeedAdapter(viewModel)
        binding.recyclerRVView.adapter = adapter

        var category_list = viewModel.getCategories().value

        if (category_list?.isEmpty() == true){
            viewModel.setCategory(default_category)
            viewModel.netPosts()
        }

        viewModel.observeLiveData().observe(viewLifecycleOwner){
            Log.d("ANBU: ", "ObserveLiveData")
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        }

        viewModel.observeCategory().observe(viewLifecycleOwner){
            viewModel.netPosts()
            adapter.notifyDataSetChanged()
        }

        binding.swipeRefreshLayout.setOnRefreshListener{
            viewModel.netPosts()
            adapter.notifyDataSetChanged()
        }

        viewModel.fetchDone.observe(viewLifecycleOwner) {
            binding.swipeRefreshLayout.isRefreshing = false
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}