package com.KingsStory.Application

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class ListActivity : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMarkerClickListener {
    private lateinit var mMap: GoogleMap
    private lateinit var mapView: MapView

    private lateinit var viewPager:ViewPager2
    private lateinit var recyclerView: RecyclerView
    private val viewPagerAdapter = ViewPagerListAdapter()
    private val recyclerAdapter = CaregiverListAdapter()

    private lateinit var marker_root_view: View
    private lateinit var tv_marker: TextView
    private lateinit var backbtn : ImageView
    private lateinit var bottomsheetText : TextView
    private lateinit var seachdata :String
    private lateinit var toolbartitleTextView : TextView

    //TODO: 검색 후 지도가 나오면서 해당 주소 값을 포함하는 데이터만 지도에 표시
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        init()
        mapView.onCreate(savedInstanceState)

    }
    // 주소를 위경도값으로 변환
    fun test(test :String){
        val geocoder:Geocoder = Geocoder(this)
        val list:List<Address>

        val str : String = test

        list = geocoder.getFromLocationName(str,10)
        if (list != null) {
            if (list.size == 0) {
                Toast.makeText(this, "주소를 찾을수 없습니다", Toast.LENGTH_SHORT).show()
            } else {
                // 해당되는 주소로 인텐트 날리기
                val addr : Address = list.get(0);
                val lat: Double  = addr.getLatitude();
                val lon :Double = addr.getLongitude();

                Log.d("cccccccccc","${lat},${lon}")
            }
        }
    }
    private fun init() {
        initView(this)
        mapView.getMapAsync(this)
        viewPager.adapter = viewPagerAdapter
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        viewPagerPageChange()
        setbackbtnOnClickListener(backbtn,this@ListActivity)
        seachdata =  intent.getStringExtra("seach").toString()
        toolbartitleTextView.text = seachdata + "의 요양사"
    }
    private fun initView(activity: Activity){
        mapView = activity.findViewById(R.id.mapView)
        viewPager =  activity.findViewById(R.id.ViewPagerList)
        recyclerView = activity.findViewById(R.id.recycler)
        backbtn = activity.findViewById(R.id.backbtn)
        bottomsheetText = activity.findViewById(R.id.bottomSheetTitleTextView)
        toolbartitleTextView = activity.findViewById(R.id.toolbartitleTextView)
    }

    //뒤로가기 버튼 클릭이벤트
    private fun setbackbtnOnClickListener(view: View, activity: Activity,  ){
        view.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            finish()

        }
    }
    //뷰페이저 페이지 넘길시 해당하는 마커로 카메라 이동
    private fun viewPagerPageChange(){
        viewPager.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val selectHouseModel = viewPagerAdapter.currentList[position]
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(selectHouseModel.lat,selectHouseModel.lng),15f)
                mMap.animateCamera(cameraUpdate)
            }
        })
    }
    //맵을 그림
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMaxZoomPreference(18f)
        mMap.setMinZoomPreference(10f)
        mMap.setOnMarkerClickListener(this)
        mMap.setPadding(0,0,0,500)
        //setCustomMarkerView()
        MyLocationEnabled()
        getData()

        val seoul = LatLng(37.5642135, 127.0016985)
//        mMap.addMarker(
//            MarkerOptions()
//                .position(seoul)
//                .title("서울")
//                .icon(
//                    BitmapDescriptorFactory.fromBitmap(
//                        createDrawableFromView(
//                            this, marker_root_view
//                        )
//                    )
//                )
//        )
        mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul))


    }
