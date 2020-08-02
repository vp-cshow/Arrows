package com.example.arrows

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_list_rooms.*
import kotlinx.android.synthetic.main.fragment_rooms.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [RecyclerFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [RecyclerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecyclerFragment : Fragment(), RecyclerViewAdapter.MyItemClickListener  {

    override fun onItemLongClickedFromAdapter(position: Int) {
        // Do nothing
    }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    lateinit var adapter: RecyclerViewAdapter
    var lastClickedPosition: Int = 0
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        val gson = Gson()
        val array = gson.fromJson<Array<RoomData>>(param1, Array<RoomData>::class.java)
        val arrayList = ArrayList(array.toMutableList())
        adapter = RecyclerViewAdapter(context!!, arrayList)
    }

    override fun onItemClickedFromAdapter(room : RoomData) {
        onItemClicked(room, 0) // unused position
    }

    fun onItemClicked(room : RoomData, position: Int) {
        listener?.onFragmentInteraction(room, position)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView2.adapter = adapter
        listener = activity as MainActivity
        adapter.setMyItemClickListener(this)
        var layoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager
        layoutManager = androidx.recyclerview.widget.LinearLayoutManager(view.context)
        listView2.hasFixedSize()
        listView2.layoutManager = layoutManager
        adapter.setMyItemClickListener(this)
        listView2.adapter = adapter // default Item Animator
        listView2.itemAnimator?.addDuration = 1000L
        listView2.itemAnimator?.removeDuration = 1000L
        listView2.itemAnimator?.moveDuration = 1000L
        listView2.itemAnimator?.changeDuration = 1000L
    }
    
    

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rooms, container, false)
    }
    

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
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
        fun onFragmentInteraction(room : RoomData, position: Int)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RecyclerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecyclerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
