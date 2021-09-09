package com.KingsStory.Application

import retrofit2.Call
import retrofit2.http.GET

interface HouseService {
    @GET("/v3/cdf11e39-807b-47f3-b77e-cf06a73b6652")
    fun getHouseList(): Call<HouseDto>
}