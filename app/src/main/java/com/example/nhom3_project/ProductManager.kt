package com.example.nhom3_project

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProductManager : AppCompatActivity() {
    private lateinit var edt_id: EditText
    private lateinit var edt_name: EditText
    private lateinit var btn_add_user: Button
    private lateinit var recycler: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var listUser: MutableList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        setControl()
        setEvent()
        getListUser()
    }

    private fun setControl() {
        edt_id = findViewById(R.id.edt_id)
        edt_name = findViewById(R.id.edt_name)
        btn_add_user = findViewById(R.id.btn_add_user)
        recycler = findViewById(R.id.rcv_list_user)

        val linerLayout = LinearLayoutManager(this)
        recycler.layoutManager = linerLayout

        val dividerItem = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recycler.addItemDecoration(dividerItem)

        listUser = mutableListOf()
        userAdapter = UserAdapter(this, listUser)

        recycler.adapter = userAdapter
    }


    private fun setEvent() {
        btn_add_user.setOnClickListener {
            val id = edt_id.text.toString().trim().toInt()
            var name = edt_name.text.toString().trim()
            var user = User(id, name)
            onClickAddUser(user)

        }
    }

    private fun onClickAddUser(user: User) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("list_users")

        val pathObject = user.id.toString()
        myRef.child(pathObject).setValue(user) { error, _ ->
            if (error == null) {
                Toast.makeText(this, "Add user success", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to add user", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getListUser() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("list_users")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    val user: User? = postSnapshot.getValue(User::class.java)
                    user?.let {
                        listUser.add(it)
                    }
                }
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())

            }
        })
    }
}
