package com.gitexplorerbox.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gitexplorerbox.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SectionAdapter.OnSectionClickListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.app_name)

        val adapter = SectionAdapter(Section.seeds, this)
        binding.recycler.adapter = adapter

        binding.fabSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }

    override fun onSectionClick(section: Section) {
        val intent = Intent(this, RepoListActivity::class.java).apply {
            putExtra(RepoListActivity.EXTRA_TITLE, section.title)
            putExtra(RepoListActivity.EXTRA_QUERY, section.query)
            putExtra(RepoListActivity.EXTRA_FAVORITES, section.localOnly)
        }
        startActivity(intent)
    }
}