// 데이터를 받아오는 부분
    private fun getData(){
        (application as GlobalApplication).service2.also {
            it.getCaregiverList()
                .enqueue(object : Callback<CaregiverDto>{
                    override fun onResponse(
                        call: Call<CaregiverDto>,
                        response: Response<CaregiverDto>
                    ) {
                        if(!response.isSuccessful){
                            Log.e("Retrofit", "연결실패")
                            return
                        }
                        response.body()?.let { dto ->
                            Log.d("Retrofit", dto.toString())
                            //TODO: db에서 검색 부분이 조건으로 들어가서 해당하는 데이터만 가져와야함 이게 안된다면 전체 데이터에서 필요한 값만 가져오게 해야함
                            dto.items.forEach{test ->
                                if(test.address.contains(seachdata)){
                                    updateMarker(dto.items)
                                    bottomsheetText.text = dto.items.size.toString() + " 명의 요양사"
                                    viewPagerAdapter.submitList(dto.items)
                                    recyclerAdapter.submitList(dto.items)
                                }else{
                                    Toast.makeText(this@ListActivity, "해당하는 주소에 데이터가 없습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }

                        }
                    }

                    override fun onFailure(call: Call<CaregiverDto>, t: Throwable) {

                    }
                })
        }
    }

    //데이터 가져와서 마커 생성
    private fun updateMarker(caregiver: List<CaregiverModel>){
        caregiver.forEach{caregiver ->
            val markeradd = mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(caregiver.lat,caregiver.lng))
                    .title(caregiver.name)
            )
            markeradd.tag = caregiver.id
            //Log.d("cccccccccc","${caregiver.address}")
           // test(caregiver.address)
        }
    }
    // 내위치 버튼 생성 및 권한확인 -> 권한이 없을시 권한 요청
    private fun MyLocationEnabled(){
        mMap.isMyLocationEnabled = true

        val permission = ContextCompat.checkSelfPermission(
            this@ListActivity,
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
    }

    //권한확인하고 결과값
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

    //클러스터 아이템 추가
//    private fun addItems() {
//
//        var lat = 37.56393856479181
//        var lng = 126.93852845580885
//        var lat2 = 37.575197
//        var lng2 = 126.927741
//
//        for (i in 0..9) {
//            val offset = i / 60000.0
//            lat += offset
//            lng += offset
//            val offsetItem =
//                ClusterItme(
//                    lat, lng, "Title $i", "Snippet $i",
//                    BitmapDescriptorFactory.fromBitmap(
//                        createDrawableFromView(
//                            this, marker_root_view
//                        )
//                    )
//                )
//            clusterManager.addItem(offsetItem)
//        }
//
//        for (i in 0..5) {
//            val offset = i / 6000.0
//            lat2 += offset
//            lng2 += offset
//            val offsetItem =
//                ClusterItme(
//                    lat2, lng2, "Title $i", "Snippet $i", BitmapDescriptorFactory.fromBitmap(
//                        createDrawableFromView(
//                            this, marker_root_view
//                        )
//                    )
//                )
//            clusterManager.addItem(offsetItem)
//        }
//    }

    //마커 아이콘 커스텀
    private fun setCustomMarkerView() {
        marker_root_view = LayoutInflater.from(this).inflate(R.layout.maker, null)
        tv_marker = marker_root_view.findViewById(R.id.tv_marker) as TextView
    }

    // View를 Bitmap으로 변환
    private fun createDrawableFromView(context: Context, view: View): Bitmap? {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        view.setLayoutParams(
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(
            view.getMeasuredWidth(),
            view.getMeasuredHeight(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }
    //
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

//클러스터 마커 랜더러
//private class MarkerRenderer(
//    context: Context?,
//    map: GoogleMap?,
//    clusterManager: ClusterManager<ClusterItme>
//) :
//    DefaultClusterRenderer<ClusterItme>(context, map, clusterManager) {
//    override fun onClustersChanged(clusters: Set<Cluster<ClusterItme>>) {
//        super.onClustersChanged(clusters)
//
//    }
//
//    override fun setOnClusterItemClickListener(listener: OnClusterItemClickListener<ClusterItme>) {
//        super.setOnClusterItemClickListener(listener)
//    }
//
//    override fun onBeforeClusterItemRendered(item: ClusterItme, markerOptions: MarkerOptions) {
//        super.onBeforeClusterItemRendered(item, markerOptions)
//        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_background))
//    }
//}