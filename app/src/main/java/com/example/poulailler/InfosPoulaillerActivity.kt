package com.example.poulailler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class InfosPoulaillerActivity : AppCompatActivity() {

    private val poulesList = mutableListOf<Poule>()
    private lateinit var dbRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var pouleAdapter: PouleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.infos_poulailler)
        // Vérifie que Firebase est initialisé
        if (!FirebaseApp.getApps(this).isEmpty()) {
            dbRef = FirebaseDatabase.getInstance().getReference("Poules")

            // Initialisez le RecyclerView
            recyclerView = findViewById<RecyclerView>(R.id.recicleViewPoulailler)
            recyclerView.layoutManager = LinearLayoutManager(this)
            pouleAdapter = PouleAdapter(poulesList)
            recyclerView.adapter = pouleAdapter



            dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    poulesList.clear()
                    for (childSnapshot in dataSnapshot.children) {
                        val poule = childSnapshot.getValue(Poule::class.java)
                        poule?.let { poulesList.add(it) }
                    }
                    pouleAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }
}

