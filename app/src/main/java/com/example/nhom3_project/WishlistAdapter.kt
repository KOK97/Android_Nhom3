import com.example.nhom3_project.R
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.nhom3_project.CartActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class WishlistAdapter(
    private val context: Context,
    private val productsList: MutableList<Products>,
    private var WishlList: MutableList<Wishlist> = mutableListOf()
) : RecyclerView.Adapter<WishlistAdapter.ProductViewHolder>() {

    private lateinit var dbRef: DatabaseReference

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val currentUser = firebaseAuth.currentUser
    private val uid = currentUser?.uid.toString()

    init {
        getDataWislist()
    }

    // ViewHolder class
    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView = view.findViewById(R.id.tvWNamePro)
        val productPrice: TextView = view.findViewById(R.id.tvWprice)
        val productImage: ImageView = view.findViewById(R.id.ivWImgPro)
        val btnAddtoCart: AppCompatImageButton  = view.findViewById(R.id.ibtnWAddtoCart)
        val ibtnWDelete: AppCompatImageButton  = view.findViewById(R.id.ibtnWDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.wishlist_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productsList[position]

        holder.productName.text = product.name
        holder.productPrice.text = "${product.price} VND"

        val imageResId = context.resources.getIdentifier(product.imageUrl, "drawable", context.packageName)
        holder.productImage.setImageResource(if (imageResId != 0) imageResId else R.drawable.baseline_hide_image_24)


        holder.btnAddtoCart.setOnClickListener {
            val intent = Intent(context, CartActivity::class.java)
            intent.putExtra("productClick", product.id)
            context.startActivity(intent)
        }
        holder.ibtnWDelete.setOnClickListener{
            if (WishlList.isNotEmpty()){
                for (wis in WishlList){
                    if (wis.productid == product.id){
                        updateWishlist(wis.id)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = productsList.size

    private fun getDataWislist() {
        dbRef = FirebaseDatabase.getInstance().reference

        dbRef.child("Wishlist").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                WishlList.clear()
                for (cartSnapshot in snapshot.children) {
                    val userid = cartSnapshot.child("userid").getValue(String::class.java) ?: ""
                  if (userid == uid){
                      val id = cartSnapshot.child("id").getValue(String::class.java) ?: ""
                      val productid = cartSnapshot.child("productid").getValue(String::class.java) ?: "0"
                      val wishlist = Wishlist(id, userid, productid)
                      WishlList.add(wishlist)
                  }
                }
                notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("WishlistAdapter", "Lỗi tải dữ liệu yêu thích: ${error.message}")
            }
        })
    }
    private fun updateWishlist(WishlistId: String) {
        dbRef.child("Wishlist").child(WishlistId.toString()).child("selected").setValue(false)
            .addOnSuccessListener {
                Log.d("WishlistAdapter", "Cập nhật Wishlist thành công: $WishlistId")
            }
            .addOnFailureListener { error ->
                Log.e("WishlistAdapter", "Lỗi cập nhật: ${error.message}")
            }
    }
}
