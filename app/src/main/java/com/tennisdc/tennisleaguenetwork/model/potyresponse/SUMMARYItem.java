package com.tennisdc.tennisleaguenetwork.model.potyresponse;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class SUMMARYItem{

	@SerializedName("faded_desc")
	private List<String> fadedDesc;

	@SerializedName("sub_title")
	private String subTitle;

	@SerializedName("Title")
	private String title;

	@SerializedName("desc")
	private List<String> desc;

	public void setFadedDesc(List<String> fadedDesc){
		this.fadedDesc = fadedDesc;
	}

	public List<String> getFadedDesc(){
		return fadedDesc;
	}

	public void setSubTitle(String subTitle){
		this.subTitle = subTitle;
	}

	public String getSubTitle(){
		return subTitle;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setDesc(List<String> desc){
		this.desc = desc;
	}

	public List<String> getDesc(){
		return desc;
	}

	@Override
 	public String toString(){
		return 
			"SUMMARYItem{" + 
			"faded_desc = '" + fadedDesc + '\'' + 
			",sub_title = '" + subTitle + '\'' + 
			",title = '" + title + '\'' + 
			",desc = '" + desc + '\'' + 
			"}";
		}
}