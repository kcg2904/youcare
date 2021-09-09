package com.KingsStory.Application

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    lateinit var login_btn: Button
    lateinit var useridText: EditText
    lateinit var userpwText: EditText
//    lateinit var kakaologin_btn: ImageButton
//    lateinit var googlelogin_btn : SignInButton
    lateinit var gso : GoogleSignInOptions
    lateinit var mGoogleSignInClient : GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        initView(this@LoginActivity)

        login_btn.setOnClickListener {
            login()
        }
//        kakaologin_btn.setOnClickListener {
//            kakaologin()
//        }
//        googlelogin_btn.setOnClickListener {
//            googlelogin()
//        }
    }

    fun googlelogin(){
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent,200)
    }

    fun login() {
        val account = useridText.text.toString()
        val pass = userpwText.text.toString()
        (application as GlobalApplication).service.login(
            account, pass
        ).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    var user = response.body()
                    val token = user!!.token!!
                    if(token.contains("a")){
                        Log.d("token" , "" +token)
                    }
                    saveUserToken("token "+token, this@LoginActivity)
                    (application as GlobalApplication).createRetrofit()
                    Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@LoginActivity, UserinfoActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "로그인 실패", Toast.LENGTH_LONG).show()

                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d("testt","$t")
                Toast.makeText(this@LoginActivity, "접속실패", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun kakaologin() {
        //로그인 부분
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                when {
                    error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                        Toast.makeText(this, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                        Toast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                        Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT)
                            .show()
                    }
                    error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                        Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                        Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                        Toast.makeText(this, "설정이 올바르지 않음(android key hash)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.ServerError.toString() -> {
                        Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                        Toast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
                    }
                    else -> { // Unknown
                        Toast.makeText(this, "기타 에러", Toast.LENGTH_SHORT).show()
                        Log.d("errorrr", "" + error)
                    }
                }
            } else if (token != null) {
                Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                UserApiClient.instance.me { user, error ->
                    saveUserToken("kakao "+user?.id.toString(),this@LoginActivity)
                }
                startActivity(Intent(this@LoginActivity, UserinfoActivity::class.java))
                finish()
            }
        }

            if (LoginClient.instance.isKakaoTalkLoginAvailable(this)) {
                LoginClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else {
                LoginClient.instance.loginWithKakaoAccount(this, callback = callback)
            }


    }

    fun saveUserToken(token: String, activity: Activity) {
        val sp = activity.getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString("login_sp", ""+token)
        editor.commit()
    }

    fun initView(activity: Activity) {
//        kakaologin_btn = activity.findViewById(R.id.kakao_login_button)
        login_btn = activity.findViewById(R.id.login_button)
        useridText = activity.findViewById(R.id.user_id)
        userpwText = activity.findViewById(R.id.user_pw)
//        googlelogin_btn = activity.findViewById(R.id.google_login_button)
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 200) {
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val acct: GoogleSignInAccount? = task.getResult(ApiException::class.java)
            if (acct != null) {
                val personName = acct.displayName
                val personGivenName = acct.givenName
                val personFamilyName = acct.familyName
                val personEmail = acct.email
                val personId = acct.id
                val personPhoto: Uri? = acct.photoUrl
                saveUserToken("google "+personId,this@LoginActivity)
                startActivity(Intent(this@LoginActivity,UserinfoActivity::class.java))
                finish()
            }
        } catch (e: ApiException) {
            Log.e("test", "signInResult:failed code=" + e.statusCode)
        }
    }
}
