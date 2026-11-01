package com.gitexplorerbox.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class RepoListActivity : AppCompatActivity() {

    private lateinit var adapter: RepoAdapter
    private var showingFavorites = false

    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var progress: ProgressBar
    private lateinit var recycler: RecyclerView
    private lateinit var errorContainer: LinearLayout
    private lateinit var errorMessage: TextView
    private lateinit var btnRetry: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repo_list)

        swipe = findViewById(R.id.swipe)
        progress = findViewById(R.id.progress)
        recycler = findViewById(R.id.recycler)
        errorContainer = findViewById(R.id.error_container)
        errorMessage = findViewById(R.id.error_message)
        btnRetry = findViewById(R.id.btn_retry)

        val title = intent.getStringExtra(EXTRA_TITLE) ?: getString(R.string.app_name)
        showingFavorites = intent.getBooleanExtra(EXTRA_FAVORITES, false)

        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter = RepoAdapter(
            mutableListOf(),
            { repo -> openDetail(repo) },
            { _, _ -> }
        )
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        swipe.setOnRefreshListener { load() }
        btnRetry.setOnClickListener { load() }

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
            swipe.isRefreshing = false
            if (favorites.isEmpty()) {
                progress.visibility = View.GONE
                recycler.visibility = View.GONE
                errorContainer.visibility = View.VISIBLE
                errorMessage.text = getString(R.string.favorites_empty)
            } else {
                progress.visibility = View.GONE
                errorContainer.visibility = View.GONE
                recycler.visibility = View.VISIBLE
            }
            return
        }

        val query = intent.getStringExtra(EXTRA_QUERY) ?: ""
        progress.visibility = View.VISIBLE
        recycler.visibility = View.GONE
        errorContainer.visibility = View.GONE

        GitHubApi.searchRepositories(this, query, 1, object : GitHubApi.Callback<List<Repo>> {
            override fun onSuccess(result: List<Repo>) {
                runOnUiThread {
                    adapter.setItems(result)
                    progress.visibility = View.GONE
                    swipe.isRefreshing = false
                    recycler.visibility = if (result.isEmpty()) View.GONE else View.VISIBLE
                    if (result.isEmpty()) {
                        errorContainer.visibility = View.VISIBLE
                        errorMessage.text = getString(R.string.no_repos_in_section)
                    } else {
                        errorContainer.visibility = View.GONE
                    }
                }
            }

            override fun onError(error: String) {
                runOnUiThread {
                    progress.visibility = View.GONE
                    swipe.isRefreshing = false
                    errorContainer.visibility = View.VISIBLE
                    errorMessage.text = error
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
