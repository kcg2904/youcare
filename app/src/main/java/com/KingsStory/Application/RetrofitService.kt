package com.KingsStory.Application

import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    @POST("user/signup/")
    @FormUrlEncoded
    fun register(
        @Field("username")username: String,
        @Field("password1")password1: String,
        @Field("password2")password2: String
    ): Call<User>

    @POST("user/login/")
    @FormUrlEncoded
    fun login(
        @Field("username")username: String,
        @Field("password")password1: String
    ): Call<User>

    @POST("/companyLogin.php")
    fun test(
        @Body account:String,
        @Body pass:String
    ):Call<UserDto>
    @GET("/v3/0b928f1b-08b0-4b0c-8c60-ec62d399c42e")
    fun getCaregiverList(): Call<CaregiverDto>
}