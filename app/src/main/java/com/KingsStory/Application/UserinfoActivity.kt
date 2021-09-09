package com.KingsStory.Application

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.kakao.sdk.user.UserApiClient

class UserinfoActivity : AppCompatActivity() {
    lateinit var loginbtn: TextView
    lateinit var hello_user: TextView
    lateinit var shar : SharedPreferences
    lateinit var mGoogleSignInClient : GoogleSignInClient
    lateinit var gso : GoogleSignInOptions
    var id : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userinfo)
        initView(this)
        kakaologincheck()

        loginbtn.setOnClickListener {
            if(id != null && id != "null") {
                logout()
            }else{
                startActivity(Intent(this@UserinfoActivity,LoginActivity::class.java))
                finish()
            }
        }
    }

    fun logout(){
        if(id!!.contains("google")){
            googlelogout()
        }else if(id!!.contains("token")){
            (application as GlobalApplication).createRetrofit()
        }else if(id!!.contains("kakao")){
            kakaologout()
        }
        val editor = shar.edit()
        editor.putString("login_sp","null")
        editor.commit()
        startActivity(Intent(this@UserinfoActivity, this@UserinfoActivity::class.java))
        finish()
    }
    private fun googlelogout() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this, OnCompleteListener<Void?> {
                Toast.makeText(this@UserinfoActivity, "구글 로그아웃 성공", Toast.LENGTH_LONG).show()
            })
    }
    fun kakaologout(){
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
           if(tokenInfo != null){
                UserApiClient.instance.logout { error->
                    if(error !=null){
                        Toast.makeText(this@UserinfoActivity, "로그아웃 실패", Toast.LENGTH_LONG).show()
                    }else {
                        Toast.makeText(this@UserinfoActivity, "로그아웃 성공", Toast.LENGTH_LONG).show()

                    }
                }
            }
        }
    }

    fun kakaologincheck() {
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                //Toast.makeText(this, "토큰 정보보기 실패", Toast.LENGTH_LONG).show()
                if (id != null && id != "null"){
                    if(!id!!.contains("google")) {
                        hello_user.text = "안녕하세요 ${id}님"
                        loginbtn.text = "로그아웃 >"
                    }else{
                        val account = GoogleSignIn.getLastSignedInAccount(this)
                        hello_user.text = "안녕하세요 ${account?.displayName}님"
                        loginbtn.text = "로그아웃 >"
                    }
                }
            } else if (tokenInfo != null) {
                Toast.makeText(this, "토큰 정보 보기 성공", Toast.LENGTH_LONG).show()
                UserApiClient.instance.me { user, error ->
                    hello_user.text = "안녕하세요 ${user?.kakaoAccount?.profile?.nickname}님"
                    loginbtn.text = "로그아웃 >"
                }
            }
        }
    }

    fun initView(activity: Activity) {
        loginbtn = activity.findViewById(R.id.loginout_button)
        hello_user = activity.findViewById(R.id.hello_user)
        shar = getSharedPreferences("login_sp", MODE_PRIVATE)
        id = shar.getString("login_sp","null")
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
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
            startActivity(Intent(activity, ListActivity::class.java))
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