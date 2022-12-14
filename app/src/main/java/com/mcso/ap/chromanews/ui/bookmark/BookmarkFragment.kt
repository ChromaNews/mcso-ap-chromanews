package com.mcso.ap.chromanews.ui.bookmark

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcso.ap.chromanews.databinding.FragmentRvBinding
import com.mcso.ap.chromanews.model.MainViewModel

class BookmarkFragment : Fragment() {
    private var _binding: FragmentRvBinding? = null

    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private var bookmarkadapter: BookmarkAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRvBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerRVView.layoutManager = LinearLayoutManager(binding.recyclerRVView.context)
        var adapter = BookmarkAdapter(viewModel)
        bookmarkadapter = adapter
        binding.recyclerRVView.adapter = adapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchSavedNewsList()
            adapter.notifyDataSetChanged()
            viewModel.fetchDone.value = false
            binding.swipeRefreshLayout.isRefreshing = false
        }

        viewModel.observeSavedNewsList().observe(viewLifecycleOwner) {
            adapter.submitList(it){
                binding.recyclerRVView.scrollToPosition(0)
            }
            adapter.notifyDataSetChanged()
        }
    }
}

