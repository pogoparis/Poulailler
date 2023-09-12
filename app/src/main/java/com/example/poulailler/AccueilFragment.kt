package com.example.poulailler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class AccueilFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_accueil, container, false)

        val buttonCreerPoule = view.findViewById<Button>(R.id.creerPoule)
        val buttonInfoPoulailler = view.findViewById<Button>(R.id.infoPoulailler)

        buttonCreerPoule.setOnClickListener {
            val creationPouleFragment = CreationPouleFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, creationPouleFragment)
                .addToBackStack(null)
                .commit()
        }
        buttonInfoPoulailler.setOnClickListener {
            val infosPoulaillerFrag = InfosPoulaillerFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, infosPoulaillerFrag)
                .addToBackStack(null)
                .commit()
        }
        return view
    }
}
