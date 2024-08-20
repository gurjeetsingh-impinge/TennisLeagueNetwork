package com.tennisdc.tln.model.potyresponse;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PotyResponse {

	@SerializedName("PLAYERS OF THE YEAR")
	private List<PLAYERSOFTHEYEARItem> pLAYERSOFTHEYEAR;

	@SerializedName("SUMMARY")
	private List<SUMMARYItem> sUMMARY;

	@SerializedName("TAB2")
	private String tAB2;

	@SerializedName("TAB1")
	private String tAB1;

	public void setPLAYERSOFTHEYEAR(List<PLAYERSOFTHEYEARItem> pLAYERSOFTHEYEAR){
		this.pLAYERSOFTHEYEAR = pLAYERSOFTHEYEAR;
	}

	public List<PLAYERSOFTHEYEARItem> getPLAYERSOFTHEYEAR(){
		return pLAYERSOFTHEYEAR;
	}

	public void setSUMMARY(List<SUMMARYItem> sUMMARY){
		this.sUMMARY = sUMMARY;
	}

	public List<SUMMARYItem> getSUMMARY(){
		return sUMMARY;
	}

	public void setTAB2(String tAB2){
		this.tAB2 = tAB2;
	}

	public String getTAB2(){
		return tAB2;
	}

	public void setTAB1(String tAB1){
		this.tAB1 = tAB1;
	}

	public String getTAB1(){
		return tAB1;
	}

	@Override
 	public String toString(){
		return 
			"PotyResponse{" +
			"pLAYERS OF THE YEAR = '" + pLAYERSOFTHEYEAR + '\'' + 
			",sUMMARY = '" + sUMMARY + '\'' + 
			",tAB2 = '" + tAB2 + '\'' + 
			",tAB1 = '" + tAB1 + '\'' + 
			"}";
		}
}