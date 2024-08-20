package com.tennisdc.tln.model

data class PreOrderResponse(
    val response: Response,
    val responseCode: String,
    val responseMessage: String
) {
    data class Response(val message: String, val data: Data) {
        data class Data(val title: String)
    }
}