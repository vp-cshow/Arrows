package com.example.arrows

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class JoinRoomDialog : DialogFragment() {
    lateinit var editText : EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var builder = androidx.appcompat.app.AlertDialog.Builder(context!!)
        editText = EditText(context)
        editText.inputType = InputType.TYPE_CLASS_TEXT
        editText.hint = "Room Code"
        editText.gravity = Gravity.CENTER
        builder.setView(editText)
            .setTitle("Join Room")
            .setMessage("Enter the Room Code provided by the presenter:")
            .setPositiveButton("Enter", DialogInterface.OnClickListener {
                    interf, i ->
                tryJoin()
            })

        return builder.create()
    }

    private fun tryJoin() {
        if (editText.text.toString().equals("")) {
            Toast.makeText(context, "Make sure the Room Code is non-empty!", Toast.LENGTH_LONG).show()
            return
        }
        val client = OkHttpClient()
        val url = "http://10.0.2.2:8080/api/v1/joinRoom?sender=" + MainActivity.loggedInUsername + "&roomCode=" + editText.text.toString()
        val body = JSONObject() // This lib needs a body for the post to go through (don't ask.)
        Toast.makeText(context, "Connecting to server...", Toast.LENGTH_SHORT).show()
        val request = Request.Builder().post(RequestBody.create(MediaType.get("application/json;charset=utf-8"), body.toString()))
            .url(url).build()
        val response = client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response) {
                val resp = response.body()!!.string()
                val asJson = JSONObject(resp)
                if (resp.contains("roomCode")) {
                    val code = asJson.get("roomCode") as String
                    val title = asJson.get("title") as String
                    val creator = asJson.get("creator") as String
                    val intent = Intent(activity, RoomActivity::class.java)
                    intent.putExtra("ROOMCODE", code)
                    intent.putExtra("TITLE", title)
                    intent.putExtra("CREATOR", creator)
                    startActivity(intent)
                }
                else  {
                    activity?.runOnUiThread{ Toast.makeText(context, "The code was invalid.", Toast.LENGTH_SHORT).show()}
                }

            }

            override fun onFailure(call: Call?, e: IOException?) {
                activity?.runOnUiThread{ Toast.makeText(context, "Connection failed.", Toast.LENGTH_SHORT).show()}
                Log.e("Connection to Server failed: ", e.toString())
            }
        })
    }
}
