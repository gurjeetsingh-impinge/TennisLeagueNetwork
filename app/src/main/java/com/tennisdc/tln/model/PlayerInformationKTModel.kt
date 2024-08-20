//package com.tennisdc.tln.model
//
//import android.os.Parcelable
//import kotlinx.android.parcel.Parcelize
//
//data class PlayerInformationKTModel(
//    val responseCode: Int,
//    val user_data: UserDataBean
//)
//
//@Parcelize
//data class UserDataBean(
//        val about_me: String,
//        val age_month: Int,
//        val age_year: Int,
//        val championship: Int,
//        val championship_list: List<ChampionshipListBean>,
//        val courts: List<List<String>>,
//        val current_rating: String,
//        val fav_link: String,
//        val fav_professional_player: String,
//        val fav_shot: String,
//        val favorite_players: List<List<String>>,
//        val favourite_shorts: List<List<String>>,
//        val first_name: String,
//        val fourty_plus_leagues: List<List<String>>,
//        val fouty_plus_leage: String,
//        val game_description: String,
//        val home_court: String,
//        val last_name: String,
//        val more_info_league_rating_status: String,
//        val more_info_member_since: String,
//        val more_info_participating_city: String,
//        val more_info_player_of_the_year_point: String,
//        val more_info_playing_region: String,
//        val more_info_tln_player_rating: String,
//        val overall_league_record: String,
//        val owner: Boolean,
//        val phone_number: String,
//        val playing_preference_time: String,
//        val playing_regions: List<List<String>>,
//        val playoff_record: String,
//        var total_championship: Int? = null,
//        val user_achievement_title: String,
//        val user_city: String,
//        val user_id: Int,
//        val user_image: String,
//        val user_name: String,
//        val user_state: String,
//        val user_street_address: String,
//        val utr_rating: String
//): Parcelable
//
//@Parcelize
//data class ChampionshipListBean(
//    val id: Int,
//    val name: String
//): Parcelable