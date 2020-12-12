package com.example.firebaseapp.ui.home

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebaseapp.R
import com.example.firebaseapp.Router
import com.example.firebaseapp.firebase.authentication.AuthenticationManager
import com.example.firebaseapp.firebase.realTimeDatabase.RealtimeDatabaseManager
import com.example.firebaseapp.model.Post
import com.example.firebaseapp.ui.login.LoginActivity
import com.example.firebaseapp.utils.DateUtils
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private val authenticationManager by lazy { AuthenticationManager() }

    private val realtimeDatabaseManager by lazy { RealtimeDatabaseManager() }

    private val router by lazy { Router() }
    private val feedAdapter by lazy { FeedAdapter(DateUtils()) }

    companion object {
        fun createIntent(context: Context) = Intent(context, HomeActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        MobileAds.initialize(this@HomeActivity)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        initialize()
    }

    override fun onStart() {
        super.onStart()
        listenForPostsUpdates()
    }

    override fun onStop() {
        super.onStop()
        realtimeDatabaseManager.removePostsValuesChangesListener()
    }

    private fun listenForPostsUpdates() {
        realtimeDatabaseManager.onPostsValuesChange().observe(this, Observer(::onPostsUpdate))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_home, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item?.itemId) {
            R.id.action_logout -> {
                authenticationManager.signOut(this)
                router.startLoginScreen(this)
                finish()
                true
            }
            else -> {
                if (item != null) {
                    super.onOptionsItemSelected(item)
                }
                true
            }
        }


    private fun onPostsUpdate(posts: List<Post>) {
        feedAdapter.onFeedUpdate(posts)
    }

    private fun onPostItemClick(post: Post) {
        router.startPostDetailsActivity(this, post)
    }

    private fun initialize() {
//        setSupportActionBar(homeToolbar)
        initializeRecyclerView()
        addPostFab.setOnClickListener { router.startAddPostScreen(this)}


        feedAdapter.onPostItemClick()
            .observe(this, Observer(::onPostItemClick))
    }

    private fun initializeRecyclerView() {
        postsFeed.layoutManager = LinearLayoutManager(this)
        postsFeed.setHasFixedSize(true)
        postsFeed.adapter = feedAdapter

        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        postsFeed.addItemDecoration(divider)
    }
}