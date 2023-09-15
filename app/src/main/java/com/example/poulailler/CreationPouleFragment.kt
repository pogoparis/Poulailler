package com.example.poulailler

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.internal.ViewUtils.hideKeyboard
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.Date

class CreationPouleFragment : Fragment() {

    private lateinit var dbRef: DatabaseReference
    private lateinit var rootView: View
    private var isEditMode = false
    private var imageUrlBeingEdited: String? = null
    private var selectedImageBitmap: Bitmap? = null
    private lateinit var imageUri: Uri
    private lateinit var etPouleNom: EditText
    private lateinit var etPouleRace: EditText
    private lateinit var etPouleCaract: EditText
    private lateinit var etPoulePoids: EditText

    companion object {
        //     private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_SELECT_IMAGE = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_creation_poule, container, false)
        val goToWebButton = rootView.findViewById<Button>(R.id.lienCreerPoule)
        val url = "https://meiker.io/play/13152/online.html"
        val intentWeb = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        val chooseImageButton = rootView.findViewById<Button>(R.id.buttonChooseImage)
        val boutonCreerPoule = rootView.findViewById<Button>(R.id.buttonCreer)
        etPouleNom = rootView.findViewById(R.id.editTextNom)
        etPouleRace = rootView.findViewById(R.id.editTextRace)
        etPoulePoids = rootView.findViewById(R.id.editTextPoids)
        etPouleCaract = rootView.findViewById(R.id.editTextCaract)
        val bundle = arguments
        //**************************** CHARGEMENT DES INFOS SI UN BUNDLE EST PASSE AU FRAGMENT ****************************
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
            if (!imageUrl.isNullOrEmpty()) {
                val imagePoule = rootView.findViewById<ImageView>(R.id.imagePoule)
                Glide.with(this)
                    .load(imageUrl)
                    .into(imagePoule)
            }
            // et changer le bouton en sauvegarder pluto que creer
            boutonCreerPoule.text = "Sauvegarder"
        }
        //********************************** LISTENER *******************************

        rootView.setOnClickListener {
            hideKeyboard()
        }

        goToWebButton.setOnClickListener {
            startActivity(intentWeb)
        }

        chooseImageButton.setOnClickListener {
            val pickImageIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickImageIntent, REQUEST_SELECT_IMAGE)
        }
// ********************************************* BOUTON CREER POULE *********************************************
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

            if (isValid) {
                jouerSon()
                dbRef = FirebaseDatabase.getInstance().getReference("poules")


                if (!isEditMode) {
                    // ************************** CREATION *****************************
                    val pouleId = dbRef.push().key!!
                    val poule = Poule(
                        pouleId,
                        pouleName,
                        pouleRace,
                        poulePoids,
                        pouleCaract,
                        imageUrlBeingEdited ?: ""
                    )

                    // Vérifiez si une image a été sélectionnée et envoyez-la en base de données
                    if (selectedImageBitmap != null) {
                        saveImageToFirebaseStorage(selectedImageBitmap!!, poule)
                    } else {
                        // Si aucune image n'a été sélectionnée, envoyez uniquement les données de la poule
                        sendPouleToFirebase(poule)
                    }
                } else {
                    // ***************************************** EDIT MODE *****************************************
                    val pouleId =
                        bundle?.getString("pouleId") // Obtenez l'ID de la poule à partir des arguments
                    if (pouleId != null) {
                        val poule = Poule(
                            pouleId,
                            pouleName,
                            pouleRace,
                            poulePoids,
                            pouleCaract,
                            imageUrlBeingEdited
                        )
                        if (selectedImageBitmap != null) {
                            saveImageToFirebaseStorage(selectedImageBitmap!!, poule)
                        } else {
                            // Si aucune image n'a été sélectionnée, envoyez uniquement les données de la poule
                            sendPouleToFirebase(poule)
                        }
                    }
                }
            }
        }
        return rootView
    }

    private fun saveImageToFirebaseStorage(bitmap: Bitmap, poule: Poule) {

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val imagesRef =
            storageRef.child("images") // Répertoire des images dans FirebaseStorage
        // Générez un nom de fichier unique pour l'image (horodatage)
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
                    val imageUrl = urlTask.result.toString()
                    poule.imageUrl =
                        imageUrl // Mettez à jour l'URL de l'image dans l'objet de poule
                    sendPouleToFirebase(poule) // Envoyez la poule en base de données
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

    private fun sendPouleToFirebase(poule: Poule) {
        Log.d(TAG, "sendPouleToFirebase $poule")
        dbRef = FirebaseDatabase.getInstance().getReference("poules")
        dbRef.child(poule.id).setValue(poule)
            .addOnCompleteListener {
                Toast.makeText(context, "Poule insérée avec succès", Toast.LENGTH_LONG)
                    .show()
                etPouleNom.text.clear()
                etPouleCaract.text.clear()
                etPoulePoids.text.clear()
                etPouleRace.text.clear()
                replaceFragment(InfosPoulaillerFragment())
            }.addOnFailureListener { err ->
                Toast.makeText(context, "Erreur : ${err.message}", Toast.LENGTH_LONG)
                    .show()
            }
    }

    private fun jouerSon() {
        val mainActivity = requireActivity() as AccueilActivity
        mainActivity.playSaveSound()
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
                    imageUri = data?.data!!
                    val imageBitmap = MediaStore.Images.Media.getBitmap(
                        requireContext().contentResolver,
                        imageUri
                    )
                    imageUrlBeingEdited =
                        imageUri.toString() // Mettre à jour l'URL de l'image en cours de modification
                    updateImagePreview(imageBitmap)
                }
            }
        }
    }

    private fun updateImagePreview(bitmap: Bitmap) {
        val imagePoule = rootView.findViewById<ImageView>(R.id.imagePoule)
        imagePoule.setImageBitmap(bitmap)
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusView = requireActivity().currentFocus
        if (currentFocusView != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
        }
    }

}
