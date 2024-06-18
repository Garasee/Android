package com.example.garasee.data.pref

data class PostPredictRequest (
    val brand: String,
    val isNew: Boolean,
    val year: Number,
    val engineCapacity: Float,
    val peakPower: Float,
    val peakTorque: Float,
    val injection: String,
    val length: Float,
    val width: Float,
    val wheelBase: Float,
    val doorAmount: Int,
    val seatCapacity: Int
)

