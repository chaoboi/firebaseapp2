package com.example.firebaseapp

import android.app.Activity
import com.example.firebaseapp.model.Post
import com.example.firebaseapp.ui.addPost.AddPostActivity
import com.example.firebaseapp.ui.home.HomeActivity
import com.example.firebaseapp.ui.login.LoginActivity
import com.example.firebaseapp.ui.postDetails.PostDetailsActivity

class Router {

    fun startHomeScreen(activity: Activity) {
        val home = HomeActivity.createIntent(activity)
        activity.startActivity(home)
    }

    fun startLoginScreen(activity: Activity) {
        val intent = LoginActivity.createIntent(activity)
        activity.startActivity(intent)
    }

    fun startAddPostScreen(activity: Activity) {
        val intent = AddPostActivity.createIntent(activity)
        activity.startActivity(intent)
    }

    fun startPostDetailsActivity(activity: Activity, post: Post) {
        val intent = PostDetailsActivity.createIntent(activity, post)
        activity.startActivity(intent)
    }

//    fun startEmailActivity(activity: Activity) {
//        val intent = EmailLoginActivity.createIntent(activity)
//        activity.startActivity(intent)
//    }
}