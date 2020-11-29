package com.example.kotlin_messenger.LatestMessageActivity

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_messenger.ChatLog.ChatLogActivity
import com.example.kotlin_messenger.ChatLog.ChatMessage
import com.example.kotlin_messenger.NewMessages.bindImage
import com.example.kotlin_messenger.NewMessages.refreshlist
import com.example.kotlin_messenger.R
import com.example.kotlin_messenger.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LatestMessageAdapter(var ProfileList:ArrayList<ChatMessage>):ListAdapter<ChatMessage,LatestMessageAdapter.ViewHolder>(refreshlist()){
    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        var UserName= itemView.findViewById<TextView>(R.id.latest_name)
        var UserProfile=itemView.findViewById<ImageView>(R.id.latest_profile)
        var UserMessage=itemView.findViewById<TextView>(R.id.latest_message)
        var content=itemView.findViewById<ConstraintLayout>(R.id.content_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent?.context).inflate(R.layout.latest_message_view,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var data=ProfileList[position]
        holder.UserMessage.text=data.text
        var chatPartnerid:String
        if(data.fromId==FirebaseAuth.getInstance().uid)
            {chatPartnerid=data.toId}
        else
            {chatPartnerid=data.fromId}
        val ref=FirebaseDatabase.getInstance().getReference("/users/$chatPartnerid")
        ref.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user=snapshot.getValue(Users::class.java)
                val data2=Users(chatPartnerid,user?.Username.toString(),user?.ProfilePhotoUrl.toString())
                holder.UserName.text=user?.Username
                bindImage(holder.UserProfile,user?.ProfilePhotoUrl)
                holder.content.setOnClickListener{
                    var intent= Intent(it.context, ChatLogActivity::class.java)
                    intent.putExtra("User_key",data2)
                    it.context.startActivity(intent)
                }
            }

        })
    }

    override fun getItemCount(): Int {
        return ProfileList.size
    }
    class refreshlist: DiffUtil.ItemCallback<ChatMessage>(){
        override fun areItemsTheSame(oldItem:ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.id ==newItem.id
        }
        override fun areContentsTheSame(oldItem: ChatMessage, newItem:ChatMessage): Boolean {
            return oldItem==newItem
        }
    }
}
