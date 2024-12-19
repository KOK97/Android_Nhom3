import android.os.Parcel
import android.os.Parcelable

data class Products(
    val id: String = "",
    var name: String = "",
    var category: String = "",
    var type: String = "",
    var price: Int = 0,
    var quantity: Int =0,
    var desc: String = "",
    var imageUrl: String = "",
    var buyCount: Int = 0,
    var isSelected: Boolean = false
) : Parcelable {

    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",
        name = parcel.readString() ?: "",
        category = parcel.readString() ?: "",
        type = parcel.readString() ?: "",
        price = parcel.readInt(),
        quantity = parcel.readInt(),
        desc = parcel.readString() ?: "",
        imageUrl = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(category)
        parcel.writeString(type)
        parcel.writeInt(price)
        parcel.writeInt(quantity)
        parcel.writeString(desc)
        parcel.writeString(imageUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Products> {
        override fun createFromParcel(parcel: Parcel): Products {
            return Products(parcel)
        }

        override fun newArray(size: Int): Array<Products?> {
            return arrayOfNulls(size)
        }
    }
}
