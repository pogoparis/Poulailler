package com.example.poulailler

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp

class AccueilActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var fragmentManager: FragmentManager
    private var mediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_base)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        fragmentManager = supportFragmentManager

        // Chargez le fragment AccueilFragment par défaut
        val defaultFragment = AccueilFragment()
        replaceFragment(defaultFragment)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_accueil -> {
                    val accueilFragment = AccueilFragment()
                    replaceFragment(accueilFragment)
                    true
                }
                R.id.navigation_infos -> {
                    val infosPoulaillerFragment = InfosPoulaillerFragment()
                    replaceFragment(infosPoulaillerFragment)
                    true
                }
                R.id.navigation_creer -> {
                    val creationPouleFragment = CreationPouleFragment()
                    replaceFragment(creationPouleFragment)
                    true
                }
                else -> false
            }
        }
    }
    fun playSaveSound() {
        // Libérez le lecteur audio précédent s'il existe
        mediaPlayer?.release()

        // Créez un nouveau lecteur audio et configurez-le pour jouer le son
        mediaPlayer = MediaPlayer.create(this, R.raw.sound_poule)

        // Écoutez la fin de la lecture pour libérer les ressources
        mediaPlayer?.setOnCompletionListener {
            mediaPlayer?.release()
            mediaPlayer = null
        }

        // Jouez le son
        mediaPlayer?.start()
    }


    // Fonction pour remplacer le fragment actuellement affiché
    private fun replaceFragment(fragment: Fragment) {
        fragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
