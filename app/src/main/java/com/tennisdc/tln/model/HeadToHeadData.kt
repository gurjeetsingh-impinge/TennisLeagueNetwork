package com.tennisdc.tln.model

import com.google.gson.annotations.SerializedName


data class HeadToHeadData(

    @SerializedName("responseCode") var responseCode: String? = null,
    @SerializedName("player") var player: PlayerData? = PlayerData(),
    @SerializedName("opponent") var opponent: Opponent? = Opponent(),
    @SerializedName("scores") var scores: ArrayList<Scores> = arrayListOf()

)


data class PlayerData(

    @SerializedName("player_name") var player_name: String? = null,
    @SerializedName("league_rating") var league_rating: String? = null,
    @SerializedName("player_wins") var player_wins: String? = null,
    @SerializedName("playoff_wins") var playoff_wins: String? = null,
    @SerializedName("games_won") var games_won: String? = null,
    @SerializedName("game_win_percentage") var game_win_percentage: String? = null,
    @SerializedName("avatar") var avatar: String? = null

)

data class Opponent(

    @SerializedName("oppnent_name") var oppnent_name: String? = null,
    @SerializedName("league_rating") var league_rating: String? = null,
    @SerializedName("player_wins") var player_wins: String? = null,
    @SerializedName("playoff_wins") var playoff_wins: String? = null,
    @SerializedName("games_won") var games_won: String? = null,
    @SerializedName("game_win_percentage") var game_win_percentage: String? = null,
    @SerializedName("avatar") var avatar: String? = null

)

data class Scores(

    @SerializedName("type") var type: String? = null,
    @SerializedName("match_date") var match_date: String? = null,
    @SerializedName("match_score") var match_score: String? = null,
    @SerializedName("result") var result: String? = null

)