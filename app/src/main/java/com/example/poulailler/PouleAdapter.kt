package com.example.poulailler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PouleAdapter(
    private val poules: List<Poule>)
    : RecyclerView.Adapter<PouleAdapter.PouleViewHolder>() {

    inner class PouleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomTextView: TextView = itemView.findViewById(R.id.nomTextView)
        private val raceTextView: TextView = itemView.findViewById(R.id.raceTextView)
        private val poidsTextView: TextView = itemView.findViewById(R.id.poidsTextView)

        fun bind(poule: Poule) {
            nomTextView.text = poule.nom
            raceTextView.text = poule.race
            poidsTextView.text = poule.poids
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PouleViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.poule_item_layout, parent, false)
        return PouleViewHolder(itemView)
    }

    override fun getItemCount() = poules.size

    override fun onBindViewHolder(holder: PouleViewHolder, position: Int) {
        val currentPoule = poules[position]
       holder.nomTextView.text = currentPoule.nom
    }

}
