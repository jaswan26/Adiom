package com.homeautomation.adiom.models


data class RoomModel (
        val roomName : String = "",
        var deviceList : List<DeviceModel>
){
    companion object {
        var roomList:MutableList<RoomModel> = mutableListOf<RoomModel>()

    }
}
data class DeviceModel(
        val deviceName : String = "",
        val status : Int = -1
)


