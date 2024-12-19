import android.os.Parcel
import android.os.Parcelable
import java.util.Date

data class Bills(
    val id: String = "",
    var cartitemid: String = "",
    var userid: String = "",
    var addressid: String = "",
    var paymentid: String = "",
    var totalpayment: Int = 0,
    var creationdate: Date = Date(),
    val products: MutableList<Products> = mutableListOf()
) : Parcelable {

    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",
        cartitemid = parcel.readString() ?: "",
        userid = parcel.readString() ?: "",
        addressid = parcel.readString() ?: "",
        paymentid = parcel.readString() ?: "",
        totalpayment = parcel.readInt(),
        creationdate = Date(parcel.readLong()),
        products = parcel.createTypedArrayList(Products.CREATOR) ?: mutableListOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(cartitemid)
        parcel.writeString(userid)
        parcel.writeString(addressid)
        parcel.writeString(paymentid)
        parcel.writeInt(totalpayment)
        parcel.writeLong(creationdate.time)
        parcel.writeTypedList(products)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Bills> {
        override fun createFromParcel(parcel: Parcel): Bills {
            return Bills(parcel)
        }

        override fun newArray(size: Int): Array<Bills?> {
            return arrayOfNulls(size)
        }
    }
}
