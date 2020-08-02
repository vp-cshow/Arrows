package com.example.arrows

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.UnknownHostException

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        login_button.setOnClickListener {
            login()
        }
        register_button.setOnClickListener {
            register()
        }
    }

    fun login() {
        if (password_login.text.toString().equals("") || email_login.text.toString().equals("")) {
            Toast.makeText(applicationContext, "Make sure the e-mail/password fields are non-empty!", Toast.LENGTH_LONG).show()
            return
        }
        val client = OkHttpClient()
        val url = "http://10.0.2.2:8080/api/v1/users/authUser?username=" + email_login.text + "&password=" + password_login.text
        val body = JSONObject()
        Toast.makeText(applicationContext, "Connecting to server...", Toast.LENGTH_SHORT).show()
        val request = Request.Builder().post(RequestBody.create(MediaType.get("application/json;charset=utf-8"), body.toString()))
            .url(url).build()
        val response = client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response) {
                val resp = response.body()!!.string()
                if (resp.contains("ok")) {
                    val intent = Intent(baseContext, MainActivity::class.java)
                    intent.putExtra("USER", email_login.text.toString())
                    startActivity(intent)
                }
                else if (resp.contains("bad")) {
                    runOnUiThread{Toast.makeText(applicationContext, "The password was invalid.", Toast.LENGTH_SHORT).show()}
                }
                else if (resp.contains("noUser")) {
                    runOnUiThread{Toast.makeText(applicationContext, "This username didn't exist.", Toast.LENGTH_SHORT).show()}
                }
            }

            override fun onFailure(call: Call?, e: IOException?) {
                runOnUiThread{Toast.makeText(applicationContext, "Connection failed.", Toast.LENGTH_SHORT).show()}
                Log.e("Connection to Server failed: ", e.toString())
            }
        })
    }

    fun register() {
        if (password_login.text.toString().equals("") || email_login.text.toString().equals("")) {
            Toast.makeText(applicationContext, "Make sure the e-mail/password fields are non-empty!", Toast.LENGTH_LONG).show()
            return
        }
        val client = OkHttpClient()
        val url = "http://10.0.2.2:8080/api/v1/users?username=" + email_login.text + "&password=" + password_login.text
        val body = JSONObject()
        Toast.makeText(applicationContext, "Connecting to server...", Toast.LENGTH_SHORT).show()
        val request = Request.Builder().post(RequestBody.create(MediaType.get("application/json;charset=utf-8"), body.toString()))
            .url(url).build()
        val response = client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response) {
                val resp = response.body()!!.string()
                if (resp.contains("ok")) {
                    val intent = Intent(baseContext, MainActivity::class.java)
                    intent.putExtra("USER", email_login.text.toString())
                    startActivity(intent)
                }
                else {
                    runOnUiThread{Toast.makeText(applicationContext, "The username was already taken.", Toast.LENGTH_SHORT).show()}
                }

            }

            override fun onFailure(call: Call?, e: IOException?) {
                runOnUiThread{Toast.makeText(applicationContext, "Connection failed.", Toast.LENGTH_SHORT).show()}
                Log.e("somehintg", e.toString())
            }
        })
    }

}
