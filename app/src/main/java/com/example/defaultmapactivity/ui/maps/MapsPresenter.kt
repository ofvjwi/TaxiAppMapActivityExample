package com.example.defaultmapactivity.ui.maps

import android.util.Log
import com.example.defaultmapactivity.data.network.NetworkService
import com.example.defaultmapactivity.utils.Constants
import com.example.defaultmapactivity.utils.WebSocket
import com.example.defaultmapactivity.utils.WebSocketListener
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject

class MapsPresenter(private val networkService: NetworkService) : WebSocketListener {

    companion object {
        private const val TAG = "MapsPresenter"
    }

    private var view: MapsView? = null
    private lateinit var webSocket: WebSocket

    fun onAttach(view: MapsView?) {
        this.view = view
        webSocket = networkService.createWebSocket(this)
        webSocket.connect()
    }

    fun onDetach() {
        webSocket.disconnect()
        view = null
    }

    fun requestNearbyCabs(latLng: LatLng) {
        val jsonObject = JSONObject()
        jsonObject.put(Constants.TYPE, Constants.NEAR_BY_CABS)
        jsonObject.put(Constants.LAT, latLng.latitude)
        jsonObject.put(Constants.LNG, latLng.longitude)
        webSocket.sendMessage(jsonObject.toString())
    }

    fun requestCab(pickUpLatLng: LatLng, dropLatLng: LatLng) {

        // 41.343681,69.203775
        // 41.326572,69.228903

        val l1 = LatLng(41.343681,69.203775)
        val l2 = LatLng(41.326572,69.228903)

        val jsonObject = JSONObject()
        jsonObject.put("type", "requestCab")
        jsonObject.put("pickUpLat", l1.latitude)
        jsonObject.put("pickUpLng", l1.longitude)
        jsonObject.put("dropLat", l2.latitude)
        jsonObject.put("dropLng", l2.longitude)
        webSocket.sendMessage(jsonObject.toString())
    }

    private fun handleOnMessageNearbyCabs(jsonObject: JSONObject) {
        val nearbyCabLocations = arrayListOf<LatLng>()
        val jsonArray = jsonObject.getJSONArray(Constants.LOCATIONS)
        for (i in 0 until jsonArray.length()) {
            val lat = (jsonArray.get(i) as JSONObject).getDouble(Constants.LAT)
            val lng = (jsonArray.get(i) as JSONObject).getDouble(Constants.LNG)
            val latLng = LatLng(lat, lng)
            nearbyCabLocations.add(latLng)
        }
        view?.showNearbyCabs(nearbyCabLocations)
    }

    override fun onConnect() {
        Log.d(TAG, "onConnect")
    }

    override fun onMessage(data: String) {
        Log.d(TAG, "onMessage data : $data")
        val jsonObject = JSONObject(data)
        when (jsonObject.getString(Constants.TYPE)) {
            Constants.NEAR_BY_CABS -> {
                handleOnMessageNearbyCabs(jsonObject)
            }
            Constants.CAB_BOOKED -> {
                view?.informCabBooked()
            }
            Constants.PICKUP_PATH, Constants.TRIP_PATH -> {
                val jsonArray = jsonObject.getJSONArray("path")
                val pickUpPath = arrayListOf<LatLng>()
                for (i in 0 until jsonArray.length()) {
                    val lat = (jsonArray.get(i) as JSONObject).getDouble("lat")
                    val lng = (jsonArray.get(i) as JSONObject).getDouble("lng")
                    val latLng = LatLng(lat, lng)
                    pickUpPath.add(latLng)
                }
                view?.showPath(pickUpPath)
            }
            Constants.LOCATION -> {
                val latCurrent = jsonObject.getDouble("lat")
                val lngCurrent = jsonObject.getDouble("lng")
                view?.updateCabLocation(LatLng(latCurrent, lngCurrent))
            }
            Constants.CAB_IS_ARRIVING -> {
                view?.informCabIsArriving()
            }
            Constants.CAB_ARRIVED -> {
                view?.informCabArrived()
            }
            Constants.TRIP_START -> {
                view?.informTripStart()
            }
            Constants.TRIP_END -> {
                view?.informTripEnd()
            }
        }
    }

    override fun onDisconnect() {
        Log.d(TAG, "onDisconnect")
    }

    override fun onError(error: String) {
        Log.d(TAG, "onError : $error")
        val jsonObject = JSONObject(error)
        when (jsonObject.getString(Constants.TYPE)) {
            Constants.ROUTES_NOT_AVAILABLE -> {
                view?.showRoutesNotAvailableError()
            }
            Constants.DIRECTION_API_FAILED -> {
                view?.showDirectionApiFailedError(
                    "Direction API Failed : " + jsonObject.getString(
                        Constants.ERROR
                    )
                )
            }
        }
    }

}
