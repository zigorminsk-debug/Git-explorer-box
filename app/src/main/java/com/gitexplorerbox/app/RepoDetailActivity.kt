package com.gitexplorerbox.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RepoDetailActivity : AppCompatActivity() {

    private var fullName = ""
    private var htmlUrl = ""
    private var currentRepo: Repo? = null

    private lateinit var detailTitle: TextView
    private lateinit var detailDescription: TextView
    private lateinit var detailMeta: TextView
    private lateinit var detailCreated: TextView
    private lateinit var detailUpdated: TextView
    private lateinit var detailLicense: TextView
    private lateinit var detailProgress: ProgressBar
    private lateinit var detailError: TextView
    private lateinit var btnOpen: Button
    private lateinit var btnStar: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repo_detail)

        detailTitle = findViewById(R.id.detail_title)
        detailDescription = findViewById(R.id.detail_description)
        detailMeta = findViewById(R.id.detail_meta)
        detailCreated = findViewById(R.id.detail_created)
        detailUpdated = findViewById(R.id.detail_updated)
        detailLicense = findViewById(R.id.detail_license)
        detailProgress = findViewById(R.id.detail_progress)
        detailError = findViewById(R.id.detail_error)
        btnOpen = findViewById(R.id.btn_open)
        btnStar = findViewById(R.id.btn_star)

        fullName = intent.getStringExtra(EXTRA_FULL_NAME) ?: ""
        htmlUrl = intent.getStringExtra(EXTRA_HTML_URL) ?: ""
        val title = intent.getStringExtra(EXTRA_TITLE) ?: fullName

        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        detailTitle.text = title

        btnOpen.setOnClickListener {
            if (htmlUrl.isNotEmpty()) {
                startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(htmlUrl) })
            }
        }

        btnStar.setOnClickListener {
            currentRepo?.let { repo ->
                val added = Bookmarks.toggle(this@RepoDetailActivity, repo)
                btnStar.setImageResource(
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
        detailProgress.visibility = View.VISIBLE
        detailError.visibility = View.GONE
        GitHubApi.repository(this, fullName, object : GitHubApi.Callback<Repo> {
            override fun onSuccess(repo: Repo) = runOnUiThread { show(repo) }
            override fun onError(error: String) = runOnUiThread { showError(error) }
        })
    }

    private fun show(repo: Repo) {
        currentRepo = repo
        detailProgress.visibility = View.GONE
        detailError.visibility = View.GONE

        detailDescription.text = repo.description ?: getString(R.string.no_description)

        val language = repo.language ?: getString(R.string.unknown_language)
        detailMeta.text = buildString {
            append(language)
            append("   ★ ${repo.stars}")
            append("   🍴 ${repo.forks}")
        }

        detailCreated.text = getString(R.string.created, repo.createdAt ?: "—")
        detailUpdated.text = getString(R.string.updated, repo.updatedAt ?: "—")
        detailLicense.text = getString(R.string.license, repo.license ?: "—")

        val starred = Bookmarks.isBookmarked(this, repo.fullName)
        btnStar.setImageResource(
            if (starred) R.drawable.ic_star_filled else R.drawable.ic_star_border
        )
    }

    private fun showError(error: String) {
        detailProgress.visibility = View.GONE
        detailError.visibility = View.VISIBLE
        detailError.text = error
    }

    companion object {
        const val EXTRA_FULL_NAME = "extra_full_name"
        const val EXTRA_HTML_URL = "extra_html_url"
        const val EXTRA_TITLE = "extra_title"
    }
}
