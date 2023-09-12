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

        val etPouleNom = findViewById<EditText>(R.id.editTextNom)
        val etPoulePoids = findViewById<EditText>(R.id.editTextPoids)
        val etPouleCaract = findViewById<EditText>(R.id.editTextCaract)
        val etPouleRace = findViewById<EditText>(R.id.editTextRace)
        val etPouleImage = findViewById<ImageView>(R.id.imagePoule)

        etPouleImage.setImageResource(R.drawable.poule1)
        val boutonCreerPoule = findViewById<Button>(R.id.buttonCreer)
        boutonCreerPoule.setOnClickListener {

            val pouleName = etPouleNom.text.toString()
            val pouleRace = etPouleRace.text.toString()
            val poulePoids = etPoulePoids.text.toString()
            val pouleCaract = etPouleCaract.text.toString()
            //       val pouleImg = etPouleImage.getImageResource(R.drawable.poule)

            var isValid = true

            if (pouleName.isEmpty()) {
                etPouleNom.error = "Veuillez, svp, entrer un nom"
                isValid = false
            }
            if (pouleRace.isEmpty()) {
                etPouleRace.error = "Veuillez, svp, entrer une race"
                isValid = false
            }
            if (pouleName.isEmpty()) {
                etPoulePoids.error = "Veuillez, svp, entrer un poids approximatif"
                isValid = false
            }
            if (pouleName.isEmpty()) {
                etPouleCaract.error = "Veuillez, svp, entrer un caractère"
                isValid = false
            }

            if (isValid) {
                dbRef = FirebaseDatabase.getInstance().getReference("Poules")
                val pouleId = dbRef.push().key!!
                val poule = Poule(pouleId, pouleName, pouleRace, poulePoids, pouleCaract)
                dbRef.child(pouleId).setValue(poule)
                    .addOnCompleteListener {
                        Toast.makeText(this, "Poule insérée avec succès", Toast.LENGTH_LONG).show()
                        etPouleNom.text.clear()
                        etPouleCaract.text.clear()
                        etPoulePoids.text.clear()
                        etPouleRace.text.clear()

                    }.addOnFailureListener { err ->
                        Toast.makeText(this, "Erreur  ${err.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }
    }
}