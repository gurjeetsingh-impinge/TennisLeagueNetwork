package com.tennisdc.tln.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class LeagueSwagItems(val response:Response){
data class Response(
    val message: String,
    val data: Data) {
    data class Data(
        val page_heading: String,
        val page_title: String,
        val show_pre_order_button: Boolean,
        val shipping_address1: String,
        val shipping_address2: String,
        val swag_items: List<SwagItems>
    ) {
        data class SwagItems(
            val id: Long,
            val name: String,
            val description: String,
            val cost: String,
            val shipping_cost: String,
            val avatar: String,
            val size: Size
        ) : Parcelable {
            constructor(parcel: Parcel) : this(
                parcel.readLong(),
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readParcelable(Size::class.java.classLoader)!!
            ) {
            }

            data class Size(val options: List<String>, val default: String) : Parcelable {
                constructor(parcel: Parcel) : this(
                    parcel.createStringArrayList()!!,
                    parcel.readString()!!
                ) {
                }

                override fun writeToParcel(parcel: Parcel, flags: Int) {
                    parcel.writeStringList(options)
                    parcel.writeString(default)
                }

                override fun describeContents(): Int {
                    return 0
                }

                companion object CREATOR : Parcelable.Creator<Size> {
                    override fun createFromParcel(parcel: Parcel): Size {
                        return Size(parcel)
                    }

                    override fun newArray(size: Int): Array<Size?> {
                        return arrayOfNulls(size)
                    }
                }
            }

            override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeLong(id)
                parcel.writeString(name)
                parcel.writeString(description)
                parcel.writeString(cost)
                parcel.writeString(shipping_cost)
                parcel.writeString(avatar)
                parcel.writeParcelable(size, flags)
            }

            override fun describeContents(): Int {
                return 0
            }

            companion object CREATOR : Parcelable.Creator<SwagItems> {
                override fun createFromParcel(parcel: Parcel): SwagItems {
                    return SwagItems(parcel)
                }

                override fun newArray(size: Int): Array<SwagItems?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}





}