package com.example.kotlin_messenger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.kotlin_messenger.LatestMessageActivity.LatestMessagesActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class RegisterActivity : AppCompatActivity() {
    var profilePhotouri: Uri? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        register.setOnClickListener({performregistration()
                                     uploadImageToFirebase()
        })
        already_have_an_account.setOnClickListener {
            var intent= Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        profile_img.setOnClickListener{
            val intent=Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent,0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==0 && resultCode==Activity.RESULT_OK && data!=null)
            { profilePhotouri=data.data
                val bitmap=MediaStore.Images.Media.getBitmap(contentResolver,profilePhotouri)
                val bitmapDrawable=BitmapDrawable(bitmap)
                profile_img.setBackgroundDrawable(bitmapDrawable)
                profile_img.text=""

            }
    }
    fun performregistration(){
        val user=user_name.text.toString()
        val email=email_address.text.toString()
        val password=password.text.toString()
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener({
                if(!it.isSuccessful)
                {return@addOnCompleteListener}
                else
                {Toast.makeText(this,"Registration Successfull",Toast.LENGTH_SHORT).show()}
            }
            )
            .addOnFailureListener {Toast.makeText(this,"Do Not Create The User",Toast.LENGTH_SHORT).show()  }

    }
   fun uploadImageToFirebase() {
       val filename = UUID.randomUUID().toString()
       val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
       ref.putFile(profilePhotouri!!).addOnSuccessListener {
           val result = it.metadata!!.reference!!.downloadUrl;
           result.addOnSuccessListener {
               var imageLink = it.toString()
               Log.d("result", "${imageLink}")
               saveUserToDatabase(imageLink)
           }
       }
   }
    fun saveUserToDatabase(uri:String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = Users(uid, user_name.text.toString(), uri)
        ref.setValue(user).addOnSuccessListener {
            var intent= Intent(this,
                LatestMessagesActivity::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}
@Parcelize
data class Users constructor(var uid:String="",var Username:String="",var ProfilePhotoUrl:String=""):Parcelable{
}