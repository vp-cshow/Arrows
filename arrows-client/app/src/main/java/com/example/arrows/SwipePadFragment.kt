package com.example.arrows

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.arrows.RoomActivity.Companion.roomCode
import kotlinx.android.synthetic.main.activity_room.*
import kotlinx.android.synthetic.main.fragment_swipe_pad.*
import kotlinx.android.synthetic.main.join_dialog.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.abs

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SwipePadFragment : Fragment(), GestureDetector.OnGestureListener, View.OnTouchListener {

    var lastArrowSaw = 0

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event)
        return true
    }

    override fun onShowPress(e: MotionEvent?) {

    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
       return false
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return false
    }

    override fun onFling(
        down: MotionEvent?,
        move: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        Log.e("onFling", "flinged")
        val yVector = down!!.rawY - move!!.rawY
        val xVector = down.rawX- move.rawX
        if (abs(yVector) >= abs(xVector)) {
            // up or down swipe
            if (yVector > 0) {
                Log.e("onFling", "flinged down")
                swipeUp(velocityY, down.rawX, down.rawY)
            }
            else {
                Log.e("onFling", "flinged up")
                swipeDown(velocityY, down.rawX, down.rawY)
            }
        }
        return false
    }

    private fun swipeDown(velocityY: Float, startX: Float ,startY: Float) {
        sendToServer("down", velocityY)
    }

    private fun sendToServer(direction: String, velocityY: Float): Boolean {
        val client = OkHttpClient()
        val url =
            "http://10.0.2.2:8080/api/v1/arrow?sender=" + MainActivity.loggedInUsername +
                    "&roomCode=" + RoomActivity.roomCode +
                    "&direction=" + direction +
                    "&intensity=" + velocityY.toInt()

        val body = JSONObject() // This lib needs a body for the post to go through (don't ask.)
        val request = Request.Builder().post(
            RequestBody.create(
                MediaType.get("application/json;charset=utf-8"),
                body.toString()
            )
        )
            .url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response) {
                val resp = response.body()!!.string()
                val asJson = JSONObject(resp)
                if (!asJson.get("result").equals("ok")) {
                    activity?.runOnUiThread {
                        Toast.makeText(
                            context,
                            "Issue sending arrow event to server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }

            override fun onFailure(call: Call?, e: IOException?) {
                activity?.runOnUiThread {
                    Toast.makeText(
                        context,
                        "Connection failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Log.e("Connection to Server failed: ", e.toString())
            }
        })
        return false
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {

        super.onViewStateRestored(savedInstanceState)
    }

    private fun displayDownArrowDynamically(startX: Int, startY: Int) {
        val view = ImageView(context!!)
        var x = startX
        var y = startY
        val layoutParams = RelativeLayout.LayoutParams(x, y)
        view.setImageResource(R.drawable.arrow_pad)
        view.layoutParams = layoutParams
        relativeLayout.addView(view)
        view.setBackgroundColor(Color.TRANSPARENT)
        view.rotation = 180f
        view.animate().setDuration(1000).alpha(1f)
        view.animate().setDuration(1000).alpha(0f)
        Timer("RemoveImageView", false).schedule(2000) {
            activity?.runOnUiThread { view.visibility = View.GONE }
        }
    }

    private fun swipeUp(velocityY: Float, startX: Float ,startY: Float) {
       // displayUpArrowDynamically(startX, startY)
        sendToServer("up", velocityY)
    }

    private fun displayUpArrowDynamically(startX: Int, startY: Int) {
        // create a new image view
        val view = ImageView(context!!)
        var x = startX
        var y = startY
        // cap the size of the arrow image

        // set image + layout params
        val layoutParams = RelativeLayout.LayoutParams(x.toInt() , y.toInt())
        view.setImageResource(R.drawable.arrow_pad)
        view.setBackgroundColor(Color.TRANSPARENT)
        view.layoutParams = layoutParams
        relativeLayout.addView(view)
        // fade animation
        view.animate().setDuration(1000).alpha(1f)
        view.animate().setDuration(1000).alpha(0f)
        // delayed execution: remove the view once the animation is complete
        Timer("RemoveImageView", false).schedule(2000) {
            activity!!.runOnUiThread { view.visibility = View.GONE }
        }
    }


    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent?) {

    }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    lateinit var gestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        gestureDetector = GestureDetector(this)
        launchBackgroundThread()
    }

    private fun launchBackgroundThread() {
        // spin up bg thread for roomupdates
        val thread = Thread {
            Looper.prepare()
            var run = true
            while (run) {
                val client = OkHttpClient()
                val url =
                    "http://10.0.2.2:8080/api/v1/roomUpdate?sender=" + MainActivity.loggedInUsername +
                            "&roomCode=" + roomCode

                val body =
                    JSONObject() // This lib needs a body for the post to go through (don't ask.)
                val request = Request.Builder().post(
                    RequestBody.create(
                        MediaType.get("application/json;charset=utf-8"),
                        body.toString()
                    )
                )
                    .url(url).build()
                Thread.sleep(1500)
                val response = client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call?, response: Response) {
                        val resp = response.body()!!.string()
                        val asJson = JSONObject(resp)
                        try {
                            if (!asJson.get("result").equals("ok")) {
                                activity?.runOnUiThread {
                                    Toast.makeText(
                                        context,
                                        "Issue sending update request to server",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                val listeners = asJson.get("listeners") as String
                                val active = asJson.get("active") as String
                                val recentArrow = asJson.get("mostRecentArrow")

                                // Update Listener count
                                RoomActivity.listenerCount = listeners.toInt()
                                listener?.updateListenerCount()

                                // Close room if closed by host

                                if (active == "false") {
                                    run = false
                                    listener?.killMe()
                                }

                                // play an arrow animation, if the server says there's a new one

                                if (recentArrow.toString() != lastArrowSaw.toString()) {
                                    lastArrowSaw = recentArrow.toString().toInt()
                                    val client2 = OkHttpClient()
                                    val url2 =
                                        "http://10.0.2.2:8080/api/v1/arrowInfo?id=" + lastArrowSaw.toString()

                                    val body2 =
                                        JSONObject()
                                    val request2 = Request.Builder().post(
                                        RequestBody.create(
                                            MediaType.get("application/json;charset=utf-8"),
                                            body.toString()
                                        )
                                    )
                                        .url(url2).build()

                                    val response2 = client.newCall(request2).enqueue(object : Callback {
                                        override fun onResponse(call: Call?, response: Response) {
                                            val resp2 = response.body()!!.string()
                                            val asJson2 = JSONObject(resp2)
                                            val direction = asJson2.getString("direction")
                                            val intensity = asJson2.getString("intensity")
                                            if (direction.equals("up")) {
                                                activity?.runOnUiThread {
                                                    displayUpArrowDynamically(Random().nextInt(2000) + 100, Random().nextInt(2000) + 100)
                                                }
                                            }
                                            else {
                                                activity?.runOnUiThread {
                                                    displayDownArrowDynamically(
                                                        Random().nextInt(2000) + 100,
                                                        Random().nextInt(2000) + 100
                                                    )
                                                }
                                            }

                                        }

                                        override fun onFailure(call: Call, e: IOException) {
                                            e.printStackTrace()
                                        }
                                    })
                                }



                            }
                        }
                        catch (e : JSONException){
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call?, e: IOException?) {
                        activity?.runOnUiThread {
                            Toast.makeText(
                                context,
                                "Connection failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        Log.e("Connection to Server failed: ", e.toString())
                    }
                })
            }
        }
        thread.start()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_swipe_pad, container, false)
        v.setOnTouchListener(this)
        return v
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.updateListenerCount()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
            listener?.updateListenerCount()
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun updateListenerCount()
        fun killMe()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SwipePadFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SwipePadFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
