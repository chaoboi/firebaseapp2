package com.example.firebaseapp.ui.postDetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseapp.R
import com.example.firebaseapp.model.Comment

/*
Comments adapter will feed the comments table with data
 */
class CommentsAdapter(private val dateUtils: com.example.firebaseapp.utils.DateUtils) : RecyclerView.Adapter<CommentViewHolder>() {

    //the list of comment objects to fill our list
    private val comments = mutableListOf<Comment>()

    /*
    Use the comment list item for the list of comments in our table
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_list_item, parent, false)

        return CommentViewHolder(view, dateUtils)
    }

    //Bind the view holder to some data, this will be the position in the list of comments
    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) = holder.setItem(comments[position])

    //Get size of comments in list
    override fun getItemCount(): Int = comments.size

    //Clear comments
    fun onCommentsUpdate(comments: List<Comment>) {
        this.comments.clear()
        this.comments.addAll(comments)
        notifyDataSetChanged()
    }

}