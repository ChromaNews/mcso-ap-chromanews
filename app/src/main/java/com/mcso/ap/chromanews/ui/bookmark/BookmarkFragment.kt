package com.mcso.ap.chromanews.ui.bookmark

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcso.ap.chromanews.model.MainViewModel
import com.mcso.ap.chromanews.databinding.FragmentRvBinding
import com.mcso.ap.chromanews.ui.newsfeed.NewsFeedAdapter

class BookmarkFragment : Fragment() {
    // https://developer.android.com/topic/libraries/view-binding#fragments
    private var _binding: FragmentRvBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRvBinding.inflate(inflater, container, false)
        val root: View = binding.root
        Log.d("FavoritesFragment", "onCreateView $viewModel")
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // (requireActivity() as AppCompatActivity).supportActionBar?.title = "Bookmarks"
        binding.recyclerRVView.layoutManager = LinearLayoutManager(binding.recyclerRVView.context)
        var adapter = NewsFeedAdapter(viewModel)
        binding.recyclerRVView.adapter = adapter

        viewModel.observeLiveFavoritesData().observe(viewLifecycleOwner) {
            Log.d("Anbu: LiveDATA", it.toString())
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}