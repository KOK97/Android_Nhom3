package com.example.nhom3_project

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CommentHistoryActivity : AppCompatActivity() {
    private lateinit var rvUserComments: RecyclerView
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var ivBack: ImageView
    private lateinit var ivEmpty: ImageView
    private lateinit var tvEmpty: TextView
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment_history)

        mAuth = FirebaseAuth.getInstance()
        setControl()
        setEvent()
    }

    private fun setControl() {
        rvUserComments = findViewById(R.id.rvUserComments)
        ivBack = findViewById(R.id.ivBack)
        ivEmpty = findViewById(R.id.ivEmpty)
        tvEmpty = findViewById(R.id.tvEmpty)

        rvUserComments.layoutManager = LinearLayoutManager(this)
        commentAdapter = CommentAdapter()
        rvUserComments.adapter = commentAdapter

        ivBack.setOnClickListener {
            finish()
        }
    }

    private fun setEvent() {
        val currentUser = mAuth.currentUser
        if (currentUser == null) {
            showIvEmpty()
            return
        }

        val commentRef = FirebaseDatabase.getInstance().getReference("Comments")
        commentRef.orderByChild("userId").equalTo(currentUser.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val comments = mutableListOf<Comment>()

                    for (commentSnapshot in snapshot.children) {
                        val comment = commentSnapshot.getValue(Comment::class.java)
                        comment?.let {
                            loadProductInfo(it) { commentWithProduct ->
                                comments.add(commentWithProduct)
                                if (comments.size == snapshot.childrenCount.toInt()) {
                                    updateUI(comments)
                                }
                            }
                        }
                    }

                    if (snapshot.childrenCount == 0L) {
                        showIvEmpty()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Comments", "Error loading comments", error.toException())
                    showIvEmpty()
                }
            })
    }

    private fun loadProductInfo(comment: Comment, onComplete: (Comment) -> Unit) {
        val productRef = FirebaseDatabase.getInstance().getReference("products")
            .child(comment.productId)

        productRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productName =
                    snapshot.child("name").getValue(String::class.java) ?: "Unknown Product"
                val productImage = snapshot.child("imageUrl").getValue(String::class.java) ?: ""

                val commentWithProduct = Comment(
                    id = comment.id,
                    userId = comment.userId,
                    userName = comment.userName,
                    productId = comment.productId,
                    rating = comment.rating,
                    content = comment.content,
                    productName = productName,
                    productImage = productImage
                )
                onComplete(commentWithProduct)
            }

            override fun onCancelled(error: DatabaseError) {
                onComplete(comment)
            }
        })
    }

    private fun updateUI(comments: List<Comment>) {
        if (comments.isEmpty()) {
            showIvEmpty()
        } else {
            hideIvEmpty()
            commentAdapter.submitList(comments)
        }
    }

    private fun showIvEmpty() {
        rvUserComments.visibility = View.GONE
        ivEmpty.visibility = View.VISIBLE
        tvEmpty.visibility = View.VISIBLE
    }

    private fun hideIvEmpty() {
        rvUserComments.visibility = View.VISIBLE
        ivEmpty.visibility = View.GONE
        tvEmpty.visibility = View.GONE
    }
}