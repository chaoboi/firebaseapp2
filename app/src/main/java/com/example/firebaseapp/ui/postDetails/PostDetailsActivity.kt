package com.example.firebaseapp.ui.postDetails

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebaseapp.R
import com.example.firebaseapp.firebase.authentication.AuthenticationManager
import com.example.firebaseapp.firebase.realTimeDatabase.RealtimeDatabaseManager
import com.example.firebaseapp.model.Post
import com.example.firebaseapp.model.Comment
import com.example.firebaseapp.utils.DateUtils
import kotlinx.android.synthetic.main.activity_post_details.*

class PostDetailsActivity : AppCompatActivity() {
    var count = 0
    //Post model
    private lateinit var post: Post

    //ref for comments adapter
    private val commentsAdapter by lazy { CommentsAdapter(DateUtils()) }
    //ref for auth manager
    private val authenticationManager by lazy { AuthenticationManager() }
    //ref for realtime database
    private val realtimeDatabaseManager by lazy { RealtimeDatabaseManager() }

    //static object to send data to post details class through activity
    companion object {
        private const val POST_EXTRA = "post_extra"
        fun createIntent(context: Context, post: Post) = Intent(context, PostDetailsActivity::class.java).apply {
            putExtra(POST_EXTRA, post)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)
        initialize()
    }

    override fun onStart() {
        super.onStart()
        listenForComments()
    }

    override fun onStop() {
        super.onStop()
        realtimeDatabaseManager.removeCommentsValuesChangesListener()
    }

    private fun initialize() {
//        setSupportActionBar(postDetailsToolbar)
        extractArguments()
        initializeClickListener()
        initializeRecyclerView()
        if (authenticationManager.getCurrentUser() != post.author) {
            updatePostButton.visibility = View.GONE
            deletePostButton.visibility = View.GONE
        }

        postText.setText(post.content, TextView.BufferType.EDITABLE)
    }

    private fun initializeRecyclerView() {
        commentsRecyclerView.layoutManager = LinearLayoutManager(this)
        commentsRecyclerView.setHasFixedSize(true)
        commentsRecyclerView.adapter = commentsAdapter

        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        commentsRecyclerView.addItemDecoration(divider)
    }

    private fun listenForComments() {
        realtimeDatabaseManager.onCommentsValuesChange(post.id)
            .observe(this, Observer(::onCommentsUpdate))
    }

    private fun onCommentsUpdate(comments: List<Comment>) {
        commentsAdapter.onCommentsUpdate(comments)
    }

    private fun initializeClickListener() {
        updatePostButton.setOnClickListener {
            realtimeDatabaseManager.updatePostContent(post.id, postText.text.toString().trim())
            finish()
        }

        deletePostButton.setOnClickListener {
            realtimeDatabaseManager.deletePost(post.id)
            finish()
        }

        addCommentButton.setOnClickListener {
            val comment = commentEditText.text.toString().trim()
            if(comment.isNotEmpty()) {
                realtimeDatabaseManager.addComment(post.id, comment)
            }
        }

        button_like.setOnClickListener{
           // var likeButtonState = true

                count=count+1
                realtimeDatabaseManager.updateLikeContent(post.id,count)
                Toast.makeText(applicationContext,"Liked post worked!",Toast.LENGTH_SHORT).show()

              //  likeButtonState=false

        }
    }

    private fun extractArguments() {
        post = intent.getParcelableExtra(POST_EXTRA)!!
    }
}