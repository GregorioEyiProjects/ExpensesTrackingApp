package com.example.expensestraker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.expensestraker.databinding.ActivitySignUpInBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class SignUpInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpInBinding
    private lateinit var fb: FirebaseAnalytics
    private lateinit var db: FirebaseFirestore
    private lateinit var auth:  FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //firebase
        fb = FirebaseAnalytics.getInstance(this)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        //Bundle
        val bundle = Bundle()
        bundle.putString("Message", "Implementation completed")
        fb.logEvent("InitScreen", bundle)

        //Initiating listeners
        initListeners()
    }

    private fun initListeners() {

        title = "Authentication"

        binding.btnSignUp.setOnClickListener {
            if (binding.etEmail.text!!.isNotEmpty() && binding.etPassword.text!!.isNotEmpty()) {
                val userEmail = binding.etEmail.text.toString()
                val userPassword = binding.etPassword.text.toString()
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener {user ->

                        if (user.isSuccessful) {

                            //val userID: String = user.result.user?.uid ?: "" //to get the unique user id
                            //val userRefID = userRef.id
                            // val userRef = userID?.let { it1 -> createUserCollection(it1) } //Creating a collection for each item of this particularly user
                            val userID = getUserID()
                            val intent = Intent(this, MainActivity::class.java).apply {
                                putExtra("userID", userID)
                                putExtra("userEmail",user.result?.user?.email ?: "") // here is how to get the user email
                            }

                            Log.i("Sign In UP Activity","User ID is: $userID")
                            startActivity(intent)
                        } else {
                            Log.i("Sign Up In Activity", "The value of the user ID is ${user.result.user?.uid}")
                            showAlert()
                        }

                    }
            }
        }

        binding.btnSignIn.setOnClickListener {
            if (binding.etEmail.text!!.isNotEmpty() && binding.etPassword.text!!.isNotEmpty()) {
                val userEmail = binding.etEmail.text.toString()
                val userPassword = binding.etPassword.text.toString()
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener { user ->
                        if (user.isSuccessful) {
                            val intent = Intent(this, MainActivity::class.java)
                            val userID = getUserID()
                            intent.putExtra("userID",userID)
                            //intent.putExtra("userEmail",it.result?.user?.email ?: "") // here is how to get the user email
                            startActivity(intent)
                        } else {
                            showAlert()
                        }
                    }
            }
        }

    }

    private fun getUserID(): String? {
        // Registration successful, retrieve the user ID
        val user: FirebaseUser? = auth.currentUser
        val userId = user?.uid
        // Use the userId as needed
        println("User ID: $userId")
        return  userId
    }

    private fun createUserCollection(userID: String): DocumentReference {
        return db.collection("Users").document(userID) // returns an empty collection
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Credentials are incorrect!")
        builder.setPositiveButton("OK", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}