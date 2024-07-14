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
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.waly.chat.R
import com.waly.chat.databinding.ActivityProfileBinding
import com.waly.chat.models.Card
import com.waly.chat.models.Team
import com.waly.chat.models.TeamFull
import com.waly.chat.models.TeamMin
import com.waly.chat.models.UriDTO
import com.waly.chat.models.User
import com.waly.chat.models.UserFull
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
    private lateinit var teams: MutableList<TeamMin>
    private lateinit var cards: MutableList<Card>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)
        teams = mutableListOf()
        cards = mutableListOf()

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

        val teams = binding.teams
        val cards = binding.cards

        teams.setOnClickListener {
            teams.setTextColor(getColor(R.color.white))
            cards.setTextColor(getColor(R.color.gray_tertiary))
            showTeams()
        }
        cards.setOnClickListener {
            cards.setTextColor(getColor(R.color.white))
            teams.setTextColor(getColor(R.color.gray_tertiary))
            showCards()
        }

        fetchUserData()


    }

    private fun showTeams() {
        val list = binding.scrollLayout
        list.removeAllViews() // Remover todas as visualizações anteriores

        teams.forEach { team ->
            val userCard = layoutInflater.inflate(R.layout.card_chat_item_search, list, false)
            val userName = userCard.findViewById<TextView>(R.id.userName)
            val userImage = userCard.findViewById<LinearLayout>(R.id.cardImage)
            val userNickname = userCard.findViewById<TextView>(R.id.userNickname)
            val btnConnect = userCard.findViewById<ImageView>(R.id.connectButton)
            userName.text = team.roomName
            userNickname.text = team.description
            val imgLayout = layoutInflater.inflate(R.layout.home_image, userImage, false)
            Glide.with(applicationContext)
                .load(team.imgUrl)
                .centerCrop()
                .placeholder(R.drawable.unknow_image)
                .into(imgLayout.findViewById(R.id.statusImage))
            userImage.addView(imgLayout)
            btnConnect.setImageResource(R.drawable.icon_more_black)
            list.addView(userCard)
        }
    }


    private fun showCards() {
        val list = binding.scrollLayout
        list.removeAllViews()

        for (i in cards.indices step 2) {
            val horizontalLayout = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setPadding(0, 10, 0, 10)
                }
                orientation = LinearLayout.HORIZONTAL
            }

            val firstCard =
                layoutInflater.inflate(R.layout.card_board_main, horizontalLayout, false)
            val done = layoutInflater.inflate(R.layout.board_priority, horizontalLayout, false)
            val firstCardData = cards[i]
            firstCard.findViewById<TextView>(R.id.boardName).text = firstCardData.title
            firstCard.findViewById<TextView>(R.id.boardDesc).text = firstCardData.description

            if (!cards[i].done) {
                done.setBackgroundResource(R.color.red)
            }

            horizontalLayout.addView(firstCard)

            // Verificar se existe um segundo cartão no par e adicionar ao layout horizontal
            if (i + 1 < cards.size) {
                val secondCard =
                    layoutInflater.inflate(R.layout.card_board_main, horizontalLayout, false)
                val secondCardData = cards[i + 1]
                secondCard.findViewById<TextView>(R.id.boardName).text = secondCardData.title
                secondCard.findViewById<TextView>(R.id.boardDesc).text = secondCardData.description

                horizontalLayout.addView(secondCard)
            } else {
                // Se não houver um segundo cartão, adicionar um espaço vazio para balancear o layout
                val emptySpace = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                }
                horizontalLayout.addView(emptySpace)
            }

            // Adicionar o layout horizontal ao layout vertical principal
            list.addView(horizontalLayout)
        }
    }


    private fun fetchUserData() {
        val token = session.accessToken
        val service = NetworkUtils.createServiceUser()

        service.findMeFull(token!!)
            .enqueue(object : Callback<UserFull> {
                override fun onResponse(call: Call<UserFull>, response: Response<UserFull>) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        if (user != null) {
                            showUserDetails(user.username, user.nickname, user.imgUrl)
                            teams = user.teams.toMutableList()
                            cards = user.cards.toMutableList()
                            showTeams()
                        }
                    }
                }

                override fun onFailure(call: Call<UserFull>, t: Throwable) {
                    Log.e("PROFILE FRAG", "Error fetching user data", t)
                }
            })
    }

    private fun showUserDetails(username: String, nickname: String, imgUrl: String) {
        val userName = binding.userName
        userName.text = username
        val nicknameView = binding.userNick
        nicknameView.text = nickname
        val userInfo = binding.userInfo
        val userImage = layoutInflater.inflate(R.layout.profile_image, userInfo, false)
        val imageView = userImage.findViewById<ImageView>(R.id.userImage)
        val edtImage = userImage.findViewById<ImageView>(R.id.editImage)
        edtImage.setOnClickListener {
            selectImageFromGallery(it)
        }

        Glide
            .with(this)
            .load(imgUrl)
            .placeholder(R.drawable.unknow_image)
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

    private fun updateUserImage(uri: UriDTO) {
        val service = NetworkUtils.createServiceUser()
        val token = session.accessToken

        service.updateUserImage(token!!, uri)
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        if (user != null) {
                            showUserDetails(user.username, user.nickname, user.imgUrl)
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