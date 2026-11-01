package com.gitexplorerbox.app

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {

    private lateinit var adapter: RepoAdapter

    private lateinit var editQuery: EditText
    private lateinit var btnSearch: Button
    private lateinit var searchProgress: ProgressBar
    private lateinit var searchError: TextView
    private lateinit var searchRecycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        editQuery = findViewById(R.id.edit_query)
        btnSearch = findViewById(R.id.btn_search)
        searchProgress = findViewById(R.id.search_progress)
        searchError = findViewById(R.id.search_error)
        searchRecycler = findViewById(R.id.search_recycler)

        supportActionBar?.title = getString(R.string.search)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter = RepoAdapter(
            mutableListOf(),
            { repo -> openDetail(repo) },
            { _, _ -> }
        )
        searchRecycler.adapter = adapter

        btnSearch.setOnClickListener { performSearch() }
        editQuery.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == 3) { // EditorInfo.IME_ACTION_SEARCH
                performSearch()
                true
            } else {
                false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun performSearch() {
        val query = editQuery.text.toString().trim()
        if (query.isEmpty()) {
            editQuery.error = getString(R.string.search_enter)
            return
        }
        editQuery.error = null

        searchProgress.visibility = View.VISIBLE
        searchError.visibility = View.GONE
        searchRecycler.visibility = View.GONE

        GitHubApi.searchRepositories(this, query, 1, object : GitHubApi.Callback<List<Repo>> {
            override fun onSuccess(result: List<Repo>) {
                runOnUiThread {
                    searchProgress.visibility = View.GONE
                    adapter.setItems(result)
                    searchRecycler.visibility = View.VISIBLE
                    if (result.isEmpty()) {
                        searchError.visibility = View.VISIBLE
                        searchError.text = getString(R.string.empty)
                    } else {
                        searchError.visibility = View.GONE
                    }
                }
            }

            override fun onError(error: String) {
                runOnUiThread {
                    searchProgress.visibility = View.GONE
                    searchError.visibility = View.VISIBLE
                    searchError.text = error
                }
            }
        })
    }

    private fun openDetail(repo: Repo) {
        val intent = android.content.Intent(this, RepoDetailActivity::class.java).apply {
            putExtra(RepoDetailActivity.EXTRA_FULL_NAME, repo.fullName)
            putExtra(RepoDetailActivity.EXTRA_HTML_URL, repo.htmlUrl)
            putExtra(RepoDetailActivity.EXTRA_TITLE, repo.name)
        }
        startActivity(intent)
    }
}
