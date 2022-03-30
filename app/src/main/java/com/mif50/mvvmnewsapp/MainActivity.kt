package com.mif50.mvvmnewsapp

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mif50.mvvmnewsapp.databinding.ActivityMainBinding
import com.mif50.mvvmnewsapp.features.bookmarks.BookmarksFragment
import com.mif50.mvvmnewsapp.features.breakingnews.BreakingNewsFragment
import com.mif50.mvvmnewsapp.features.searchnews.SearchNewsFragment
import com.mif50.mvvmnewsapp.util.fragmentByTag
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var breakingNewsFragment: BreakingNewsFragment
    private lateinit var searchNewsFragment: SearchNewsFragment
    private lateinit var bookmarksFragment: BookmarksFragment

    private val fragments: Array<Fragment>
        get() = arrayOf(
            breakingNewsFragment,
            searchNewsFragment,
            bookmarksFragment
        )

    private var selectedIndex = 0

    private val selectedFragment get() = fragments[selectedIndex]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingView()
        initFragments(savedInstanceState)
        selectFragment(selectedFragment)
        handleTapInBottomNavigation()
    }

    private fun bindingView() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initFragments(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            createsFragments()
            addingFragments()
        } else {
            findFragmentsByTags()
            getSavedSelectIndex(savedInstanceState)
        }
    }

    private fun createsFragments() {
        breakingNewsFragment = BreakingNewsFragment()
        searchNewsFragment = SearchNewsFragment()
        bookmarksFragment = BookmarksFragment()
    }

    private fun addingFragments() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, breakingNewsFragment, TAG_BREAKING_NEWS_FRAGMENT)
            .add(R.id.fragment_container, searchNewsFragment, TAG_SEARCH_NEWS_FRAGMENT)
            .add(R.id.fragment_container, bookmarksFragment, TAG_BOOKMARKS_FRAGMENT)
            .commit()
    }

    private fun findFragmentsByTags() {
        breakingNewsFragment = fragmentByTag(TAG_BREAKING_NEWS_FRAGMENT) as BreakingNewsFragment
        searchNewsFragment = fragmentByTag(TAG_SEARCH_NEWS_FRAGMENT) as SearchNewsFragment
        bookmarksFragment = fragmentByTag(TAG_BOOKMARKS_FRAGMENT) as BookmarksFragment
    }

    private fun getSavedSelectIndex(savedInstanceState: Bundle) {
         selectedIndex = savedInstanceState.getInt(KEY_SELECTED_INDEX, 0)
    }

    private fun selectFragment(selectedFragment: Fragment) {
        var transaction = supportFragmentManager.beginTransaction()
        fragments.forEachIndexed { index, fragment ->
            if (selectedFragment == fragment) {
                transaction = transaction.attach(fragment)
                selectedIndex = index
            } else {
                transaction = transaction.detach(fragment)
            }
        }
        transaction.commit()
        updateTitle()
    }

    private fun updateTitle() {
        title = when (selectedFragment) {
            is BreakingNewsFragment -> getString(R.string.title_breaking_news)
            is SearchNewsFragment -> getString(R.string.title_search_news)
            is BookmarksFragment -> getString(R.string.title_bookmarks)
            else -> ""
        }
    }

    private fun handleTapInBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            val fragment = getFragmentFromMenu(item)
            if (selectedFragment === fragment && fragment is OnBottomNavigationFragmentReselectedListener) {
                fragment.onBottomNavigationFragmentReselected()
            } else {
                selectFragment(fragment)
            }
            true
        }
    }

    private fun getFragmentFromMenu(item: MenuItem): Fragment {
        val fragment = when (item.itemId) {
            R.id.nav_breaking -> breakingNewsFragment
            R.id.nav_search -> searchNewsFragment
            R.id.nav_bookmarks -> bookmarksFragment
            else -> throw IllegalArgumentException("Unexpected itemId")
        }
        return fragment
    }

    override fun onBackPressed() {
        if (selectedIndex != 0) {
            backToBreakingFragment()
        } else {
            super.onBackPressed()
        }
    }

    private fun backToBreakingFragment() {
        binding.bottomNav.selectedItemId = R.id.nav_breaking
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_SELECTED_INDEX, selectedIndex)
    }

    //TODO: OnBottomNavigationFragmentReselectedListener
    interface OnBottomNavigationFragmentReselectedListener {
        fun onBottomNavigationFragmentReselected()
    }
}

private const val TAG_BREAKING_NEWS_FRAGMENT = "TAG_BREAKING_NEWS_FRAGMENT"
private const val TAG_SEARCH_NEWS_FRAGMENT = "TAG_SEARCH_NEWS_FRAGMENT"
private const val TAG_BOOKMARKS_FRAGMENT = "TAG_BOOKMARKS_FRAGMENT"
private const val KEY_SELECTED_INDEX = "KEY_SELECTED_INDEX"