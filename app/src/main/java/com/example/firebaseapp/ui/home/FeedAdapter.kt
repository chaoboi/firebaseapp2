package com.example.firebaseapp.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseapp.R
import com.example.firebaseapp.model.Post
import com.example.firebaseapp.utils.DateUtils
import com.google.android.gms.common.util.DataUtils
import kotlinx.android.synthetic.main.post_item.view.*

/*
Data class for home feed
 */
class FeedAdapter(private val dateUtils: DateUtils) : RecyclerView.Adapter<FeedAdapter.PostViewHolder>() {

    /*
    Feed adapter will show posts created in the app on the home screen
     */
    private val posts = mutableListOf<Post>()
    private val onItemClickLiveData = MutableLiveData<Post>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedAdapter.PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view, onItemClickLiveData, dateUtils)
    }

    override fun onBindViewHolder(holder: FeedAdapter.PostViewHolder, position: Int) {
        holder.setItem(posts[position])
    }

    override fun getItemCount(): Int = posts.size


    fun onFeedUpdate(posts: List<Post>) {
        this.posts.clear()
        this.posts.addAll(posts)
        notifyDataSetChanged()
    }

    fun onPostItemClick(): LiveData<Post> = onItemClickLiveData

    class PostViewHolder(
        private val view: View,
        private val onItemClickLiveData: MutableLiveData<Post>,
        private val dateUtils: com.example.firebaseapp.utils.DateUtils
    ) : RecyclerView.ViewHolder(view) {

    private lateinit var post: Post

    init {
        view.setOnClickListener { onItemClickLiveData.postValue(post) }
    }

    fun setItem(post: Post) {
        this.post = post
        with(view) {
            author.text = post.author
            content.text = post.content
            time.text = dateUtils.mapToNormalisedDateText(post.timestamp)
            }
        }
    }
}