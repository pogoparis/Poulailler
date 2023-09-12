package com.example.poulailler

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.provider.MediaStore
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
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.Date

class CreationPouleFragment : Fragment() {

    private lateinit var dbRef: DatabaseReference
    private lateinit var rootView: View

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_SELECT_IMAGE = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_creation_poule, container, false)

        val etPouleNom = rootView.findViewById<EditText>(R.id.editTextNom)
        val etPoulePoids = rootView.findViewById<EditText>(R.id.editTextPoids)
        val etPouleCaract = rootView.findViewById<EditText>(R.id.editTextCaract)
        val etPouleRace = rootView.findViewById<EditText>(R.id.editTextRace)
        val takePhotoButton = rootView.findViewById<Button>(R.id.buttonTakePhoto)
        val chooseImageButton = rootView.findViewById<Button>(R.id.buttonChoosePhoto)

        chooseImageButton.setOnClickListener {
            // Créer un intent pour sélectionner une image depuis la galerie
            val pickImageIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickImageIntent, REQUEST_SELECT_IMAGE)
        }

        takePhotoButton.setOnClickListener {
            // Créer un intent pour l'appareil photo
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            // Vérifier si l'appareil photo est disponible
            if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }

        val boutonCreerPoule = rootView.findViewById<Button>(R.id.buttonCreer)
        boutonCreerPoule.setOnClickListener {

            val pouleName = etPouleNom.text.toString()
            val pouleRace = etPouleRace.text.toString()
            val poulePoids = etPoulePoids.text.toString()
            val pouleCaract = etPouleCaract.text.toString()

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
                val poule =
                    Poule(pouleId, pouleName, pouleRace, poulePoids, pouleCaract, imageUrl = "")
                dbRef.child(pouleId).setValue(poule)
                    .addOnCompleteListener {
                        Toast.makeText(context, "Poule insérée avec succès", Toast.LENGTH_LONG)
                            .show()
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

    // Gérer le résultat de la prise de photo
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    // Gérer le résultat de la capture d'image
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    updateImagePreview(imageBitmap)
                    saveImageToFirebaseStorage(imageBitmap)
                }
                REQUEST_SELECT_IMAGE -> {
                    // Gérer le résultat de la sélection d'image depuis la galerie
                    val imageUri = data?.data
                    if (imageUri != null) {
                        val imageBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
                        updateImagePreview(imageBitmap)
                        saveImageToFirebaseStorage(imageBitmap)
                    }
                }
            }
        }
    }

    private fun saveImageToFirebaseStorage(bitmap: Bitmap) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val imagesRef =
            storageRef.child("images") // Répertoire où vous souhaitez stocker les images

        // Générez un nom de fichier unique pour l'image (par exemple, un horodatage)
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "poule_$timestamp.jpg"

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val imageRef = imagesRef.child(imageFileName)

        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            // L'image a été téléchargée avec succès
            imageRef.downloadUrl.addOnCompleteListener { urlTask ->
                if (urlTask.isSuccessful) {
                    val downloadUrl = urlTask.result.toString()
                    // Enregistrez l'URL de téléchargement dans la base de données Firebase
                    updatePouleImageUrl(downloadUrl)
                }
            }
        }.addOnFailureListener { exception ->
            // Une erreur s'est produite lors du téléchargement de l'image
            Toast.makeText(
                context,
                "Erreur de téléchargement de l'image : ${exception.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun updateImagePreview(bitmap: Bitmap) {
        val imagePoule = rootView.findViewById<ImageView>(R.id.imagePoule)
        imagePoule.setImageBitmap(bitmap)
    }

    private fun updatePouleImageUrl(imageUrl: String) {
        val etPouleNom = rootView.findViewById<EditText>(R.id.editTextNom)
        val etPouleRace = rootView.findViewById<EditText>(R.id.editTextRace)
        val etPoulePoids = rootView.findViewById<EditText>(R.id.editTextPoids)
        val etPouleCaract = rootView.findViewById<EditText>(R.id.editTextCaract)

        val pouleName = etPouleNom.text.toString()
        val pouleRace = etPouleRace.text.toString()
        val poulePoids = etPoulePoids.text.toString()
        val pouleCaract = etPouleCaract.text.toString()

        val pouleId = dbRef.push().key!!
        val poule = Poule(pouleId, pouleName, pouleRace, poulePoids, pouleCaract, imageUrl)
        dbRef.child(pouleId).setValue(poule)
            .addOnCompleteListener {
                Toast.makeText(context, "Poule insérée avec succès", Toast.LENGTH_LONG).show()
                etPouleNom.text.clear()
                etPouleCaract.text.clear()
                etPoulePoids.text.clear()
                etPouleRace.text.clear()
            }.addOnFailureListener { err ->
                Toast.makeText(context, "Erreur : ${err.message}", Toast.LENGTH_LONG).show()
            }
    }

}
