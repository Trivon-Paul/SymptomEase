package com.example.symptomease.ui.diagnostics

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.symptomease.R
import com.example.symptomease.databinding.FragmentDiagnosticsBinding

class DiagnosticsFragment : Fragment() {


    private var _binding: FragmentDiagnosticsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

        //HELP! Unresolved reference:getSharedPreferences error



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val notificationsViewModel =
            ViewModelProvider(this).get(DiagnosticsViewModel::class.java)

        _binding = FragmentDiagnosticsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //jalaun ross 4/27/2023
        //ok so another weird thing here, when you use view binding it generates a file with
        //properties corresponding to each View in the layoutfile, however,
        //the underscores are replaced with camel case.that's why
        //        val conditionTextView = binding.condition_textview doesn't work,
        //but val conditionTextView = binding.conditionTextview works.
        //based on this, i'm removing underscores in the fragment_diagnostics id's and replacing it with camel case.

        //making a variable for each text view id
        var conditionTextView = binding.conditionTextview
        var IcdNameTextView = binding.IcdName
        val profNameTextView = binding.ProfName
        val accuracyTextView = binding.Accuracy





        //getSharedPreferences doesn't work for fragments, using requireContext().getSharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("preferences", Context.MODE_PRIVATE) //create shared preferences object. allows us to read and write key-value pairs
        val Name = sharedPreferences.getString("Name","  ")
        val IcdName = sharedPreferences.getString("IcdName","  ")
        val ProfName = sharedPreferences.getString("ProfName","  ")
        val Accuracy = sharedPreferences.getString("Accuracy","  ")


        //setting the text views to the appropriate information in shared preferences
        conditionTextView.text = Name
        IcdNameTextView.text = IcdName
        profNameTextView.text = ProfName
        accuracyTextView.text = Accuracy


        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}