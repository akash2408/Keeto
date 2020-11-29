package com.example.kotlin_messenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.kotlin_messenger.LatestMessageActivity.LatestMessagesActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login.setOnClickListener{
            performlogin()
        }
        back_to_registration.setOnClickListener {
            var intent= Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    fun performlogin(){
        val email=login_email_address.text.toString()
        val password=login_password.text.toString()
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener({
                if(!it.isSuccessful)
                {return@addOnCompleteListener}
                else
                {   var intent= Intent(this,
                    LatestMessagesActivity::class.java)
                    intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    Toast.makeText(this,"Login Successfull", Toast.LENGTH_SHORT).show()
                }
            })
            .addOnFailureListener { Toast.makeText(this,"Login Failed", Toast.LENGTH_SHORT).show()  }
    }
}