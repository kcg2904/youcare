package com.KingsStory.Application

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.kakao.sdk.user.UserApiClient

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val kakao_logout_button: Button = findViewById(R.id.kakao_logout_button)
        val kakao_unlink_button: Button = findViewById(R.id.kakao_unlink_button)
        val id : TextView = findViewById(R.id.id)
        val nickname : TextView = findViewById(R.id.nickname)
        val profileimage : TextView = findViewById(R.id.profileimage_url)
        val thumbnailimage : TextView = findViewById(R.id.thumbnailimage_url)

        UserApiClient.instance.me { user, error ->
            id.text = "회원번호 : ${user?.id}"
            nickname.text = "닉네임 : ${user?.kakaoAccount?.profile?.nickname}"
            profileimage.text = "프로필 링크 : ${user?.kakaoAccount?.profile?.profileImageUrl}"
            thumbnailimage.text = "썸네일 링크 : ${user?.kakaoAccount?.profile?.thumbnailImageUrl}"
        }

        kakao_logout_button.setOnClickListener {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Toast.makeText(this@SecondActivity, "로그아웃 실패 $error", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@SecondActivity, "로그아웃 성공", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@SecondActivity, MainActivity::class.java)
                    finish()
                    startActivity(intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP))
                }
            }
        }

        kakao_unlink_button.setOnClickListener {
            UserApiClient.instance.unlink { error ->
                if (error != null) {
                    Toast.makeText(this, "회원 탈퇴 실패 $error", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(this, "회원 탈퇴 성공", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP))
                    finish()
                }
            }
        }

    }


}