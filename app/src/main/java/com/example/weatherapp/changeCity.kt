package com.example.weatherapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.lang.Exception


class ChangeCity : AppCompatActivity() {
    private lateinit var clChangeCountry: ConstraintLayout
    lateinit var it_newZip: TextInputEditText
    private lateinit var bt_changeCountry: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_country)

        clChangeCountry = findViewById(R.id.clChangeCountry)
        it_newZip =  findViewById(R.id.it_newZip)
        bt_changeCountry = findViewById(R.id.bt_changeCountry)

        bt_changeCountry.setOnClickListener { checkCode() }
    }

    private fun checkCode() {
        try {
            if (it_newZip.text.toString().isNullOrBlank()) {
                Snackbar.make(clChangeCountry, "Insert a code please..", Snackbar.LENGTH_LONG).show()
            } else if (it_newZip.text.toString().toInt() > 99999 || it_newZip.text.toString().toInt() < 10000) {
                Snackbar.make(clChangeCountry, "Invalid code. It has to be five digits", Snackbar.LENGTH_LONG).show()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                val zipCode = it_newZip.text.toString()
                intent.putExtra("zip_code", "$zipCode")
                startActivity(intent)
            }
        } catch (e : Exception) {
            Snackbar.make(clChangeCountry, "Invalid code..", Snackbar.LENGTH_LONG).show()
        }
    }
}