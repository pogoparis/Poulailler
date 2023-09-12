package com.example.poulailler

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.FirebaseApp

class AccueilActivity : AppCompatActivity() {

private lateinit var infoPoulaillerButton   : Button
private lateinit var creerPouleButton   : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.accueil)

        infoPoulaillerButton = findViewById(R.id.infoPoulailler)
        infoPoulaillerButton.setOnClickListener {
            val intent = Intent(this, InfosPoulaillerActivity::class.java)
            startActivity(intent)
        }

        creerPouleButton = findViewById(R.id.creerPoule)
        creerPouleButton.setOnClickListener {
            val intent = Intent(this, CreationPouleActivity::class.java)
            startActivity(intent)


        }
    }
}
