package com.example.kotlin_messenger.NewMessages

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlin_messenger.ChatLog.ChatLogActivity
import com.example.kotlin_messenger.R
import com.example.kotlin_messenger.Users
import java.io.File

class NewMessageAdapter(var ProfileList:ArrayList<Users>):androidx.recyclerview.widget.ListAdapter<Users,NewMessageAdapter.ViewHolder>(refreshlist()){

    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        var UserName= itemView.findViewById<TextView>(R.id.userName)
        var Profile=itemView.findViewById<ImageView>(R.id.profile_Photo)
        var content=itemView.findViewById<ConstraintLayout>(R.id.content_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view=LayoutInflater.from(parent?.context)
            .inflate(R.layout.new_message_custom_view,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var data=ProfileList.get(position)
        holder.UserName.text= data.Username
        bindImage(holder.Profile,data.ProfilePhotoUrl)
        holder.content.setOnClickListener{
            var intent=Intent(it.context, ChatLogActivity::class.java)
            intent.putExtra("User_key",data)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return ProfileList.size
    }
}
class refreshlist : DiffUtil.ItemCallback<Users>() {
    override fun areItemsTheSame(oldItem: Users, newItem: Users): Boolean {
        return oldItem.uid == newItem.uid
    }
    override fun areContentsTheSame(oldItem: Users, newItem: Users): Boolean {
        return oldItem == newItem
    }
}
fun bindImage(imgView:ImageView,imgUrl:String?){
    imgUrl?.let{
        val imgUri=imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .into(imgView)
    }
}

