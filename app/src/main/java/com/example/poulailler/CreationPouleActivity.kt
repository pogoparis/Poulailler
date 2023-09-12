package com.example.poulailler

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreationPouleActivity : AppCompatActivity() {

    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.creation_poule)

        val etPouleNom =findViewById<EditText>(R.id.editTextNom)
        val etPoulePoids =findViewById<EditText>(R.id.editTextPoids)
        val etPouleCaract =findViewById<EditText>(R.id.editTextCaract)
        val etPouleRace =findViewById<EditText>(R.id.editTextRace)
        val etPouleImage =findViewById<ImageView>(R.id.imagePoule)

        etPouleImage.setImageResource(R.drawable.poule1)
        val boutonCreerPoule = findViewById<Button>(R.id.buttonCreer)
        boutonCreerPoule.setOnClickListener {

            val pouleName = etPouleNom.text.toString()
            val pouleRace = etPouleRace.text.toString()
            val poulePoids = etPoulePoids.text.toString()
            val pouleCaract = etPouleCaract.text.toString()
     //       val pouleImg = etPouleImage.getImageResource(R.drawable.poule)


            dbRef = FirebaseDatabase.getInstance().getReference("Poules")
            val pouleId = dbRef.push().key!!
            val poule = Poule(pouleId, pouleName, pouleRace , poulePoids, pouleCaract)
            dbRef.child(pouleId).setValue(poule)
                .addOnCompleteListener{
                    Toast.makeText(this, "Poule insérée avec succès", Toast.LENGTH_LONG).show()
                }.addOnCompleteListener { err ->
                    Toast.makeText(this, "Erreur", Toast.LENGTH_LONG).show()
                }
        }
    }
}