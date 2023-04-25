package com.example.symptomease

import android.os.Bundle
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
    lateinit var retrofitLogin: Retrofit
    lateinit var retrofit: Retrofit
    var listSymptoms = mutableListOf<String>()

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
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

    }

    fun onSendClick(view: View){
        if(spinnerValue.equals("")){
            Toast.makeText(this, "Select a valid symptom", Toast.LENGTH_SHORT).show()
        } else {



            /*
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
            dialog.show() */
        }
    }

    fun apiLogin(){
        val login = retrofitLogin.create(LoginService::class.java)

         login.login().enqueue(object: Callback<Login>{
            override fun onResponse(call: Call<Login>, response: Response<Login>) {
                val body = response.body()
                if (body != null) {
                    println(body.Token)
                    updateSpinner(body.Token)
                }
            }

            override fun onFailure(call: Call<Login>, t: Throwable) {
            }

        })

    }

    fun updateSpinner(token :String){
        println(token)
        val symptomsList = retrofit.create(SymptomsListService::class.java)
        symptomsList.getAllSymptoms(token, "en-gb").enqueue(object : Callback<SymptomsList> {
            override fun onResponse(
                call: Call<SymptomsList>,
                response: Response<SymptomsList>
            ) {
                println(response.message())
                println(response.code())
                if (response.body() != null) {
                    for (symptom in response.body()!!.listOfSymptoms) {
                        listSymptoms.add(symptom.symptomName)
                    }
                    finishUpdate()
                }
            }

            override fun onFailure(call: Call<SymptomsList>, t: Throwable) {

            }

        })


    }

    fun finishUpdate(){
        println("here")
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
}