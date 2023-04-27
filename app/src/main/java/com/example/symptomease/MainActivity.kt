package com.example.symptomease

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.Xml
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.symptomease.databinding.ActivityMainBinding
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigInteger
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var spinnerValue : String = ""
    private var tokenUsed : String = ""
    private var spinnerValueID : Int = 0
    private var gender : String = ""
    private var year_of_birth = 0
    lateinit var retrofitLogin: Retrofit
    lateinit var retrofit: Retrofit
    var listSymptoms = mutableListOf<String>()
    var listSymptomsID = mutableListOf<Int>()

    private val TAG = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retrofit = Retrofit.Builder()
            .baseUrl(uri)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofitLogin = Retrofit.Builder()
            .baseUrl(loginUri)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiLogin()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_saved_symptoms, R.id.navigation_dashboard, R.id.navigation_diagnostics
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        val spinner = findViewById<Spinner>(R.id.spinner)


        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                spinnerValue = listSymptoms.get(p2)
                spinnerValueID = listSymptomsID.get(p2)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

    }

    fun onSendClick(view: View){
        if(spinnerValue.equals("")){
            Toast.makeText(this, "Select a valid symptom", Toast.LENGTH_SHORT).show()
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
//                            //since specialisation is an array, indicate the index with loop
//                       // response.body()?.specialisation[0].Name)
                        }
                    }

                    override fun onFailure(call: Call<List<Diagnostics>?>, t: Throwable) {
                        Log.d(TAG, "onFailure:error ")
                    }
                })


//            val diagnostics = retrofit.create(DiagnosticsService::class.java)
//            diagnostics.getDiagnosis(tokenUsed, "en-gb", spinnerValueID, gender, year_of_birth)
//                .enqueue(object : Callback<Diagnostics?> {
//                override fun onResponse(call: Call<Diagnostics?>,
//                                        response: Response<Diagnostics?>) {
//                    val responseBody = response.body()
//
//                    if(responseBody != null) {
//                        displayDialog(responseBody.issue, responseBody.specialisation[0])
//                            //since specialisation is an array, indicate the index with loop
//                       // response.body()?.specialisation[0].Name)
//                    }
//                }
//
//                override fun onFailure(call: Call<Diagnostics?>, t: Throwable) {
//
//                }
//            })


        }
    }

    //jalaun ross
    //icdNAme = diagnosis
    //it'll just diagnose you, it doesn't connect you to a professional
    //however, it does give you an accuracy
    //it'll be up to the user to find a professional for that

    //Jalaun and Antonio
    fun displayDialog(issue : Issue, specialisation : Specialist ){

        val sharedPreferences = getSharedPreferences(
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
        val builder = AlertDialog.Builder(this)
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


    //Jalaun Ross 4/27/2023
    //To fix the problem with the spinner clearing data when transferring fragments, we can use
    //viewmodel.
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
    //SHARED PREFERENCE CODE NOT DONE

    /**
     * ANTONIO COMMENT
     * So it needs to save the three variables (Condition, Triage, Specialist) for each diagnostic that we get from the symptom.
     * I have an idea that at least covers one, but I’m not sure how to create it so it works for all diagnostics.
     * I’ll send the code that I added into one of the functions, it’s tied to the send button
     * fun onSendClick(view: View){
    if(spinnerValue.equals("")){
    Toast.makeText(this, "Select a valid symptom", Toast.LENGTH_SHORT).show()
    } else {

    /*
    val sharedPreferences = getSharedPreferences("Diagnostics", MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    val condition : String
    val triage : String
    val specialist : String


    val builder = AlertDialog.Builder(this)
    builder.setTitle("Diagnostics")
    builder.setMessage("Symptom: $spinnerValue\n" +
    "Condition: $condition\n" +
    "Triage level: $triage\n" +
    "Specialist Recommendation: $specialist")

    builder.setNeutralButton("Cancel"){dialog, _ ->
    dialog.cancel()
    }
    // create the dialog and show it
    val dialog = builder.create()
    dialog.show()

    editor.putString("Condition", condition)
    editor.putString("Triage", triage)
    editor.putString("Specialist", specialist)
    editor.apply()
    */
    }
    }
     */

    fun finishUpdate(){
        val spinner = findViewById<Spinner>(R.id.spinner)
        val symptomsAdapter = this?.let {
            ArrayAdapter<String>(it, android.R.layout.simple_spinner_dropdown_item, listSymptoms) }
        spinner.adapter = symptomsAdapter
    }

    companion object varables{
        const val loginUri = "https://sandbox-authservice.priaid.ch/"
        const val uri = "https://sandbox-healthservice.priaid.ch/"
        const val api_key = "pault@my.ccsu.edu"


        const val hash = "7da2b1034cd58b44b25fe4dd87a32695"
        const val hash2 = "3107d7589cfa06c71009935ead23dd9c"

        const val loginHeader = "Authorization: Bearer $api_key:$hash2"
    }

    override fun onResume() {
        super.onResume()
        if (!tokenUsed.equals(""))
            updateSpinner(tokenUsed)
    }
}