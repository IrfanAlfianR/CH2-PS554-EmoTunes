//package com.harshRajpurohit.musicPlayer
//
//import com.harshRajpurohit.musicPlayer.API.ApiConfig
//import com.harshRajpurohit.musicPlayer.API.ApiService
//
//class AppFactory {
//    companion object {
//
//        private lateinit var apiService: ApiService
//        private lateinit var uploadSongService: ApiService
//
//        // Fungsi untuk mendapatkan atau membuat instance ApiService
//        fun getApiService(): ApiService {
//            if (!::apiService.isInitialized) {
//                apiService = ApiConfig.getApiService()
//            }
//            return apiService
//        }
//
//        // Fungsi untuk mendapatkan atau membuat instance uploadSong
//        fun getUploadSongService(): ApiService {
//            if (!::uploadSongService.isInitialized) {
//                uploadSongService = ApiConfig.getuploadSong()
//            }
//            return uploadSongService
//        }
//
//        // Fungsi-fungsi lainnya sesuai kebutuhan aplikasi
//    }
//}