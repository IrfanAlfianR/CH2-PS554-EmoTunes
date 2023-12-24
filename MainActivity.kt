package com.harshRajpurohit.musicPlayer

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract.Contacts.Data
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.harshRajpurohit.musicPlayer.API.ApiConfig
import com.harshRajpurohit.musicPlayer.API.ApiService
import com.harshRajpurohit.musicPlayer.API.DataItem
import com.harshRajpurohit.musicPlayer.API.Response
import com.harshRajpurohit.musicPlayer.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter
    private lateinit var apiService: ApiService
    private lateinit var uploadSongService: ApiService
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    companion object {
        lateinit var MusicListMA: ArrayList<DataItem>
        lateinit var musicListSearch: ArrayList<DataItem>
        var search: Boolean = false
        var themeIndex: Int = 0
        val currentTheme = arrayOf(
            R.style.coolPink,
            R.style.coolBlue,
            R.style.coolPurple,
            R.style.coolGreen,
            R.style.coolBlack
        )
        val currentThemeNav = arrayOf(
            R.style.coolPinkNav, R.style.coolBlueNav, R.style.coolPurpleNav, R.style.coolGreenNav,
            R.style.coolBlackNav
        )
        val currentGradient = arrayOf(
            R.drawable.gradient_pink,
            R.drawable.gradient_blue,
            R.drawable.gradient_purple,
            R.drawable.gradient_green,
            R.drawable.gradient_black
        )
        var sortOrder: Int = 0
        val sortingList = arrayOf(
            MediaStore.Audio.Media.DATE_ADDED + " DESC", MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.SIZE + " DESC"
        )
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val themeEditor = getSharedPreferences("THEMES", MODE_PRIVATE)
        themeIndex = themeEditor.getInt("themeIndex", 0)
        setTheme(currentThemeNav[themeIndex])
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //for nav drawer
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //checking for dark theme
        if (themeIndex == 4 && resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO)
            Toast.makeText(this, "Black Theme Works Best in Dark Mode!!", Toast.LENGTH_LONG).show()
        initializeLayout()
        binding.shuffleBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "MainActivity")
            startActivity(intent)
        }
        binding.favouriteBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, FavouriteActivity::class.java))
        }
        binding.playlistBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, PlaylistActivity::class.java))
        }
        binding.playNextBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, PlayNext::class.java))
        }
        binding.chat.setOnClickListener {
            startActivity(Intent(this@MainActivity, ChatActivity::class.java))
        }
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
//                R.id.navFeedback -> startActivity(Intent(this@MainActivity, FeedbackActivity::class.java))
                R.id.navSettings -> startActivity(
                    Intent(
                        this@MainActivity,
                        SettingsActivity::class.java
                    )
                )

                R.id.navAbout -> startActivity(Intent(this@MainActivity, AboutActivity::class.java))
                R.id.navExit -> {
                    val builder = MaterialAlertDialogBuilder(this)
                    builder.setTitle("Exit")
                        .setMessage("Do you want to close app?")
                        .setPositiveButton("Yes") { _, _ ->
                            exitApplication()
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                    val customDialog = builder.create()
                    customDialog.show()

                    setDialogBtnBackground(this, customDialog)
                }
            }
            true
        }
    }

    //For requesting permission
    private fun requestRuntimePermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    13
                )
                return false
            }
        }
        //android 13 permission request
        else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_MEDIA_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_MEDIA_AUDIO),
                    13
                )
                return false
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 13) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                initializeLayout()
            } else
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    13
                )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    private fun initializeLayout() {
        search = false
        val sortEditor = getSharedPreferences("SORTING", MODE_PRIVATE)
        sortOrder = sortEditor.getInt("sortOrder", 0)

        // Inisialisasi ApiService
        apiService = ApiConfig.getApiService()
        coroutineScope.launch {
            // Coroutine code here
            withContext(Dispatchers.IO) {
                try{
                    val result = apiService.getSong()
                    val response = result.data
                    MusicListMA = response as ArrayList<DataItem>

                }catch (e : Exception){
                    Log.e("Exception",e.message!!)
                }
            }
            binding.musicRV.setHasFixedSize(true)
            binding.musicRV.setItemViewCacheSize(13)
            binding.musicRV.layoutManager = LinearLayoutManager(this@MainActivity)
            musicAdapter = MusicAdapter(this@MainActivity, MusicListMA)
            binding.musicRV.adapter = musicAdapter
            binding.totalSongs.text = "Total Songs : " + musicAdapter.itemCount
        }

//        fun makePostRequest() {
//            // Create an instance of ApiService
//            apiService = ApiConfig.getUploadSongService()
//
//            // Create a new Song object with the required data
//            val song = Song(
//                title = "My Song",
//                album = "My Album",
//                artist = "My Artist",
//                id = "",
//                url = ""
//            )
//
//            // Make the POST request
//            apiService.uploadSong(song).enqueue(object : Callback<Song> {
//                override fun onResponse(call: Call<Song>, response: Response<Song>) {
//                    if (response.isSuccessful) {
//                        val uploadSong = response.body()
//                        if (uploadSong != null) {
//                            // Handle the created song object
//                            // You can display a success message or perform additional operations
//                        }
//                    } else {
//                        // Handle unsuccessful response
//                        // You can display an error message or perform error handling logic
//                    }
//                }
//
//                override fun onFailure(call: Call<Song>, t: Throwable) {
//                    // Handle failure
//                    // You can display an error message or perform error handling logic
//                }
//            })
//        }
    }
}
