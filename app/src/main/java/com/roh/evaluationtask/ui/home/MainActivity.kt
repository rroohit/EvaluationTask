package com.roh.evaluationtask.ui.home

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.roh.evaluationtask.R
import com.roh.evaluationtask.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "MAIN_ACTIVITY"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setTitle(getString(R.string.app_name))

        val postAdapter = HomePostAdapter()
        postAdapter.setHasStableIds(true)
        binding.rvHome.apply {
            adapter = postAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            hasFixedSize()
        }

        binding.rvHome.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!viewModel.isLoading &&
                    (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 1 &&
                    firstVisibleItemPosition >= 0
                ) {
                    // Log.d(TAG, "OnScrolled load more data")
                    // Load next page
                    viewModel.fetchPosts()
                }
            }
        })


        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.posts.collect { newPosts ->
                    postAdapter.submitList(newPosts)
                }
            }
        }

    }

    // Using paging3 ex
    /*private suspend fun initPagination3(postAdapter: HomePostAdapter){
        viewModel.getPosts().collect { newPosts ->
            // postAdapter.submitData(newPosts)
        }
    }*/


}