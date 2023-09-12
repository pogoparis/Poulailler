package com.example.poulailler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



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
        holder.nomTextView.text = currentPoule.nom
    }

    inner class PouleViewHolder(itemView: View, clickListener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val nomTextView: TextView = itemView.findViewById(R.id.nomTextView)
        init {
            itemView.setOnClickListener{
                clickListener.onItemClick(adapterPosition)
            }
        }
    }

    inner class SetOnItemClickListener(any: Any) {

    }

}
