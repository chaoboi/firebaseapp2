package com.example.firebaseapp.ui.postDetails

import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseapp.model.Comment
import com.example.firebaseapp.utils.DateUtils
import kotlinx.android.synthetic.main.comment_list_item.view.*

/*

 */
class CommentViewHolder(private val view: android.view.View, private val dateUtils: DateUtils) : RecyclerView.ViewHolder(view) {

    fun setItem(comment: Comment) {
        with(view) {
            author.text = comment.author
            timestamp.text = dateUtils.mapToNormalisedDateText(comment.time)
            commentText.text = comment.content
        }
    }
}