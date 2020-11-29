package com.example.kotlin_messenger.NewMessages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.kotlin_messenger.LatestMessageActivity.LatestMessagesActivity
import com.example.kotlin_messenger.R
import com.example.kotlin_messenger.Users
import com.example.kotlin_messenger.VideoChatViewActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_new_message_acitivity.*

class NewMessageAcitivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message_acitivity)
        supportActionBar?.title = "Select User"
        fetchUsers()
    }

    fun fetchUsers(){
        val listUser =ArrayList<Users>()
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val user = it.getValue(Users::class.java)
                    if(user!=null){
                        Log.d("user","${user.Username}")
                    listUser.add(Users(user.uid,user.Username,user.ProfilePhotoUrl))
                        Log.d("AginShit","${listUser.size}")}
                }
                var adapter=NewMessageAdapter(listUser)
                recycler_view.adapter=adapter
            }

        })
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.Video_call ->{
                var intent= Intent(this,
                    VideoChatViewActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.send_file,menu)
        return super.onCreateOptionsMenu(menu)
    }
}