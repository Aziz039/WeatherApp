package com.example.weatherapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.BoringLayout.make
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import org.json.JSONObject
import java.lang.Exception
import java.net.URL
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import android.os.Looper





class MainActivity : AppCompatActivity() {
    // declare variables
    private lateinit var clMain: ConstraintLayout
    private lateinit var pb_loading: ProgressBar

    private lateinit var tv_location: TextView
    private lateinit var tv_updateDate: TextView

    private lateinit var tv_weatherCondition: TextView
    private lateinit var tv_temp: TextView
    private lateinit var tv_lowTemp: TextView
    private lateinit var tv_highTemp: TextView

    private lateinit var tv_sunriseValue: TextView
    private lateinit var tv_sunsetValue: TextView
    private lateinit var tv_windValue: TextView
    private lateinit var tv_pressureValue: TextView
    private lateinit var tv_humidityValue: TextView

    private lateinit var card_sunrise: LinearLayout
    private lateinit var card_sunset: LinearLayout
    private lateinit var card_wind: LinearLayout
    private lateinit var card_pressure: LinearLayout
    private lateinit var card_humidity: LinearLayout
    private lateinit var card_refresh: LinearLayout

    private lateinit var it_newZip: TextInputEditText

    private val APIKEY = "759c169b47233d6e56b90c6496b82d4b"
    private var ZIP_CODE = "10001"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // init variables and onClickListener
        initVariables()

        requestAPI()
    }

    // init variables and onClickListener
    fun initVariables() {
        clMain = findViewById(R.id.clMain)
        pb_loading = findViewById(R.id.pb_loading)

        tv_location = findViewById(R.id.tv_location)
        tv_updateDate = findViewById(R.id.tv_updateDate)

        tv_weatherCondition = findViewById(R.id.tv_weatherCondition)
        tv_temp = findViewById(R.id.tv_temp)
        tv_lowTemp = findViewById(R.id.tv_lowTemp)
        tv_highTemp = findViewById(R.id.tv_highTemp)

        tv_sunriseValue = findViewById(R.id.tv_sunriseValue)
        tv_sunsetValue = findViewById(R.id.tv_sunsetValue)
        tv_windValue = findViewById(R.id.tv_windValue)
        tv_pressureValue = findViewById(R.id.tv_pressureValue)
        tv_humidityValue = findViewById(R.id.tv_humidityValue)

        card_sunrise = findViewById(R.id.card_sunrise)
        card_sunset = findViewById(R.id.card_sunset)
        card_wind = findViewById(R.id.card_wind)
        card_pressure = findViewById(R.id.card_pressure)
        card_humidity = findViewById(R.id.card_humidity)
        card_refresh = findViewById(R.id.card_refresh)

//        it_newZip = findViewById(R.id.it_newZip)

        try {
            var zipCode = intent.getStringExtra("zip_code")
            if (!zipCode.isNullOrBlank()) {
                ZIP_CODE = zipCode
            }
        } catch (e: Exception) {
            Log.d("MainActivityTAG", "ln(105) Exception: $e")
        }

        // set onClickListeners for card
        card_refresh.setOnClickListener { updateAllData() }
        tv_location.setOnClickListener { changeLocation() }
    }

    // API request
    private fun requestAPI() {
        pb_loading.visibility = View.VISIBLE
        // we use Coroutine to fetch the data
        CoroutineScope(IO).launch {
            // fetch data
            val data = async { fetchData() }.await()
            // check the data if empty
            if (data.isNotEmpty()) {
                updateUI(data)
            } else {
                Handler(Looper.getMainLooper()).post(Runnable {
                    val toast = Toast.makeText(clMain.context, "Invalid zip code", Toast.LENGTH_SHORT)
                    toast.show()
                })
                Log.d("MainActivityTAG", "ln(105): Data is empty")
                changeLocation()
            }
        }

    }

    private fun fetchData(): String {
        var response = ""
        try {
            response = URL("https://api.openweathermap.org/data/2.5/weather?" +
                    "zip=$ZIP_CODE,us&units=metric&appid=$APIKEY").readText()
            Log.d("MainActivityTAG", "Data fetched successfully")
        } catch (e: Exception) {
            Log.d("MainActivityTAG", "ln(86): Exception $e")
        }
        return response
    }

    private fun updateUI(result: String) {
        try {
            val jsonObject = JSONObject(result)
            val location = jsonObject.getString("name")

            val weatherCondition = jsonObject.getJSONArray("weather")
                .getJSONObject(0).getString("description")
            val temp = jsonObject.getJSONObject("main").getString("temp")
            val lowTemp = jsonObject.getJSONObject("main").getString("temp_min")
            val highTemp = jsonObject.getJSONObject("main").getString("temp_max")

            val sunriseValue = jsonObject.getJSONObject("sys").getString("sunrise")
            val sunsetValue = jsonObject.getJSONObject("sys").getString("sunset")
            val windValue = jsonObject.getJSONObject("wind").getString("speed")
            val pressureValue = jsonObject.getJSONObject("main").getString("pressure")
            val humidityValue = jsonObject.getJSONObject("main").getString("humidity")

            val formatter = DateTimeFormatter.ofPattern("h:mm a")
            val dt_sunrise = Instant.ofEpochSecond(sunriseValue.toLong())
                .atZone(ZoneId.of("PST"))
                .toLocalTime()
            val sunriseValueTime = dt_sunrise.format(formatter)
            val dt_sunset = Instant.ofEpochSecond(sunsetValue.toLong())
                .atZone(ZoneId.of("PST"))
                .toLocalTime()
            val sunsetValueTime = dt_sunset.format(formatter)

            val sdf = SimpleDateFormat("dd/M/yyyy h:mm a")
            val currentDate = sdf.format(Date())

            runOnUiThread(Runnable {
                tv_location.text = "$location, US"
                tv_updateDate.text = "Updates at: $currentDate"
                tv_weatherCondition.text = weatherCondition
                tv_temp.text = "${"%.1f".format(temp.toFloat())}℃"
                tv_lowTemp.text = "Low: ${"%.1f".format(lowTemp.toFloat())}℃"
                tv_highTemp.text = "High: ${"%.1f".format(highTemp.toFloat())}℃"
                tv_windValue.text = windValue
                tv_sunriseValue.text = "$sunriseValueTime"
                tv_sunsetValue.text = "$sunsetValueTime"
                tv_pressureValue.text = pressureValue
                tv_humidityValue.text = humidityValue

            })
            pb_loading.visibility = View.INVISIBLE
        } catch (e: Exception) {
            Log.d("MainActivityTAG", "ln(152): assign to UI.")
            Log.d("MainActivityTAG", "$e")
        }
    }

    private fun updateAllData() {
        requestAPI()
    }
    private fun changeLocation() {
        val intent = Intent(this, ChangeCity::class.java)
        startActivity(intent)
    }
}