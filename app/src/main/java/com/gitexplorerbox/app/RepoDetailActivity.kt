package com.gitexplorerbox.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.gitexplorerbox.app.databinding.ActivityRepoDetailBinding

class RepoDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRepoDetailBinding
    private var fullName = ""
    private var htmlUrl = ""
    private var currentRepo: Repo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fullName = intent.getStringExtra(EXTRA_FULL_NAME) ?: ""
        htmlUrl = intent.getStringExtra(EXTRA_HTML_URL) ?: ""
        val title = intent.getStringExtra(EXTRA_TITLE) ?: fullName

        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.detailTitle.text = title

        binding.btnOpen.setOnClickListener {
            if (htmlUrl.isNotEmpty()) {
                startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(htmlUrl) })
            }
        }

        binding.btnStar.setOnClickListener {
            currentRepo?.let { repo ->
                val added = Bookmarks.toggle(this@RepoDetailActivity, repo)
                binding.btnStar.setImageResource(
                    if (added) R.drawable.ic_star_filled else R.drawable.ic_star_border
                )
            }
        }

        load()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun load() {
        binding.detailProgress.visibility = View.VISIBLE
        binding.detailError.visibility = View.GONE
        GitHubApi.repository(this, fullName, object : GitHubApi.Callback<Repo> {
            override fun onSuccess(repo: Repo) = runOnUiThread { show(repo) }
            override fun onError(error: String) = runOnUiThread { showError(error) }
        })
    }

    private fun show(repo: Repo) {
        currentRepo = repo
        binding.detailProgress.visibility = View.GONE
        binding.detailError.visibility = View.GONE

        binding.detailDescription.text = repo.description ?: getString(R.string.no_description)

        val language = repo.language ?: getString(R.string.unknown_language)
        binding.detailMeta.text = buildString {
            append(language)
            append("   ★ ${repo.stars}")
            append("   🍴 ${repo.forks}")
        }

        binding.detailCreated.text = getString(R.string.created, repo.createdAt ?: "—")
        binding.detailUpdated.text = getString(R.string.updated, repo.updatedAt ?: "—")
        binding.detailLicense.text = getString(R.string.license, repo.license ?: "—")

        val starred = Bookmarks.isBookmarked(this, repo.fullName)
        binding.btnStar.setImageResource(
            if (starred) R.drawable.ic_star_filled else R.drawable.ic_star_border
        )
    }

    private fun showError(error: String) {
        binding.detailProgress.visibility = View.GONE
        binding.detailError.visibility = View.VISIBLE
        binding.detailError.text = error
    }

    companion object {
        const val EXTRA_FULL_NAME = "extra_full_name"
        const val EXTRA_HTML_URL = "extra_html_url"
        const val EXTRA_TITLE = "extra_title"
    }
}
