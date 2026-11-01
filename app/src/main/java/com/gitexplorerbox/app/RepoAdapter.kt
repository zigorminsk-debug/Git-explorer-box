package com.gitexplorerbox.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RepoAdapter(
    private val items: MutableList<Repo>,
    private val onItemClick: (Repo) -> Unit,
    private val onStarToggle: (Repo, Boolean) -> Unit
) : RecyclerView.Adapter<RepoAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)
        val meta: TextView = itemView.findViewById(R.id.meta)
        val star: ImageView = itemView.findViewById(R.id.star)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_repo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repo = items[position]
        val ctx = holder.itemView.context

        holder.title.text = repo.fullName
        holder.description.text = repo.description ?: ctx.getString(R.string.no_description)
        val language = repo.language ?: ctx.getString(R.string.unknown_language)
        holder.meta.text = buildString {
            append(language)
            append("   ")
            append(ctx.getString(R.string.stars_label, repo.stars))
            append("   ")
            append(ctx.getString(R.string.forks_label, repo.forks))
        }
        holder.star.setImageResource(
            if (Bookmarks.isBookmarked(ctx, repo.fullName))
                R.drawable.ic_star_filled else R.drawable.ic_star_border
        )

        holder.itemView.setOnClickListener { onItemClick(repo) }
        holder.star.setOnClickListener {
            val added = Bookmarks.toggle(ctx, repo)
            holder.star.setImageResource(
                if (added) R.drawable.ic_star_filled else R.drawable.ic_star_border
            )
            onStarToggle(repo, added)
        }
    }

    override fun getItemCount() = items.size

    fun setItems(list: List<Repo>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }
}
