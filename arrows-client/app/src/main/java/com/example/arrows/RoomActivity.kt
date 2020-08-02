package com.example.arrows

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_room.*
import kotlinx.android.synthetic.main.join_dialog.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class RoomActivity : AppCompatActivity(),SwipePadFragment.OnFragmentInteractionListener  {
    override fun updateListenerCount() {
        listenerCountView.text = "Listeners: " + listenerCount.toString()
    }

    override fun killMe() {
        Looper.prepare()
        Toast.makeText(this.applicationContext, "The room was closed by the host.", Toast.LENGTH_LONG).show()
        finish()
    }

    companion object {
        lateinit var hostUsername: String
        lateinit var roomTitle: String
        lateinit var roomCode: String
        var listenerCount = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

        roomCode = intent.getStringExtra("ROOMCODE")!!
        roomTitle = intent.getStringExtra("TITLE")!!
        hostUsername = intent.getStringExtra("CREATOR")!!


        roomCodeView.text = "Room Code: " + roomCode
        roomTitleView.text = "Room Title: " + roomTitle
        creatorView.text = "Created By: " + hostUsername
        listenerCountView.text = "Listeners: " + listenerCount.toString()

        button.setOnClickListener {
            val client = OkHttpClient()
            val url = "http://10.0.2.2:8080/api/v1/exitRoom?roomCode="+ roomCode + "&sender=" + MainActivity.loggedInUsername
            val body = JSONObject()

            val request = Request.Builder().post(RequestBody.create(MediaType.get("application/json;charset=utf-8"), body.toString()))
                .url(url).build()
            val response = client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call?, response: Response) {
                    val resp = response.body()!!.string()
                    val asJson = JSONObject(resp)
                    if (asJson.getString("result").equals("ok")) {
                        finish()
                    }
                }
                override fun onFailure(call: Call?, e: IOException?) {
                    runOnUiThread{Toast.makeText(applicationContext, "Connection failed.", Toast.LENGTH_SHORT).show()}
                    Log.e("Connection to Server failed: ", e.toString())
                }
            })
        }


        supportFragmentManager.beginTransaction()
            .add(R.id.padContainer, SwipePadFragment(), "SwipePad")
            .commit()
    }




}
