package com.gitexplorerbox.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), SectionAdapter.OnSectionClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = getString(R.string.app_name)

        val adapter = SectionAdapter(Section.seeds, this)
        val recycler: RecyclerView = findViewById(R.id.recycler)
        recycler.adapter = adapter

        val fabSearch: FloatingActionButton = findViewById(R.id.fab_search)
        fabSearch.setOnClickListener {
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
