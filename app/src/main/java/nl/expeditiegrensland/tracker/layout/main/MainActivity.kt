package nl.expeditiegrensland.tracker.layout.main

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import nl.expeditiegrensland.tracker.R
import nl.expeditiegrensland.tracker.helpers.ActivityHelper
import nl.expeditiegrensland.tracker.helpers.PreferenceHelper
import nl.expeditiegrensland.tracker.services.LocationUpdateService

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        private const val PERM_FINE_LOCATION = 13
    }

    private var pagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERM_FINE_LOCATION)

        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        pagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = pagerAdapter
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
    }

    override fun onStart() {
        super.onStart()

        PreferenceHelper.getPreferences(this)?.registerOnSharedPreferenceChangeListener(this)

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERM_FINE_LOCATION ->
                if (permissions[0] != Manifest.permission.ACCESS_FINE_LOCATION || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    throw Error()
        }
    }


    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(pos: Int) = StatusFragment() // Change to when(pos) for multiple tabs

        override fun getCount() = 1
    }

    private inner class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val location: Location? = intent.getParcelableExtra(LocationUpdateService.EXTRA_LOCATION)
            if (location != null) {
                Toast.makeText(this@MainActivity, "(" + location.latitude + ", " + location.longitude + ")", Toast.LENGTH_SHORT)
                        .show()
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key?.equals(PreferenceHelper.KEY_IS_REQUESTING_UPDATES) == true)
            setUpdatesButtonState(PreferenceHelper.getIsRequestingUpdates(this))
    }

    private fun setUpdatesButtonState(requesting: Boolean) {
        fab.setImageDrawable(
                ContextCompat.getDrawable(this,
                        if (requesting) android.R.drawable.ic_media_pause
                        else android.R.drawable.ic_media_play)
        )
    }
}
