import android.os.Parcel
import android.os.Parcelable

data class Wishlist(
    val id: String = "",
    var userid: String = "",
    var productid: String = "",
    var selected: Boolean = false
)