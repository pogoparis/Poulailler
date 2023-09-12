package com.example.poulailler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class PouleAdapter(
    private val poules: List<Poule>)
    : RecyclerView.Adapter<PouleAdapter.PouleViewHolder>() {

    // Cette classe interne ViewHolder représente chaque élément de la liste
    inner class PouleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nomTextView: TextView = itemView.findViewById(R.id.nomTextView)
        private val raceTextView: TextView = itemView.findViewById(R.id.raceTextView)
        private val poidsTextView: TextView = itemView.findViewById(R.id.poidsTextView)

        fun bind(poule: Poule) {
            // Associez les données de la poule aux vues du ViewHolder
            nomTextView.text = poule.nom
            raceTextView.text = poule.race
            poidsTextView.text = poule.poids.toString()
        }
    }

    // Cette méthode crée un ViewHolder pour chaque élément de la liste
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PouleViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.poule_item_layout, parent, false)
        return PouleViewHolder(itemView)
    }

    override fun getItemCount() = poules.size

    // Cette méthode associe les données de la poule au ViewHolder
    override fun onBindViewHolder(holder: PouleViewHolder, position: Int) {
        val currentPoule = poules[position]
       holder.nomTextView.text = currentPoule.nom
    }

    // Cette classe interne définit un callback pour la différenciation des éléments dans la liste
    private class PouleDiffCallback : DiffUtil.ItemCallback<Poule>() {
        override fun areItemsTheSame(oldItem: Poule, newItem: Poule): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Poule, newItem: Poule): Boolean {
            return oldItem == newItem
        }
    }
}
