package com.example.sellerapp


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val uid = currentUser.uid
        val dbRef = FirebaseDatabase.getInstance().getReference("users").child(uid)

        val etName = findViewById<EditText>(R.id.etProfileName)
        val etPhone = findViewById<EditText>(R.id.etProfilePhone)
        val tvEmail = findViewById<TextView>(R.id.tvProfileEmail)
        val btnSave = findViewById<Button>(R.id.btnSaveProfile)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // Fetch user data from Realtime Database
        dbRef.get().addOnSuccessListener { snapshot ->
            val user = snapshot.getValue(UserProfile::class.java)
            if (user != null) {
                etName.setText(user.name)
                etPhone.setText(user.phone)
                tvEmail.text = "Email: ${user.email}"
            }
        }

        // Update profile data in Realtime Database
        btnSave.setOnClickListener {
            val updatedName = etName.text.toString().trim()
            val updatedPhone = etPhone.text.toString().trim()

            val updates = mapOf<String, Any>(
                "name" to updatedName,
                "phone" to updatedPhone
            )

            dbRef.updateChildren(updates).addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
            }
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}