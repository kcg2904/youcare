package com.KingsStory.Application


data class CaregiverModel(
    val id: Int,
    val name: String,
    val age : String,
    val gender : String,
    val address : String,
    val lat: Double,
    val lng: Double,
    val imgUrl: String
)