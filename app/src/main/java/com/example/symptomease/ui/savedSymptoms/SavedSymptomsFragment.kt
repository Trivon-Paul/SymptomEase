package com.example.symptomease.ui.savedSymptoms

import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.symptomease.MyAdapter
import com.example.symptomease.R
import com.example.symptomease.SavedSymptomEntity
import com.example.symptomease.SymptomRoomDatabase
import com.example.symptomease.databinding.FragmentSavedSymptomsBinding
import com.example.symptomease.ui.dashboard.SymptomDatabase

class SavedSymptomsFragment : Fragment() {

    private var _binding: FragmentSavedSymptomsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var db : SymptomRoomDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(SavedSymptomsViewModel::class.java)

        _binding = FragmentSavedSymptomsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        db = Room.databaseBuilder(
            this.requireContext().applicationContext,
            SymptomRoomDatabase::class.java, "symptoms.db"
        ).build()

        Thread {
            val savedSymptoms = mutableListOf<String>()
            val savedSymptomsDescription = mutableListOf<String>()

            val savedList = db.savedSymptomsDAO().getSavedSymptoms()

            for (item in savedList) {
                savedSymptoms.add(item.symptomName)
                savedSymptomsDescription.add(item.symptomDescription)
            }

            val recycler = root.findViewById<RecyclerView>(R.id.symptomsList)

            recycler.adapter = MyAdapter(savedSymptoms, savedSymptomsDescription)

            recycler.layoutManager = LinearLayoutManager(root.context)
        }.start()


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}