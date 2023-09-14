package com.example.poulailler

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.util.Date

class CreationPouleFragment : Fragment() {

    private lateinit var dbRef: DatabaseReference
    private lateinit var rootView: View
    private var isEditMode = false
    private var imageUrlBeingEdited: String? = null
    private var selectedImageBitmap: Bitmap? = null

    companion object {
        //     private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_SELECT_IMAGE = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_creation_poule, container, false)

        val chooseImageButton = rootView.findViewById<Button>(R.id.buttonChoosePhoto)
        val boutonCreerPoule = rootView.findViewById<Button>(R.id.buttonCreer)
        val etPouleNom = rootView.findViewById<EditText>(R.id.editTextNom)
        val etPouleRace = rootView.findViewById<EditText>(R.id.editTextRace)
        val etPoulePoids = rootView.findViewById<EditText>(R.id.editTextPoids)
        val etPouleCaract = rootView.findViewById<EditText>(R.id.editTextCaract)

        val bundle = arguments
        if (bundle != null) {
            isEditMode = true
            val pouleName = bundle.getString("nom", "")
            val pouleRace = bundle.getString("race", "")
            val poulePoids = bundle.getString("poids", "")
            val pouleCaract = bundle.getString("caract", "")
            val imageUrl = bundle.getString("imageUrl", "")
            etPouleNom.setText(pouleName)
            etPouleRace.setText(pouleRace)
            etPoulePoids.setText(poulePoids)
            etPouleCaract.setText(pouleCaract)

            // Si une URL d'image existe, chargez l'image dans votre ImageView
            if (imageUrl.isNotEmpty()){
                Log.d("tag de log","ImageUrl : $imageUrl")
                val imagePoule = rootView.findViewById<ImageView>(R.id.imagePoule)
                Picasso.get().load(imageUrl).into(imagePoule)
            }
            // et changer le bouton en sauvegarder pluto que creer
            boutonCreerPoule.text = "Sauvegarder"
        }

        chooseImageButton.setOnClickListener {
            val pickImageIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickImageIntent, REQUEST_SELECT_IMAGE)
        }

        boutonCreerPoule.setOnClickListener {
            val pouleName = etPouleNom.text.toString()
            val pouleRace = etPouleRace.text.toString()
            val poulePoids = etPoulePoids.text.toString()
            val pouleCaract = etPouleCaract.text.toString()
            val infosPoulaillerFragment = InfosPoulaillerFragment()
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

            if (isValid) {
                dbRef = FirebaseDatabase.getInstance().getReference("poules")

                // ********************** Mode modification **************************
                if (isEditMode) {
                    val pouleId =
                        bundle?.getString("pouleId") // Obtenez l'ID de la poule à partir des arguments

                    if (pouleId != null) {
                        val poule = if (selectedImageBitmap != null) {
                            Poule(
                                pouleId,
                                pouleName,
                                pouleRace,
                                poulePoids,
                                pouleCaract,
                                imageUrlBeingEdited ?: ""
                            )
                        } else {
                            Poule(
                                pouleId,
                                pouleName,
                                pouleRace,
                                poulePoids,
                                pouleCaract,
                                bundle.getString("imageUrl", "") ?: ""
                            )

                        }
                        dbRef.child(pouleId).setValue(poule)
                            .addOnCompleteListener {
                                Toast.makeText(
                                    context, "Poule mise à jour avec succès", Toast.LENGTH_LONG
                                ).show()
                                // Vérifiez si une image a été sélectionnée et envoyez-la en base de données
                                if (selectedImageBitmap != null) {
                                    saveImageToFirebaseStorage(selectedImageBitmap!!)
                                }
                                replaceFragment(infosPoulaillerFragment)
                            }
                            .addOnFailureListener { err ->
                                Toast.makeText(
                                    context, "Erreur : ${err.message}", Toast.LENGTH_LONG
                                ).show()
                            }
                    }
                } else {
                    // ************************** CREATION *****************************
                    val pouleId = dbRef.push().key!!
                    val poule =
                        Poule(
                            pouleId,
                            pouleName,
                            pouleRace,
                            poulePoids,
                            pouleCaract,
                            imageUrlBeingEdited ?: ""
                        )
                    dbRef.child(pouleId).setValue(poule)
                        .addOnCompleteListener {
                            Toast.makeText(context, "Poule insérée avec succès", Toast.LENGTH_LONG)
                                .show()
                            etPouleNom.text.clear()
                            etPouleCaract.text.clear()
                            etPoulePoids.text.clear()
                            etPouleRace.text.clear()
                            // Vérifiez si une image a été sélectionnée et envoyez-la en base de données
                            if (selectedImageBitmap != null) {
                                saveImageToFirebaseStorage(selectedImageBitmap!!)
                            }
                            replaceFragment(infosPoulaillerFragment)

                        }.addOnFailureListener { err ->
                            Toast.makeText(context, "Erreur  ${err.message}", Toast.LENGTH_LONG)
                                .show()
                        }
                }
            }
        }
            return rootView
        }



    // Fonction pour remplacer le fragment actuellement affiché
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_SELECT_IMAGE -> {
                    // Gérer le résultat de la sélection d'image depuis la galerie
                    val imageUri = data?.data
                    if (imageUri != null) {
                        val imageBitmap = MediaStore.Images.Media.getBitmap(
                            requireContext().contentResolver,
                            imageUri
                        )
                        imageUrlBeingEdited =
                            imageUri.toString() // Mettre à jour l'URL de l'image en cours de modification
                        selectedImageBitmap = imageBitmap
                        updateImagePreview(imageBitmap)
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

}
