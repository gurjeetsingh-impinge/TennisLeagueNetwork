package com.tennisdc.tln.model;

public class HomeItemModel {

    String mTitle;
    String mImageUrl;
    String mBadgeCount;
    int mImage;
    int mID;

    public HomeItemModel(int mID, String mBadgeCount, String mTitle,String mImageUrl, int mImage){
        this.mTitle = mTitle;
        this.mImage = mImage;
        this.mID = mID;
        this.mImageUrl = mImageUrl;
        this.mBadgeCount = mBadgeCount;
    }
    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public int getmImage() {
        return mImage;
    }

    public void setmImage(int mImage) {
        this.mImage = mImage;
    }

    public int getmID() {
        return mID;
    }

    public void setmID(int mID) {
        this.mID = mID;
    }

    public String getmBadgeCount() {
        return mBadgeCount;
    }

    public void setmBadgeCount(String mBadgeCount) {
        this.mBadgeCount = mBadgeCount;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }
}
