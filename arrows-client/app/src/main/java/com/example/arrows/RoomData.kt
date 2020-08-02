package com.example.arrows

import java.io.Serializable

data class RoomData (
    val mostRecentArrowId: Long,
    val createdOn : Long,
    val id : Long,
    val createdById : Long,
    val title : String,
    val numUpvotes : Int,
    val numDownvotes : Int,
    val currentListeners : Int,
    val peakListeners : Int,
    val roomCode : String,
    val active : Boolean,
    val lastArrowId : Long
): Serializable{
    constructor() : this(-1, -1, 0,
        -1, "", -1, -1, -1,
        -1, "", false, -1)
}