package com.example.firebaseapp.ui.addPost

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseapp.R
import com.example.firebaseapp.firebase.realTimeDatabase.RealtimeDatabaseManager
import com.example.firebaseapp.utils.showToast
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_post.*

class AddPostActivity : AppCompatActivity() {
      lateinit var filepath : Uri

    //static function to be used in router
    companion object {
        fun createIntent(context: Context) = Intent(context, AddPostActivity::class.java)
    }

    //ref to realtime database
    private val realtimeDatabaseManager by lazy { RealtimeDatabaseManager() }

    //on create function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)
        initialize()
    }

    //
    private fun initialize() {
        setupClickListener()
        focusPostMessageInput()
    }



    private fun setupClickListener() {
        button_choose_file.setOnClickListener {
            startFileChooser()
        }
        button_deleteImage.setOnClickListener(){
            deleteImage()
        }
        button_uploadImage.setOnClickListener{
            uploadFile()

        }

        addPostButton.setOnClickListener {
            addPostIfNotEmpty()
        }
    }

    private fun deleteImage() {
    if(filepath!=null) {
        var pd = ProgressDialog(this)
        pd.setTitle("Deleting Process")
        var imageRef = FirebaseStorage.getInstance().reference.child("images/pic.jpg")
        imageRef.delete()
            .addOnSuccessListener {p0 ->
                pd.dismiss()
                Toast.makeText(applicationContext,"file deleted",Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener{p0 ->
                Toast.makeText(applicationContext,p0.message,Toast.LENGTH_LONG).show()
            }
        imageView.setImageResource(R.drawable.ic_launcher_background)

    }

    }

    private fun uploadFile() {
    if(filepath!=null){
        var pd = ProgressDialog(this)
        pd.setTitle("Uploading")
        pd.show()
        var imageRef = FirebaseStorage.getInstance().reference.child("images/pic.jpg")
        imageRef.putFile(filepath)
            .addOnSuccessListener {p0 ->
                pd.dismiss()
                Toast.makeText(applicationContext,"file uploaded",Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener{p0 ->
                Toast.makeText(applicationContext,p0.message,Toast.LENGTH_LONG).show()
            }
            .addOnProgressListener {p0 ->
                var progress = (100.0 * p0.bytesTransferred)/ p0.totalByteCount
                pd.setMessage("Uploaded ${progress.toInt()}")

            }
    }
    }

    private fun startFileChooser() {
        var i = Intent()
        i.setType("image/*")
        i.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(i,"Choose a picture"),111)


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 111 && resultCode== Activity.RESULT_OK && data !=null){
              filepath=data.data!!
              var bitmap = MediaStore.Images.Media.getBitmap(contentResolver,filepath)
            imageView.setImageBitmap(bitmap)
        }
    }

    private fun addPostIfNotEmpty() {
        val postMessage = postText.text.toString().trim()
        if (postMessage.isNotEmpty()) {
            realtimeDatabaseManager.addPost(postMessage,
                { showToast(getString(R.string.posted_successfully)) },
                { showToast(getString(R.string.posting_failed)) })
            finish()
        } else {
            showToast(getString(R.string.empty_post_message))
        }
    }

    private fun focusPostMessageInput() {
        //Allow user to type in keyboard
        postText.requestFocus()
    }

}