package com.tennisdc.tln.model

data class OurPlayerDetailMode(
    val id: String,
    val name: String,
    val gender : String,
    val avatar: String,
    val season_record: String,
    val home_court: String,
    val player_region: String,
    val member_since: String
)

data class OurPlayer(
    val responseCode: String,
    val players: ArrayList<OurPlayerDetailMode>
)
