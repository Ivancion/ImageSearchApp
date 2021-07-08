package com.example.imagesearchapp.data

data class UnsplashPhoto(
    val id: String,
    val description: String?,
    val urls: UnsplashPhotoUrls,
    val user: UnsplashUser
) {
    data class UnsplashPhotoUrls(
        val raw: String,
        val full: String,
        val regular: String,
        val small: String,
        val thump: String
    )

    data class UnsplashUser(
        val name: String,
        val username: String
    )
}
