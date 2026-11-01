package com.gitexplorerbox.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gitexplorerbox.app.databinding.ItemSectionBinding

class SectionAdapter(
    private val sections: List<Section>,
    private val listener: OnSectionClickListener
) : RecyclerView.Adapter<SectionAdapter.ViewHolder>() {

    interface OnSectionClickListener {
        fun onSectionClick(section: Section)
    }

    class ViewHolder(val binding: ItemSectionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val section = sections[position]
        val b = holder.binding
        b.title.text = section.title
        b.subtitle.text = if (section.localOnly) {
            b.root.context.getString(R.string.section_favorites_subtitle)
        } else {
            "Поиск GitHub: ${section.query}"
        }
        holder.root.setOnClickListener { listener.onSectionClick(section) }
    }

    override fun getItemCount() = sections.size
}
