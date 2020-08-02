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
import kotlinx.android.synthetic.main.activity_room.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class CreateRoomDialog : DialogFragment() {
    lateinit var editText : EditText
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var builder = androidx.appcompat.app.AlertDialog.Builder(context!!)
        editText = EditText(context)
        editText.inputType = InputType.TYPE_CLASS_TEXT
        editText.hint = "Title"
        editText.gravity = Gravity.CENTER
        builder.setTitle("Create Room")
            .setView(editText)
            .setMessage("Set the name of the room.")
            .setPositiveButton("Yes", DialogInterface.OnClickListener {
                    interf, i ->
                tryCreate()
            }
            )
            .setNegativeButton("No") { interf, i ->
                // Nothing to do
            }

        return builder.create()
    }

    fun tryCreate() {
        if (editText.text.toString().equals("")) {
            Toast.makeText(context, "Make sure the title is non-empty!", Toast.LENGTH_LONG).show()
            return
        }
        val client = OkHttpClient()
        val url = "http://10.0.2.2:8080/api/v1/room?creator=" + MainActivity.loggedInUsername + "&roomTitle=" + editText.text.toString()
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
                    val intent = Intent(activity, RoomActivity::class.java)
                    intent.putExtra("ROOMCODE", code)
                    intent.putExtra("TITLE", editText.text.toString())
                    intent.putExtra("CREATOR", MainActivity.loggedInUsername)
                    startActivity(intent)
                }
                else  {
                    activity?.runOnUiThread{ Toast.makeText(context, "Couldn't create the room.", Toast.LENGTH_SHORT).show()}
                }

            }

            override fun onFailure(call: Call?, e: IOException?) {
                activity?.runOnUiThread{ Toast.makeText(context, "Connection failed.", Toast.LENGTH_SHORT).show()}
                Log.e("Connection to Server failed: ", e.toString())
            }
        })
    }
}
