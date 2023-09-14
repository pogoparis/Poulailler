package com.example.poulailler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class PouleAdapter(
    private val poules: List<Poule>,
    private val onDeleteClickListener: OnDeleteClickListener
) : RecyclerView.Adapter<PouleAdapter.PouleViewHolder>() {

    private lateinit var mListener: OnItemClickListener
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    interface OnDeleteClickListener {
        fun onDeleteClick(position: Int)
    }

    fun SetOnItemClickListener(clickListener: OnItemClickListener) {
        mListener = clickListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PouleAdapter.PouleViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.poule_item_layout, parent, false)
        return PouleViewHolder(itemView, mListener)
    }

    override fun getItemCount() = poules.size

    override fun onBindViewHolder(holder: PouleAdapter.PouleViewHolder, position: Int) {
        val currentPoule = poules[position]
        holder.bind(currentPoule)

    }

    inner class PouleViewHolder(itemView: View, clickListener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        val nomTextView: TextView = itemView.findViewById(R.id.nomTextView)
        val raceTextView: TextView = itemView.findViewById(R.id.raceTextView)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)

        private val avatarImageView: ImageView = itemView.findViewById(R.id.avatarPouleList)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
            // Ajoutez un OnClickListener au bouton de suppression
            deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Appelez onDeleteClick de l'interface OnDeleteClickListener
                    onDeleteClickListener.onDeleteClick(position)
                }
            }
        }

        fun bind(poule: Poule) {
            nomTextView.text = poule.nom
            raceTextView.text = poule.race

            // Charger et afficher l'image à partir de l'URL Firebase
            val imageUrl =poule.imageUrl
            if (imageUrl!!.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(imageUrl)
                    .into(avatarImageView)

            } else {
                avatarImageView.setImageResource(R.drawable.poule1)
            }
        }
    }
}
