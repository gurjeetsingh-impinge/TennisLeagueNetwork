package com.tennisdc.tln.model.potyresponse;

import com.google.gson.annotations.SerializedName;

public class PLAYERSOFTHEYEARItem{

	@SerializedName("profile_image")
	private String profileImage;

	@SerializedName("champion_cup_image")
	private String championCupImage;

	@SerializedName("referrals")
	private String referrals;

	@SerializedName("player_name")
	private String playerName;

	@SerializedName("points")
	private String points;

	public void setProfileImage(String profileImage){
		this.profileImage = profileImage;
	}

	public String getProfileImage(){
		return profileImage;
	}

	public void setChampionCupImage(String championCupImage){
		this.championCupImage = championCupImage;
	}

	public String getChampionCupImage(){
		return championCupImage;
	}

	public void setReferrals(String referrals){
		this.referrals = referrals;
	}

	public String getReferrals(){
		return referrals;
	}

	public void setPlayerName(String playerName){
		this.playerName = playerName;
	}

	public String getPlayerName(){
		return playerName;
	}

	public void setPoints(String points){
		this.points = points;
	}

	public String getPoints(){
		return points;
	}

	@Override
 	public String toString(){
		return 
			"PLAYERSOFTHEYEARItem{" + 
			"profile_image = '" + profileImage + '\'' + 
			",champion_cup_image = '" + championCupImage + '\'' + 
			",referrals = '" + referrals + '\'' + 
			",player_name = '" + playerName + '\'' + 
			",points = '" + points + '\'' + 
			"}";
		}
}