package com.KingsStory.Application


import android.R.attr.*
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.kakao.sdk.common.model.AuthErrorCause.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView(this@MainActivity)
//        val keyHash = Utility.getKeyHash(this)
//        Log.d("Hash", keyHash



    }


    fun initView(activity: Activity){
        mauebarOnClick(activity)

    }

    fun mauebarOnClick(activity: Activity) {
        val main_bar: TextView = activity.findViewById(R.id.main_bar)
        val list_bar: TextView = activity.findViewById(R.id.list_bar)
        val upload_bar: TextView = activity.findViewById(R.id.upload_bar)
        val userinfo_bar: TextView = activity.findViewById(R.id.userinfo_bar)

        main_bar.setOnClickListener {
            startActivity(Intent(activity, MainActivity::class.java))
            finish()
        }

        list_bar.setOnClickListener {
            startActivity(Intent(activity, SeachActivity::class.java))
            finish()
        }

        upload_bar.setOnClickListener {
            startActivity(Intent(activity, UploadActivity::class.java))
            finish()
        }

        userinfo_bar.setOnClickListener {
            startActivity(Intent(activity, UserinfoActivity::class.java))
            finish()
        }


    }

}


