package com.roh.evaluationtask.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.load
import coil.request.CachePolicy
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.transform.CircleCropTransformation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.roh.evaluationtask.R
import com.roh.evaluationtask.databinding.ItemPostBinding
import com.roh.evaluationtask.domain.model.Post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
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

            // *** the Image urls are dynamic which causing to load repeatedly
            binding.ivProfileImage.load(post.profilePic) {
                crossfade(true)
                placeholder(R.drawable.ic_default_profile)
                transformations(CircleCropTransformation())
                diskCachePolicy(CachePolicy.DISABLED)
                memoryCachePolicy(CachePolicy.DISABLED)
            }
            binding.ivPostImage.isVisible = (post.thumbnail.isNotEmpty())
            binding.ivPostImage.load(post.thumbnail) {
                crossfade(true)
                diskCachePolicy(CachePolicy.DISABLED)
                memoryCachePolicy(CachePolicy.DISABLED)
            }


            /*urlToBitmap(
                CoroutineScope(Dispatchers.IO),
                post.thumbnail,
                binding.root.context,
                onSuccess = {
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.ivPostImage.setImageBitmap(it)
                    }
                },
                onError = {

                })*/

        }
    }

    fun urlToBitmap(
        scope: CoroutineScope,
        imageURL: String,
        context: Context,
        onSuccess: (bitmap: Bitmap) -> Unit,
        onError: (error: Throwable) -> Unit
    ) {
        if (imageURL.isEmpty()) return
        var bitmap: Bitmap? = null
        val loadBitmap = scope.launch(Dispatchers.IO) {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(imageURL)
                .allowHardware(false)
                .build()
            val result = loader.execute(request)
            if (result is SuccessResult) {
                bitmap = (result.drawable as BitmapDrawable).bitmap
            } else if (result is ErrorResult) {
                cancel(result.throwable.localizedMessage ?: "ErrorResult", result.throwable)
            }
        }
        loadBitmap.invokeOnCompletion { throwable ->
            bitmap?.let {
                onSuccess(it)
            } ?: throwable?.let {
                onError(it)
            } ?: onError(Throwable("Undefined Error"))
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
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.toString() == newItem.toString()
    }

}