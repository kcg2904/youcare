package com.KingsStory.Application

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem


class ClusterItme(     lat: Double,
                       lng: Double,
                       title: String,
                       snippet: String,
                       icon: BitmapDescriptor? = null
) : ClusterItem {

    private val position: LatLng
    private val title: String
    private val snippet: String
    private var icon : BitmapDescriptor? = null



    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String? {
        return title
    }

    override fun getSnippet(): String? {
        return snippet
    }
    fun getIcon():BitmapDescriptor?{
        return icon
    }


    override fun equals(other: Any?): Boolean {
        if(other is ClusterItme){
            return (other.position.latitude == position.latitude
                    && other.position.longitude == position.longitude
                    && other.title == title
                    && other.snippet == snippet
                    && other.icon == icon
                    )
        }

        return false
    }

    override fun hashCode(): Int {
        var hash = position.latitude.hashCode() * 31
        hash = hash * 31 + position.longitude.hashCode()
        hash = hash * 31 + title.hashCode()
        hash = hash * 31 + snippet.hashCode()
        hash = hash * 31 + icon.hashCode()
        return hash

    }

    init {
        position = LatLng(lat, lng)
        this.title = title
        this.snippet = snippet
    }
}