package com.example.poulailler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


class PouleAdapter(
    private val poules: List<Poule>)
    : RecyclerView.Adapter<PouleAdapter.PouleViewHolder>() {

    private lateinit var mListener: OnItemClickListener
    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    fun SetOnItemClickListener(clickListener: OnItemClickListener){
        mListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PouleAdapter.PouleViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.poule_item_layout, parent, false)
        return PouleViewHolder(itemView, mListener)
    }

    override fun getItemCount() = poules.size

    override fun onBindViewHolder(holder: PouleAdapter.PouleViewHolder, position: Int) {
        val currentPoule = poules[position]
     /*   holder.nomTextView.text = currentPoule.nom
        holder.raceTextView.text = currentPoule.race*/
holder.bind(currentPoule)
    }

    inner class PouleViewHolder(itemView: View, clickListener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val nomTextView: TextView = itemView.findViewById(R.id.nomTextView)
        val raceTextView: TextView = itemView.findViewById(R.id.raceTextView)
        // Référence à votre ImageView dans le layout de l'élément de RecyclerView
        private val avatarImageView: ImageView = itemView.findViewById(R.id.avatarPouleList)

        init {
            itemView.setOnClickListener{
                clickListener.onItemClick(adapterPosition)
            }
        }
        fun bind(poule: Poule) {
            nomTextView.text = poule.nom
            raceTextView.text = poule.race

            // Charger et afficher l'image à partir de l'URL Firebase
            if (poule.imageUrl!!.isNotEmpty()) {
                Picasso.get().load(poule.imageUrl).into(avatarImageView)
            } else {
                avatarImageView.setImageResource(R.drawable.poule1)
            }
        }
    }
}
