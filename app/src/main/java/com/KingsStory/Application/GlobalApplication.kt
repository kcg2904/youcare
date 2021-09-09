package com.KingsStory.Application

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.kakao.sdk.common.KakaoSdk
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GlobalApplication : Application() {

    lateinit var service: RetrofitService // 로그인 서비스
    lateinit var service2: RetrofitService // 요양사 리스트 서비스

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        KakaoSdk.init(this, "8b7263b57f91d3c64f58662e68b813b1")

        createRetrofit()
        getCaregiverListFromAPT()
    }

    fun createRetrofit(){
        val header = Interceptor{
            val original = it.request()
            if(checkIsLogin()){
                getUserToken()?.let{ token ->
                    val requeset = original.newBuilder()
                        .header("Authorization", "token " + token)
                        .build()
                    it.proceed(requeset)
                }
            }else {
                it.proceed(original)
            }
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(header)
            .addNetworkInterceptor(StethoInterceptor())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://mellowcode.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        service = retrofit.create(RetrofitService::class.java)
    }
    fun test(){
        val retrofit = Retrofit.Builder()
            .baseUrl("http://mellowcode.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    fun getCaregiverListFromAPT() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service2 = retrofit.create(RetrofitService::class.java)
    }
    fun checkIsLogin(): Boolean{
        val sp = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        val token = sp.getString("login_sp","null")
        if (token != "null") return true
        else return false
    }
    fun getUserToken(): String?{
        val sp = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        val token = sp.getString("login_sp","null")
        if (token != "null") return "null"
        else return token
    }
}

