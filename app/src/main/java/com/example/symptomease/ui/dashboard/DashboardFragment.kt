package com.example.symptomease.ui.dashboard

import android.content.ContentValues.TAG
import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.symptomease.*
import com.example.symptomease.databinding.FragmentDashboardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var spinnerValue : String
    lateinit var retrofit: Retrofit
    lateinit var retrofitLogin: Retrofit
    var listSymptoms = mutableListOf<String>()
    var listSymptomsID = mutableListOf<Int>()
    private var spinnerValueID : Int = 0
    private var tokenUsed : String = ""
    lateinit var viewModel: DashboardViewModel


    companion object varables{
        const val loginUri = "https://sandbox-authservice.priaid.ch/"
        const val uri = "https://sandbox-healthservice.priaid.ch/"
        const val api_key = "pault@my.ccsu.edu"


        const val hash = "7da2b1034cd58b44b25fe4dd87a32695"
        const val hash2 = "3107d7589cfa06c71009935ead23dd9c"

        const val loginHeader = "Authorization: Bearer $api_key:$hash2"
    }


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        retrofit = Retrofit.Builder()
            .baseUrl(uri)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofitLogin = Retrofit.Builder()
            .baseUrl(loginUri)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val spinner = binding.spinner

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                spinnerValue = listSymptoms.get(p2)
                spinnerValueID = listSymptomsID.get(p2)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        apiLogin()

        binding.sendButton.setOnClickListener {
            if(spinnerValue.equals("")){
                Toast.makeText(requireContext(), "Select a valid symptom", Toast.LENGTH_SHORT).show()
                apiLogin()
            } else {

                val diagnostics = retrofit.create(DiagnosticsService::class.java)
                diagnostics.getDiagnosis(tokenUsed, "en-gb", "[$spinnerValueID]", "male", 1990)
                    .enqueue(object : Callback<List<Diagnostics>?> {
                        override fun onResponse(
                            call: Call<List<Diagnostics>?>,
                            response: Response<List<Diagnostics>?>
                        ) {
                            val responseBody = response.body()
                            if(!responseBody.isNullOrEmpty()){
                                val firstResponse = responseBody[0]
                                //HELP NEif(NEEDED HERE
                                displayDialog(firstResponse.issue, firstResponse.specialisation[0])

                                // add to database
                                addToDatabase(spinnerValue, firstResponse.issue.Name)
//                            //since specialisation is an array, indicate the index with loop
//                       // response.body()?.specialisation[0].Name)
                            }
                        }

                        override fun onFailure(call: Call<List<Diagnostics>?>, t: Throwable) {
                            Log.d(TAG, "onFailure:error ")
                        }
                    })


                spinnerValue = ""
                spinnerValueID = 0
            }
        }
        return root
    }
    fun displayDialog(issue : Issue, specialisation : Specialist){

        val sharedPreferences = requireActivity().getSharedPreferences(
            "preferences",
            Context.MODE_PRIVATE
        ) //HELP! I want to put Name, IcdName, ProfName, Accuracy, and Specialisation in shared preferences!
        //got help from antonio
        val editor = sharedPreferences.edit()


        editor.putString("Name",issue.Name)
        editor.putString("IcdName",issue.IcdName)
        editor.putString("ProfName",issue.ProfName)
        editor.putString("Accuracy",issue.Accuracy.toString())
        editor.putString("Specialisation",specialisation.Name)
        editor.apply()

        //i'm getting an error on all editor.putString lines
        //it says "expecting member declaration"


        //fun displayDialog(condition : String, icdName : String, ProfName : String, Accuracy : Int? ){
//
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Diagnostics")
        builder.setMessage("Symptom: $spinnerValue\n" +
                "Name: ${issue.Name}\n" +
                "IcdName: ${issue.IcdName}\n" +
                "ProfName: ${issue.ProfName}\n" +
                "Accuracy: ${issue.Accuracy}\n" +
                "Specialisaition: ${specialisation.Name}\n"+
                "")

        builder.setNeutralButton("Cancel"){dialog, _ ->
            dialog.cancel()
        }
        // create the dialog and show it
        val dialog = builder.create()
        dialog.show()
    }

    fun addToDatabase(symptom : String, issue: String){
        var db = Room.databaseBuilder(requireContext(),
            SymptomRoomDatabase::class.java, "symptoms.db"
        ).build()

        Thread {
            db.savedSymptomsDAO().insertSavedSymptom(SavedSymptomEntity(0,symptom, issue))
        }.start()
    }

    fun apiLogin(){
        val login = retrofitLogin.create(LoginService::class.java)

        login.login().enqueue(object: Callback<Login>{
            override fun onResponse(call: Call<Login>, response: Response<Login>) {
                val body = response.body()
                if (body != null) {
                    updateSpinner(body.Token)
                }
            }

            override fun onFailure(call: Call<Login>, t: Throwable) {
            }

        })

    }

    /*
    1. Separate your data from your UI
       controller by creating a class that
       extends ViewModel
    2. Set up communications between
       your ViewModel and your UI
       controller
    3. Use your ViewModel in your UI
       controller
     */

    fun updateSpinner(token :String){
        tokenUsed = token
        val symptomsList = retrofit.create(SymptomsListService::class.java)
        symptomsList.getAllSymptoms(token, "en-gb").enqueue(object : Callback<List<SymptomsList>?> {
            override fun onResponse(
                call: Call<List<SymptomsList>?>,
                response: Response<List<SymptomsList>?>
            ) {
                val responseBody = response.body();
                if (responseBody != null) {

                    for (i in responseBody) {
                        listSymptoms.add(i.symptomName)
                        listSymptomsID.add(i.symptomID)
                    }
                    finishUpdate()
                }
            }

            override fun onFailure(call: Call<List<SymptomsList>?>, t: Throwable) {

            }
        })
    }

    fun finishUpdate(){
        val spinner = binding.spinner
        val symptomsAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, listSymptoms)
        spinner.adapter = symptomsAdapter
    }

    override fun onResume() {
        super.onResume()
        if (!tokenUsed.equals(""))
            updateSpinner(tokenUsed)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}