package com.example.arrows

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navi_header.view.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
JoinCreateRoomFragment.OnFragmentInteractionListener, RecyclerFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(room: RoomData, position: Int) {
        Toast.makeText(applicationContext, "Peak Observers: " + room.peakListeners.toString(), Toast.LENGTH_LONG).show()
    }

    companion object {
        lateinit var loggedInUsername: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loggedInUsername = intent.getStringExtra("USER")!!
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportFragmentManager.beginTransaction().replace(R.id.meContainer, JoinCreateRoomFragment(), "JoinCreate").addToBackStack(null)
            .commit()
        val toggle = ActionBarDrawerToggle(this, mainAct, toolbar, R.string.open_nav, R.string.close_nav)
        mainAct.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
        val headerView = navView.getHeaderView(0)
        headerView.findViewById<TextView>(R.id.username_nav).text = loggedInUsername
        Toast.makeText(applicationContext, "Successfully logged in as: " + loggedInUsername, Toast.LENGTH_LONG).show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.nav_logout -> {
                val intent = Intent(baseContext, LoginActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_main -> {
                supportFragmentManager.beginTransaction().replace(R.id.meContainer, JoinCreateRoomFragment(), "JoinCreate").commit()
            }

            R.id.nav_history -> {
                val client = OkHttpClient()
                val url = "http://10.0.2.2:8080/api/v1/room/" + loggedInUsername
                val body = JSONObject() // This lib needs a body for the post to go through (don't ask.)

                val request = Request.Builder().get()
                    .url(url).build()
                val response = client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call?, response: Response) {
                        val resp = response.body()!!.string()
                        val frag = RecyclerFragment.newInstance(resp.toString(), "")
                        supportFragmentManager.beginTransaction().replace(R.id.meContainer, frag, "JoinCreate").addToBackStack(null)
                            .commit()
                    }

                    override fun onFailure(call: Call?, e: IOException?) {
                        runOnUiThread{ Toast.makeText(applicationContext, "Connection failed.", Toast.LENGTH_SHORT).show()}
                        Log.e("Connection to Server failed: ", e.toString())
                    }
                })
            }


        }
        mainAct.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if(mainAct.isDrawerOpen(GravityCompat.START)){
            mainAct.closeDrawer(GravityCompat.START)
        }
        else
            super.onBackPressed()
    }

    override fun onFragmentInteraction(uri: Uri) {

    }
}
