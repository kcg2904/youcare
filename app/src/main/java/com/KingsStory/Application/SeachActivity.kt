package com.KingsStory.Application

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class SeachActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var souelbtn : TextView
    private lateinit var busanlbtn : TextView
    private lateinit var daejeonlbtn : TextView
    private lateinit var searchbtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seach)
        init()
    }

    fun init(){
        initview(this)
        dataputExtra()
    }

    fun initview(activity: Activity){
        searchEditText = findViewById(R.id.searchEidtText)
        souelbtn = findViewById(R.id.Seoulbtn)
        busanlbtn = findViewById(R.id.Busanbtn)
        daejeonlbtn = findViewById(R.id.Daejeonbtn)
        searchbtn = findViewById(R.id.searchbtn)
    }

    fun dataputExtra(){
        val intent =  Intent(this,ListActivity::class.java)

        searchbtn.setOnClickListener {

            if(searchEditText.text != null){
                intent.putExtra("seach","${searchEditText.text}")
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this, "잘못된 검색어 입니다", Toast.LENGTH_SHORT).show()
            }
        }
        souelbtn.setOnClickListener {
            intent.putExtra("seach","서울")
            startActivity(intent)
            finish()
        }
        busanlbtn.setOnClickListener {
            intent.putExtra("seach","부산")
            startActivity(intent)
            finish()
        }
        daejeonlbtn.setOnClickListener {
            intent.putExtra("seach","대전")
            startActivity(intent)
            finish()
        }
    }
}