package com.KingsStory.Application

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ZoomButton
import android.widget.ZoomButtonsController
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.selects.select

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class testActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener{
    private val mapView: MapView by lazy {
        findViewById(R.id.testmap)
    }
    private lateinit var testmap: GoogleMap
    private val viewPager:ViewPager2 by lazy {
        findViewById(R.id.houseViewPager)
    }

    private val viewPagerAdapter = HouseViewPagerAdapter()
    private val recyclerAdapter = HouseListAdapter()

    private val recyclerView: RecyclerView by lazy {
        findViewById(R.id.recycler)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync(this)
        viewPager.adapter = viewPagerAdapter
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val selectHouseModel = viewPagerAdapter.currentList[position]
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(selectHouseModel.lat,selectHouseModel.lng),15f)
                testmap.animateCamera(cameraUpdate)
            }
        })
    }

    override fun onMapReady(map: GoogleMap) {
        testmap = map
        testmap.setMaxZoomPreference(18f)
        testmap.setMinZoomPreference(10f)
        val seoul = LatLng(37.5642135, 127.0016985)
        val cameraUpdate = CameraUpdateFactory.newLatLng(seoul)
        testmap.moveCamera(cameraUpdate)

        testmap.isMyLocationEnabled = true
        val permission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1000
            )
        } else {
            Log.d("permission", "권한이 이미 있음")
        }
        getHouseListFromAPT()
        testmap.setOnMarkerClickListener(this)
        testmap.setPadding(0,0,0,500)
    }

    private fun getHouseListFromAPT() {
       val retrofit = Retrofit.Builder()
           .baseUrl("https://run.mocky.io")
           .addConverterFactory(GsonConverterFactory.create())
           .build()

        retrofit.create(HouseService::class.java).also {
            it.getHouseList()
                .enqueue(object : Callback<HouseDto>{
                    override fun onResponse(call: Call<HouseDto>, response: Response<HouseDto>) {
                        if(!response.isSuccessful) {
                            Log.d("Retrofit", "실패")
                            return
                            }
                        response.body()?.let { dto ->
                            Log.d("Retrofit", dto.toString())
                            updateMarker(dto.items)

                            viewPagerAdapter.submitList(dto.items)
                            recyclerAdapter.submitList(dto.items)
                        }

                    }

                    override fun onFailure(call: Call<HouseDto>, t: Throwable) {
                    }
                })
        }
    }
    private fun updateMarker(house: List<HouseModel>){
        house.forEach{house ->
            val markeradd = testmap.addMarker(
                MarkerOptions()
                    .position(LatLng(house.lat,house.lng))
                    .title(house.title)
            )
            markeradd.tag = house.id
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1000 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permission", "YES")
                } else {
                    Log.d("permission", "NO")
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        val selectedModel = viewPagerAdapter.currentList.firstOrNull{
            it.id == p0.tag as Int?
        }
        Log.d("testttt",selectedModel.toString())
        selectedModel?.let{
            val position = viewPagerAdapter.currentList.indexOf(it)
            viewPager.currentItem = position
        }
        return true
    }
}

