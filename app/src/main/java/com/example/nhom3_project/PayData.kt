package com.example.nhom3_project

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

@SuppressLint("ParcelCreator")
data class PayData(
    val cartid: String,
    val productid: String,
    var propductname: String,
    var quality: Int,
) :Parcelable  {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cartid)
        parcel.writeString(productid)
        parcel.writeString(propductname)
        parcel.writeInt(quality)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PayData> {
        override fun createFromParcel(parcel: Parcel): PayData {
            return PayData(parcel)
        }

        override fun newArray(size: Int): Array<PayData?> {
            return arrayOfNulls(size)
        }
    }
}