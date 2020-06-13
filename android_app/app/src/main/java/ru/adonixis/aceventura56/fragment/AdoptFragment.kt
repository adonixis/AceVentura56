package ru.adonixis.aceventura56.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_adopt.*
import ru.adonixis.aceventura56.R
import ru.adonixis.aceventura56.adapter.PetsAdapter
import ru.adonixis.aceventura56.model.Pet


class AdoptFragment : Fragment() {
    companion object {
        private const val TAG = "AdoptFragment"
    }

    private val pets: ArrayList<Pet> = ArrayList()
    private lateinit var petsAdapter: PetsAdapter
    private lateinit var remoteDB: FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_adopt, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val numberOfColumns = 2
        recyclerPets.layoutManager = GridLayoutManager(activity, numberOfColumns)
        recyclerPets.setHasFixedSize(true)
        petsAdapter = PetsAdapter(requireContext(), pets)
        recyclerPets.adapter = petsAdapter

        remoteDB = Firebase.firestore

        refreshPets()

        swipeRefresh.setOnRefreshListener { refreshPets() }
    }

    private fun refreshPets() {
        swipeRefresh.isRefreshing = true
        remoteDB.collection("petRequests")
            .get()
            .addOnSuccessListener { result ->
                val petsList: List<Pet> = result.toObjects(Pet::class.java)
                swipeRefresh.isRefreshing = false
                pets.clear()
                pets.addAll(petsList)
                petsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }
}