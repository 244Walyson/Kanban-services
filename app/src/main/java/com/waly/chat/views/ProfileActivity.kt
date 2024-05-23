package com.waly.chat.views

import SessionManager
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.waly.chat.R
import com.waly.chat.databinding.ActivityProfileBinding
import com.waly.chat.models.UriDTO
import com.waly.chat.models.User
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        binding.logoutButton.setOnClickListener {
            session.clearSession()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        fetchUserData()


    }

    private fun fetchUserData() {
        val token = session.accessToken
        val service = NetworkUtils.createServiceUser()

        service.getUser(token!!)
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        if (user != null) {
                            showUserDetails(user)
                        }
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("PROFILE FRAG", "Error fetching user data", t)
                }
            })
    }

    private fun showUserDetails(user: User) {
        val userName = binding.userName
        userName.text = user.username
        val nickname = binding.userNick
        nickname.text = user.nickname
        val userInfo = binding.userInfo
        val userImage = layoutInflater.inflate(R.layout.profile_image, userInfo, false)
        val imageView = userImage.findViewById<ImageView>(R.id.userImage)
        val edtImage = userImage.findViewById<ImageView>(R.id.editImage)
        edtImage.setOnClickListener {
            selectImageFromGallery(it)
        }

        Glide
            .with(this)
            .load(user.imgUrl)
            .centerCrop()
            .into(imageView)

        userInfo.removeAllViews()
        userInfo.addView(userImage)
    }


    fun selectImageFromGallery(view: View) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri = data.data!!
            val imageFile = saveImageLocally(selectedImageUri)
            uploadImage(imageFile)
        }
    }

    private fun saveImageLocally(imageUri: Uri): File {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFileName = "image_${System.currentTimeMillis()}.jpg"
        val imageFile = File(storageDir, imageFileName)
        contentResolver.openInputStream(imageUri)?.use { inputStream ->
            FileOutputStream(imageFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return imageFile
    }

    private fun uploadImage(imageFile: File) {
        val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)
        val service = NetworkUtils.createServiceFile()
        val token = session.accessToken

        service.uploadFile(token!!, body)
            .enqueue(object : Callback<UriDTO> {
                override fun onResponse(call: Call<UriDTO>, response: Response<UriDTO>) {
                    if (response.isSuccessful) {
                        val uri = response.body()
                        if (uri != null) {
                            updateUserImage(uri)
                        }
                    }
                }

                override fun onFailure(call: Call<UriDTO>, t: Throwable) {
                    Log.e("IMAGEEEEEEEE", "Error uploading image", t)
                }
            })
    }

    private fun updateUserImage(uri: UriDTO){
        val service  = NetworkUtils.createServiceUser()
        val token = session.accessToken

        service.updateUserImage(token!!, uri)
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        if (user != null) {
                            showUserDetails(user)
                        }
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("PROFILE FRAG", "Error updating user image", t)
                }
            })
    }

    companion object {
        private const val IMAGE_PICK_REQUEST = 1
    }

}