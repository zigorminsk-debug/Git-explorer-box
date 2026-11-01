package com.gitexplorerbox.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gitexplorerbox.app.databinding.ItemRepoBinding

class RepoAdapter(
    private val items: MutableList<Repo>,
    private val onItemClick: (Repo) -> Unit,
    private val onStarToggle: (Repo, Boolean) -> Unit
) : RecyclerView.Adapter<RepoAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemRepoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRepoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repo = items[position]
        val b = holder.binding
        val ctx = b.root.context

        b.title.text = repo.fullName
        b.description.text = repo.description ?: ctx.getString(R.string.no_description)
        val language = repo.language ?: ctx.getString(R.string.unknown_language)
        b.meta.text = buildString {
            append(language)
            append("   ")
            append(ctx.getString(R.string.stars_label, repo.stars))
            append("   ")
            append(ctx.getString(R.string.forks_label, repo.forks))
        }
        b.star.setImageResource(
            if (Bookmarks.isBookmarked(ctx, repo.fullName))
                R.drawable.ic_star_filled else R.drawable.ic_star_border
        )

        b.root.setOnClickListener { onItemClick(repo) }
        b.star.setOnClickListener {
            val added = Bookmarks.toggle(ctx, repo)
            b.star.setImageResource(
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
