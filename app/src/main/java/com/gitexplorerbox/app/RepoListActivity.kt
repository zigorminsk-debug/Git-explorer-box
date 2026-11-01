package com.gitexplorerbox.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.gitexplorerbox.app.databinding.ActivityRepoListBinding

class RepoListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRepoListBinding
    private lateinit var adapter: RepoAdapter
    private var showingFavorites = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepoListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra(EXTRA_TITLE) ?: getString(R.string.app_name)
        showingFavorites = intent.getBooleanExtra(EXTRA_FAVORITES, false)

        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter = RepoAdapter(
            mutableListOf(),
            { repo -> openDetail(repo) },
            { _, _ -> } // состояние звёзды обновляется внутри адаптера
        )
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = adapter

        binding.swipe.setOnRefreshListener { load() }
        binding.btnRetry.setOnClickListener { load() }

        load()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun load() {
        if (showingFavorites) {
            val favorites = Bookmarks.all(this)
            adapter.setItems(favorites)
            binding.swipe.isRefreshing = false
            if (favorites.isEmpty()) {
                binding.progress.visibility = View.GONE
                binding.recycler.visibility = View.GONE
                binding.error_container.visibility = View.VISIBLE
                binding.error_message.text = getString(R.string.favorites_empty)
            } else {
                binding.progress.visibility = View.GONE
                binding.error_container.visibility = View.GONE
                binding.recycler.visibility = View.VISIBLE
            }
            return
        }

        val query = intent.getStringExtra(EXTRA_QUERY) ?: ""
        binding.progress.visibility = View.VISIBLE
        binding.recycler.visibility = View.GONE
        binding.error_container.visibility = View.GONE

        GitHubApi.searchRepositories(this, query, 1, object : GitHubApi.Callback<List<Repo>> {
            override fun onSuccess(result: List<Repo>) {
                runOnUiThread {
                    adapter.setItems(result)
                    binding.progress.visibility = View.GONE
                    binding.swipe.isRefreshing = false
                    binding.recycler.visibility = if (result.isEmpty()) View.GONE else View.VISIBLE
                    if (result.isEmpty()) {
                        binding.error_container.visibility = View.VISIBLE
                        binding.error_message.text = getString(R.string.no_repos_in_section)
                    } else {
                        binding.error_container.visibility = View.GONE
                    }
                }
            }

            override fun onError(error: String) {
                runOnUiThread {
                    binding.progress.visibility = View.GONE
                    binding.swipe.isRefreshing = false
                    binding.error_container.visibility = View.VISIBLE
                    binding.error_message.text = error
                }
            }
        })
    }

    private fun openDetail(repo: Repo) {
        val intent = Intent(this, RepoDetailActivity::class.java).apply {
            putExtra(RepoDetailActivity.EXTRA_FULL_NAME, repo.fullName)
            putExtra(RepoDetailActivity.EXTRA_HTML_URL, repo.htmlUrl)
            putExtra(RepoDetailActivity.EXTRA_TITLE, repo.name)
        }
        startActivity(intent)
    }

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_QUERY = "extra_query"
        const val EXTRA_FAVORITES = "extra_favorites"
    }
}
