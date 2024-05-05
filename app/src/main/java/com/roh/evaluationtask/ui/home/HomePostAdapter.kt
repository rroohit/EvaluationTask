package com.roh.evaluationtask.ui.home

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.roh.evaluationtask.R
import com.roh.evaluationtask.databinding.ItemPostBinding
import com.roh.evaluationtask.domain.model.Post
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.ceil

//class HomePostAdapter : PagingDataAdapter<Post, HomePostAdapter.ViewHolder>(DiffCallback) { // pagination3 ex
class HomePostAdapter : ListAdapter<Post, HomePostAdapter.ViewHolder>(DiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(true)
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            Log.d("MAIN_ACTIVITY", "bind: $post")
            binding.txtUsername.text = post.username
            binding.txtTitle.text = post.title
            binding.txtTimestamp.text =
                binding.root.context.getString(R.string.weeks_passed, weeksAgo(post.dated))

            binding.ivProfileImage.load(post.profilePic) {
                placeholder(R.drawable.ic_default_profile)
                transformations(CircleCropTransformation())
            }

            binding.ivPostImage.isVisible = post.thumbnail.isNotEmpty()
            binding.ivPostImage.load(post.thumbnail) {}
        }
    }

    fun weeksAgo(timestamp: Long): Int {
        val currentDate = Calendar.getInstance()
        val pastDate = Calendar.getInstance().apply { timeInMillis = timestamp }

        val diffInMillis = currentDate.timeInMillis - pastDate.timeInMillis
        val diffInWeeks = ceil(diffInMillis.toDouble() / (1000 * 60 * 60 * 24 * 7)).toInt()

        return abs(diffInWeeks)
    }
}

object DiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

}