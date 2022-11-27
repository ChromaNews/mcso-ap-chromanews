package com.mcso.ap.chromanews

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcso.ap.chromanews.databinding.FragmentRvBinding
import com.mcso.ap.chromanews.model.savedNews.NewsMetaData
import java.util.*
import kotlin.collections.ArrayList

class BookmarkFragment : Fragment() {
    // https://developer.android.com/topic/libraries/view-binding#fragments
    private var _binding: FragmentRvBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    // private val viewModel: MainViewModel by viewModels()
    private var bookmarkadapter: BookmarkAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRvBinding.inflate(inflater, container, false)
        val root: View = binding.root
        Log.d("BookmarksFragment", "onCreateView $viewModel")
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // (requireActivity() as AppCompatActivity).supportActionBar?.title = "Bookmarks"
        binding.recyclerRVView.layoutManager = LinearLayoutManager(binding.recyclerRVView.context)
        var adapter = BookmarkAdapter(viewModel)
        bookmarkadapter = adapter
        binding.recyclerRVView.adapter = adapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchSavedNewsList()
            adapter.notifyDataSetChanged()
            viewModel.fetchDone.value = false
        }

        viewModel.observeSavedNewsList().observe(viewLifecycleOwner) {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        }

        // viewModel.observeBookmarkSearchPostLiveData().observe(viewLifecycleOwner) {
        //    adapter.submitList(it)
        //    adapter.notifyDataSetChanged()
        // }
    }
}

/*
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
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                Log.d("onQueryTextChangeNewsFeed", "BookmarkFragment query: " + query)
                viewModel.setSearchTerm(query.toString())
                // if (query != null) {
                //    filter(query)
                // }
                return true
            }
        })
    }
}

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        when (id) {
            R.id.action_search -> {
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun filter(text: String) {
        val filteredlist = ArrayList<NewsMetaData>()
        val savedNewsList =  viewModel.observeSavedNewsList().value
        if (savedNewsList != null) {
            for (item in savedNewsList) {
                if (item.title!!.lowercase(Locale.getDefault())
                        .contains(text.lowercase(Locale.getDefault()))
                ) {
                    filteredlist.add(item)
                }
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(context, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            bookmarkadapter?.filterList(filteredlist, text)
            viewModel.setBookmarkList(filteredlist)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
*/