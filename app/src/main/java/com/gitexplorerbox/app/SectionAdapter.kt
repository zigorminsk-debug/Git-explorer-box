package com.gitexplorerbox.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SectionAdapter(
    private val sections: List<Section>,
    private val listener: OnSectionClickListener
) : RecyclerView.Adapter<SectionAdapter.ViewHolder>() {

    interface OnSectionClickListener {
        fun onSectionClick(section: Section)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val subtitle: TextView = itemView.findViewById(R.id.subtitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_section, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val section = sections[position]
        holder.title.text = section.title
        holder.subtitle.text = if (section.localOnly) {
            holder.itemView.context.getString(R.string.section_favorites_subtitle)
        } else {
            "Поиск GitHub: ${section.query}"
        }
        holder.itemView.setOnClickListener { listener.onSectionClick(section) }
    }

    override fun getItemCount() = sections.size
}
