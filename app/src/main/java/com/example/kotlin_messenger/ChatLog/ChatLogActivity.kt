package com.example.kotlin_messenger.ChatLog

import ChatAdapter
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Adapter
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_messenger.LatestMessageActivity.LatestMessagesActivity
import com.example.kotlin_messenger.NewMessages.NewMessageAcitivity
import com.example.kotlin_messenger.R
import com.example.kotlin_messenger.RegisterActivity
import com.example.kotlin_messenger.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.net.URI
import java.util.*
import kotlin.collections.ArrayList

class ChatLogActivity : AppCompatActivity() {
    var messageprofilePhotouri: Uri? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        var user=intent.getParcelableExtra<Users>("User_key")
        supportActionBar?.title=user.Username
        listenMessages()
        send_chat_log.setOnClickListener{
            performSendMessage("")
        }
    }
    fun listenMessages(){
        val listUser =ArrayList<ChatMessage>()
        val fromid= FirebaseAuth.getInstance().uid
        var user=intent.getParcelableExtra<Users>("User_key")
        val toid=user.uid
        val ref=FirebaseDatabase.getInstance().getReference("/user-messages/$fromid/$toid")
        ref.addChildEventListener(object :ChildEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")

            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
              val chatmessage=snapshot.getValue(ChatMessage::class.java)
                if(chatmessage!=null){
                    Log.d("messages ","${chatmessage}")
                    listUser.add(ChatMessage(chatmessage.id,chatmessage.text,chatmessage.fromId,chatmessage.toId,chatmessage.timestamp,chatmessage.fromidurl,chatmessage.toidurl,chatmessage.imageUrl))
                    var adapter=ChatAdapter()
                    adapter.addHeaderAndSubmitList(listUser)
                    ChatAdapter.Statiscated.mContext= this@ChatLogActivity
                    recycle_chat_log.adapter=adapter

                    recycle_chat_log.scrollToPosition(recycle_chat_log.adapter?.itemCount?.minus(1) as Int)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

        })

    }
     fun performSendMessage(uri:String) {
            val text = text_chat_log.text.toString()
            //val ref=FirebaseDatabase.getInstance().getReference("/messages").push()
            val fromid = FirebaseAuth.getInstance().uid
            var myref = FirebaseDatabase.getInstance().getReference("/users/$fromid")
            myref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    var Current = snapshot.getValue(Users::class.java)
                    Log.d("Current", "${Current?.ProfilePhotoUrl}")
                    var user = intent.getParcelableExtra<Users>("User_key")
                    val toid = user.uid
                    val ref =
                        FirebaseDatabase.getInstance().getReference("/user-messages/$fromid/$toid")
                            .push()
                    val toref =
                        FirebaseDatabase.getInstance().getReference("/user-messages/$toid/$fromid")
                            .push()
                    if (Current != null) {
                        val chatMessage = ChatMessage(
                            ref.key as String,
                            text,
                            fromid as String,
                            toid,
                            System.currentTimeMillis() / 1000,
                            Current?.ProfilePhotoUrl,
                            user.ProfilePhotoUrl,
                            uri.toString()
                        )
                        ref.setValue(chatMessage).addOnSuccessListener {
                            text_chat_log.text.clear()
                            recycle_chat_log.scrollToPosition(
                                recycle_chat_log.adapter?.itemCount?.minus(
                                    1
                                ) as Int
                            )
                        }
                        toref.setValue(chatMessage)
                        val latestref = FirebaseDatabase.getInstance()
                            .getReference("/latest-messages/$fromid/$toid")
                        latestref.setValue(chatMessage)
                        val tolatestref = FirebaseDatabase.getInstance()
                            .getReference("/latest-messages/$toid/$fromid")
                        tolatestref.setValue(chatMessage)
                    }
                }

            })
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.Video_call ->{
                val intent=Intent(Intent.ACTION_PICK)
                intent.type="image/*"
                startActivityForResult(intent,0)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.video_call,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==0 && resultCode== Activity.RESULT_OK && data!=null)
        { messageprofilePhotouri=data.data
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
            ref.putFile(messageprofilePhotouri!!).addOnSuccessListener {
                val result = it.metadata!!.reference!!.downloadUrl;
                result.addOnSuccessListener {
                    var imageLink = it.toString()
                    Log.d("result", "${imageLink}")
                    performSendMessage(imageLink)
                }
            }
        }
    }
}
@Parcelize
data class ChatMessage constructor(val id:String="",val text:String="",val fromId:String="",val toId:String="",val timestamp:Long=0L,val fromidurl:String="",val toidurl:String="",val imageUrl:String=""):Parcelable{
}