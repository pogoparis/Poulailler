package com.example.poulailler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast

class CreationPouleFragment : Fragment() {

    private lateinit var dbRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_creation_poule, container, false)

        val etPouleNom = rootView.findViewById<EditText>(R.id.editTextNom)
        val etPoulePoids = rootView.findViewById<EditText>(R.id.editTextPoids)
        val etPouleCaract = rootView.findViewById<EditText>(R.id.editTextCaract)
        val etPouleRace = rootView.findViewById<EditText>(R.id.editTextRace)
        val etPouleImage = rootView.findViewById<ImageView>(R.id.imagePoule)

        etPouleImage.setImageResource(R.drawable.poule1)
        val boutonCreerPoule = rootView.findViewById<Button>(R.id.buttonCreer)
        boutonCreerPoule.setOnClickListener {

            val pouleName = etPouleNom.text.toString()
            val pouleRace = etPouleRace.text.toString()
            val poulePoids = etPoulePoids.text.toString()
            val pouleCaract = etPouleCaract.text.toString()
            // val pouleImg = etPouleImage.getImageResource(R.drawable.poule)

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
                        Toast.makeText(context, "Poule insérée avec succès", Toast.LENGTH_LONG).show()
                        etPouleNom.text.clear()
                        etPouleCaract.text.clear()
                        etPoulePoids.text.clear()
                        etPouleRace.text.clear()
                    }.addOnFailureListener { err ->
                        Toast.makeText(context, "Erreur  ${err.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }

        return rootView
    }
}
