package com.tennisdc.tln.model

data class MatchesPlayedModel(
    val description: String,
    val heading1: String,
    val heading2: String,
    val main_heading: String,
    val matches_played: String,
    val matches_played_globally: String,
    val nationwide_players: List<Player>,
    val players: List<Player>,
    val responseCode: String,
    val winner: String
)

data class Player(
    val index: Int,
    val matches: Int,
    val name: String
)