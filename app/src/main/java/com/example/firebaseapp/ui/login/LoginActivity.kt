package com.example.firebaseapp.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebaseapp.R
import com.example.firebaseapp.Router
import com.example.firebaseapp.firebase.authentication.AuthenticationManager
import com.example.firebaseapp.firebase.authentication.RC_SIGN_IN
import com.example.firebaseapp.utils.showToast
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_login.*
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

class LoginActivity : AppCompatActivity() {

    private lateinit var mInterstitialAd: InterstitialAd

    //Router is used to do intents between activites
    private val router by lazy { Router() }

    //ref to auth manager class
    private val authenticationManager by lazy { AuthenticationManager() }

    //static function to be used in router
    companion object {
        fun createIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }

    //when activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        MobileAds.initialize(this) {}

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        mInterstitialAd.adListener = object : AdListener(){

            override fun onAdLoaded() {
                mInterstitialAd.show()
            }}
        initialize()
    }

    //once we sign, check to see if results are okay or have failed
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN) {

            if(resultCode == Activity.RESULT_OK) {
                router.startHomeScreen(this)
                
            } else {
                showToast("Signed in failed")
            }
        }
    }

    private fun initialize() {
       // setSupportActionBar(loginToolbar)
        continueToHomeScreenIfUserSignedIn()
        setupClickListeners()
    }

    //
    private fun continueToHomeScreenIfUserSignedIn() = if(isUserSignedIn()) router.startHomeScreen(this) else Unit

    private fun setupClickListeners() {
        googleSignInButton.setOnClickListener { authenticationManager.startSignInFlow(this)}

//        sign_with_email_btn.setOnClickListener { router.startEmailActivity(this)}

    }

    private fun isUserSignedIn() = authenticationManager.isUserSignedIn()
}