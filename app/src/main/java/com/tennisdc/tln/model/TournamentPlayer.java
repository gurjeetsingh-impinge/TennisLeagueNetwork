package com.tennisdc.tln.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created on 21-11-2017.
 */

public class TournamentPlayer {

	@SerializedName("name")
	public String Name;

	@SerializedName("active")
	public String Active;

	@SerializedName("phone")
	public String Phone;

	@SerializedName("email")
	public String Email;

	@SerializedName("winner")
	public boolean Winner;

	@SerializedName("contact")
	public boolean Contact;

	@SerializedName("image")
	public String Image;

	@SerializedName("profile_active")
	public Boolean Profile_active = false;

	@SerializedName("player_id")
	public String Player_id;

	public Boolean getProfile_active() {
		return Profile_active;
	}

	public void setProfile_active(Boolean profile_active) {
		Profile_active = profile_active;
	}

	public String getPlayer_id() {
		return Player_id;
	}

	public void setPlayer_id(String player_id) {
		Player_id = player_id;
	}

	public String getImage() {
		return Image;
	}

	public void setImage(String image) {
		Image = image;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getActive() {
		return Active;
	}

	public void setActive(String active) {
		Active = active;
	}

	public String getPhone() {
		return Phone;
	}

	public void setPhone(String phone) {
		Phone = phone;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public boolean isWinner() {
		return Winner;
	}

	public void setWinner(boolean winner) {
		Winner = winner;
	}

	public boolean isContact() {
		return Contact;
	}

	public void setContact(boolean contact) {
		Contact = contact;
	}
}
