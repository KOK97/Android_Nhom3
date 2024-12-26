import android.os.Parcel
import android.os.Parcelable

data class Customer(
    val uid: String = "",
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val role: String = "",
) : Parcelable {

    constructor(parcel: Parcel) : this(
        uid = parcel.readString() ?: "",
        name = parcel.readString() ?: "",
        phone = parcel.readString() ?: "",
        email = parcel.readString() ?: "",
        role = parcel.readString() ?: "",
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(name)
        parcel.writeString(phone)
        parcel.writeString(email)
        parcel.writeString(role)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Customer> {
        override fun createFromParcel(parcel: Parcel): Customer {
            return Customer(parcel)
        }

        override fun newArray(size: Int): Array<Customer?> {
            return arrayOfNulls(size)
        }
    }
}
