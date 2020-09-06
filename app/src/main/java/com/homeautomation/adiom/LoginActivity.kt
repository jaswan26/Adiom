package com.homeautomation.adiom

import android.Manifest

import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.app.LoaderManager.LoaderCallbacks
import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView

import java.util.ArrayList
import android.Manifest.permission.READ_CONTACTS
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_login.*

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(){

    var progressDialog: ProgressDialog?=null

    internal var context: Context? = null



    override fun onStart() {
        super.onStart()
            requestStoragePermission()
    }

    fun netValidator() {
        Handler().postDelayed({

            this.finish()
        }, 2500)

    }

    private fun requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {


        }
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE), 1)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        //Checking the request code of our request
        if (requestCode == 1) {

            //If permission is granted
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                netValidator()
            } else {

            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // Set up the login form.



        email_sign_in_button.setOnClickListener(View.OnClickListener {

//            validateEmail()
            val email=email.text.toString()
            val pass=password.text.toString()
            progressDialog= ProgressDialog.show(this,null,"Please wait....",false,false)

            progressDialog?.show()
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,pass)
                    .addOnCompleteListener {
                        if (!it.isSuccessful) return@addOnCompleteListener

                        progressDialog?.dismiss()
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {

                        progressDialog?.dismiss()
                        Toast.makeText(this," Failed ${it.message}", Toast.LENGTH_LONG).show()
                    }
        })

    }

    private fun validateEmail(): Boolean {
        val email = email.text.toString().trim({ it <= ' ' })

        if (email.matches("-?\\d+(.\\d+)?".toRegex())) {
            if (email.length != 10 || email.isEmpty()) {
                mailTextInput.error= R.string.error_invalid_email.toString()
                mailTextInput.requestFocus()
                return false
            } else {
                //  inputEmail.setError("");

            }

        }



        return true
    }


}