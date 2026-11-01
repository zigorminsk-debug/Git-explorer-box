package com.gitexplorerbox.app

import android.os.Bundle
import android.view.EditorInfo
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.gitexplorerbox.app.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: RepoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.search)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter = RepoAdapter(
            mutableListOf(),
            { repo -> openDetail(repo) },
            { _, _ -> }
        )
        binding.searchRecycler.adapter = adapter

        binding.btnSearch.setOnClickListener { performSearch() }
        binding.editQuery.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
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
        val query = binding.editQuery.text.toString().trim()
        if (query.isEmpty()) {
            binding.editQuery.error = getString(R.string.search_enter)
            return
        }
        binding.editQuery.error = null

        binding.searchProgress.visibility = View.VISIBLE
        binding.search_error.visibility = View.GONE
        binding.searchRecycler.visibility = View.GONE

        GitHubApi.searchRepositories(this, query, 1, object : GitHubApi.Callback<List<Repo>> {
            override fun onSuccess(result: List<Repo>) {
                runOnUiThread {
                    binding.searchProgress.visibility = View.GONE
                    adapter.setItems(result)
                    binding.searchRecycler.visibility = View.VISIBLE
                    if (result.isEmpty()) {
                        binding.search_error.visibility = View.VISIBLE
                        binding.search_error.text = getString(R.string.empty)
                    } else {
                        binding.search_error.visibility = View.GONE
                    }
                }
            }

            override fun onError(error: String) {
                runOnUiThread {
                    binding.searchProgress.visibility = View.GONE
                    binding.search_error.visibility = View.VISIBLE
                    binding.search_error.text = error
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
