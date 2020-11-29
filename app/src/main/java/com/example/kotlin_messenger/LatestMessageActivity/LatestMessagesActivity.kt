package com.example.kotlin_messenger.LatestMessageActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.kotlin_messenger.ChatLog.ChatMessage
import com.example.kotlin_messenger.NewMessages.NewMessageAcitivity
import com.example.kotlin_messenger.NewMessages.NewMessageAdapter
import com.example.kotlin_messenger.R
import com.example.kotlin_messenger.RegisterActivity
import com.example.kotlin_messenger.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.activity_new_message_acitivity.*

class LatestMessagesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)
        showLatestMessages()
        verifyUserIsLoggedIn()
    }
    fun verifyUserIsLoggedIn(){
        val uid=FirebaseAuth.getInstance().uid
        if(uid==null){
            val intent= Intent(this,
                RegisterActivity::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
    fun showLatestMessages(){
        var fromid=FirebaseAuth.getInstance().uid
        var ref=FirebaseDatabase.getInstance().getReference("/latest-messages/${fromid}")
        Log.d("message","${fromid}")
        var latestmessagemap=HashMap<String,ChatMessage>()

        fun refreshlist(){
            var listUser=ArrayList<ChatMessage>()
            latestmessagemap.values.forEach{
            listUser.add(it)
            }
            Log.d("new message","${listUser}")
            var adapter= LatestMessageAdapter(listUser)
            adapter.notifyDataSetChanged()
            latest_message_recycler_view.adapter=adapter
        }
        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatmessage=snapshot.getValue(ChatMessage::class.java)
                Log.d("message","${chatmessage}")
                if(chatmessage!=null){
                    //listUser.add(ChatMessage(chatmessage.id,chatmessage.text,chatmessage.fromId,chatmessage.toId,chatmessage.timestamp,chatmessage.fromidurl,chatmessage.toidurl))
                   // Log.d("message","${listUser.size}")
                    latestmessagemap[snapshot.key!!]=chatmessage
                   // var adapter= LatestMessageAdapter(listUser)
                    //latest_message_recycler_view.adapter=adapter
                    refreshlist()
                }

            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val chatmessage=snapshot.getValue(ChatMessage::class.java)
                    Log.d("message","${chatmessage}")
                    if(chatmessage!=null){
                       // listUser.add(ChatMessage(chatmessage.id,chatmessage.text,chatmessage.fromId,chatmessage.toId,chatmessage.timestamp,chatmessage.fromidurl,chatmessage.toidurl))
                        //Log.d("message","${listUser.size}")
                        latestmessagemap[snapshot.key!!]=chatmessage
                        //var adapter= LatestMessageAdapter(listUser)
                        //latest_message_recycler_view.adapter=adapter
                        refreshlist()
                    }

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.Logout ->{FirebaseAuth.getInstance().signOut()
                val intent= Intent(this,
                    RegisterActivity::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            R.id.New_Messages ->{val intent= Intent(this,NewMessageAcitivity::class.java)
                startActivity(intent)}
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.overflow_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

}