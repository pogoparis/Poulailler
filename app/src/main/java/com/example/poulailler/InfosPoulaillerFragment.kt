package com.example.poulailler

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class InfosPoulaillerFragment : Fragment() {
    private val poulesList = mutableListOf<Poule>()
    private lateinit var dbRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var pouleAdapter: PouleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflateur le layout du fragment ici (au lieu de setContentView)
        val rootView = inflater.inflate(R.layout.fragment_infos_poulailler, container, false)

        // Vérifie que Firebase est initialisé
        if (!FirebaseApp.getApps(requireContext()).isEmpty()) {
            dbRef = FirebaseDatabase.getInstance().getReference("poules")

            // Initialisez le RecyclerView
            recyclerView = rootView.findViewById(R.id.recicleViewPoulailler)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            pouleAdapter = PouleAdapter(poulesList, object : PouleAdapter.OnDeleteClickListener {
                override fun onDeleteClick(position: Int) {
                    val selectedPoule = poulesList[position] // Obtenez la poule sélectionnée

                    // Supprimez la poule de la liste locale
                    poulesList.removeAt(position)
                    pouleAdapter.notifyItemRemoved(position)

                    // Supprimez également la poule de la base de données Firebase
                    val dbRef = FirebaseDatabase.getInstance().getReference("poules")
                    dbRef.child(selectedPoule.id).removeValue()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "La poule a été supprimée",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            } else {
                                Toast.makeText(context, "Suppression échouée", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                }
            })

            recyclerView.adapter = pouleAdapter

            dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    poulesList.clear()
                    if (dataSnapshot.exists()) {
                        for (childSnapshot in dataSnapshot.children) {
                            val poule = childSnapshot.getValue(Poule::class.java)
                            poule?.let { poulesList.add(it) }
                        }
                        val mAdapter =
                            PouleAdapter(poulesList, object : PouleAdapter.OnDeleteClickListener {
                                override fun onDeleteClick(position: Int) {
                                    val selectedPoule =
                                        poulesList[position] // Obtenez la poule sélectionnée

                                    // Supprimez la poule de la liste locale
                                    poulesList.removeAt(position)
                                    pouleAdapter.notifyItemRemoved(position)

                                    // Supprimez également la poule de la base de données Firebase
                                    val dbRef =
                                        FirebaseDatabase.getInstance().getReference("poules")
                                    dbRef.child(selectedPoule.id).removeValue()
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(
                                                    context,
                                                    "La poule a été supprimée",
                                                    Toast.LENGTH_LONG
                                                )
                                                    .show()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Suppression échouée",
                                                    Toast.LENGTH_LONG
                                                )
                                                    .show()
                                            }
                                        }
                                }
                            })
                        recyclerView.adapter = mAdapter

                        mAdapter.SetOnItemClickListener(object : PouleAdapter.OnItemClickListener {
                            override fun onItemClick(position: Int) {
                                val selectedPoule =
                                    poulesList[position] // Obtenez la poule sélectionnée
                                val bundle = Bundle()
                                bundle.putString("pouleId", selectedPoule.id)
                                bundle.putString("nom", selectedPoule.nom)
                                bundle.putString("race", selectedPoule.race)
                                bundle.putString("poids", selectedPoule.poids)
                                bundle.putString("caract", selectedPoule.caract)
                                bundle.putString("imageUrl", selectedPoule.imageUrl)

                                val fragment = CreationPouleFragment()
                                fragment.arguments = bundle

                                val fragmentManager = requireActivity().supportFragmentManager
                                val fragmentTransaction = fragmentManager.beginTransaction()
                                fragmentTransaction.replace(R.id.fragmentContainer, fragment)
                                fragmentTransaction.addToBackStack(null)
                                fragmentTransaction.commit()
                            }
                        })
                        pouleAdapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Gérer les erreurs ici
                }
            })
        }

        return rootView
    }
}