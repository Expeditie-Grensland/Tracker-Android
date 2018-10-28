package nl.expeditiegrensland.tracker.layout.main

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import nl.expeditiegrensland.tracker.R
import nl.expeditiegrensland.tracker.helpers.ActivityHelper
import nl.expeditiegrensland.tracker.helpers.PreferenceHelper

class MainActivity : AppCompatActivity() {

    private var pagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        pagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = pagerAdapter
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                R.id.action_sign_out -> signOut()
                R.id.action_change_expeditie -> changeExpeditie()
                else -> super.onOptionsItemSelected(item)
            }

    private fun signOut(): Boolean {
        PreferenceHelper.removeExpeditie(this)
        PreferenceHelper.removeToken(this)
        ActivityHelper.openLogin(this)
        finish()
        return true
    }

    private fun changeExpeditie(): Boolean {
        PreferenceHelper.removeExpeditie(this)
        ActivityHelper.openExpeditieSelect(this)
        finish()
        return true
    }


    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(pos: Int) = StatusFragment() // Change to when(pos) for multiple tabs

        override fun getCount() = 1

    }
}
