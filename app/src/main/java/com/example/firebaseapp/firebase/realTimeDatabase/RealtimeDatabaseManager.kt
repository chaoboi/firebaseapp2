package com.example.firebaseapp.firebase.realTimeDatabase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.firebaseapp.firebase.authentication.AuthenticationManager
import com.example.firebaseapp.model.Comment
import com.example.firebaseapp.model.Post
import com.google.firebase.database.*
import com.google.firebase.database.FirebaseDatabase

private const val POST_REFERENCE = "posts"
private const val POST_CONTENT_PATH = "content"
private const val COMMENTS_REFERENCE = "comments"
private const val COMMENT_POST_ID_PATH = "postId"

private const val LIKES_REFERENCE = "likes"
class RealtimeDatabaseManager {

    private val authenticationManager = AuthenticationManager()
    //main entry point to database
    private val database = FirebaseDatabase.getInstance()

    private val postsValues = MutableLiveData<List<Post>>()
    private val commentsValues = MutableLiveData<List<Comment>>()

    private lateinit var postsValueEventListener: ValueEventListener
    private lateinit var commentsValueEventListener: ValueEventListener

    /*
    Uses the authentication manager class to get the current logged in user name,
    current time and returns newly created Post instance
     */
    private fun createPost(key: String, content: String) : Post {
        val user = authenticationManager.getCurrentUser()
        val timestamp = getCurrentTime()
        return Post(key, content, user, timestamp)
    }

    fun addComment(postID: String, content: String) {
        val commentsReference = database.getReference(COMMENTS_REFERENCE)
        val key = commentsReference.push().key ?: ""
        val comment = createComment(postID, content)

        commentsReference.child(key).setValue(comment)
    }

    fun onCommentsValuesChange(postId: String): LiveData<List<Comment>> {
        listenForPostCommentsValueChanges(postId)
        return commentsValues
    }

    fun removeCommentsValuesChangesListener() {
        database.getReference(COMMENTS_REFERENCE).removeEventListener(commentsValueEventListener)
    }

    fun onPostsValuesChange(): LiveData<List<Post>> {
        listenForPostsValueChanges()
        return postsValues
    }

    fun removePostsValuesChangesListener() {
        database.getReference(POST_REFERENCE).removeEventListener(postsValueEventListener)
    }

    private fun listenForPostCommentsValueChanges(postId: String) {
        commentsValueEventListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                /* No op */
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val comments = dataSnapshot.children.mapNotNull { it.getValue(Comment::class.java) }.toList()
                    commentsValues.postValue(comments)
                } else {
                    commentsValues.postValue(emptyList())
                }
            }
        }

        database.getReference(COMMENTS_REFERENCE)
            .orderByChild(COMMENT_POST_ID_PATH)
            .equalTo(postId)
            .addValueEventListener(commentsValueEventListener)
    }

    fun updatePostContent(key: String, content: String) {

        database.getReference(POST_REFERENCE)
            .child(key)
            .child(POST_CONTENT_PATH)
            .setValue(content)
    }

    fun updateLikeContent(key: String, content: Int) {

        database.getReference(POST_REFERENCE)
            .child(key)
            .child(LIKES_REFERENCE)
            .setValue(content)
    }

    fun deletePost(key: String) {
        database.getReference(POST_REFERENCE)
            .child(key)
            .removeValue()

        deletePostComments(key)
    }

    private fun deletePostComments(postID: String) {
        database.getReference(COMMENTS_REFERENCE)
            .orderByChild(COMMENT_POST_ID_PATH)
            .equalTo(postID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { it.ref.removeValue() }
                }
            })
    }

    private fun listenForPostsValueChanges() {

        postsValueEventListener = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()) {
                    val posts = snapshot.children.mapNotNull { it.getValue(Post::class.java) }.toList()
                    postsValues.postValue(posts)
                } else {
                    postsValues.postValue(emptyList())
                }
            }

        }

        database.getReference(POST_REFERENCE).addValueEventListener(postsValueEventListener)
    }

    private fun createComment(postID: String, content: String): Comment {
        val user = authenticationManager.getCurrentUser()
        val timestamp = getCurrentTime()
        return Comment(postID, user, timestamp, content)
    }

    //get current time in millisecond
    private fun getCurrentTime() = System.currentTimeMillis()

    //Add a post
    fun addPost(content: String, onSuccessAction: () -> Unit, onFailureAction: () -> Unit) {
        /*
        DatabaseReference class represents a particular location in the database
         */
        val postsReference = database.getReference(POST_REFERENCE)
        /*
        posts will be added as a child of the posts node
         */
        val key = postsReference.push().key ?: ""
        val post = createPost(key, content)
        /*
        to access the newly created location you can use the child method and return a reference to the location
         */
        postsReference.child(key).setValue(post).addOnSuccessListener { onSuccessAction }
            .addOnFailureListener { onFailureAction }
    }
}