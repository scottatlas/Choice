package com.example.choice

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.util.*

//Daniel Kathiresan
class RegisterFragment : Fragment() {
    //Init variables
    private lateinit var email: EditText
    private lateinit var fname: EditText
    private lateinit var lname: EditText
    private lateinit var password: EditText
    private lateinit var cnfrmPassword: EditText
    private lateinit var fAuth: FirebaseAuth
    private lateinit var regBtn: Button
    private lateinit var refUsers: DatabaseReference
    private var firebaseUserID : String = ""
    private lateinit var usergndr: Spinner
    private lateinit var prefgndr: Spinner
    private var genPref: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        //Variables for user info
        email = view.findViewById(R.id.reg_email)
        fname = view.findViewById(R.id.reg_fname)
        lname = view.findViewById(R.id.reg_lname)
        password = view.findViewById(R.id.reg_password)
        cnfrmPassword = view.findViewById(R.id.reg_cnfrm_password)
        fAuth = Firebase.auth
        regBtn = view.findViewById(R.id.create_account_btn)
        usergndr = view.findViewById(R.id.gender_spinner)
        prefgndr = view.findViewById(R.id.preference_spinner)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            usergndr.adapter = adapter
        }
        //Same array adapter but for gender preference
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.preference_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            prefgndr.adapter = adapter
        }



        view.findViewById<Button>(R.id.return_login_btn).setOnClickListener {
            val navRegister = activity as FragmentNavigation
            navRegister.navigateFrag(LoginFragment(), false)
        }
        //password validation and error checking
        view.findViewById<Button>(R.id.create_account_btn).setOnClickListener {
            //Run validation function when called
            validateEmptyForm()

        }

        return view

    }
        class SpinnerActivity : Activity(), AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
            // An item was selected. You can retrieve the selected item using
            parent.getItemAtPosition(pos)
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
            // Another interface callback
        }
    }
    //Function to provide connectivity and signup to firebase, sends the stored user data
    private fun firebaseSignUp() {
        regBtn.isEnabled = false
        regBtn.alpha = 0.5f
        //May need to create first and last name variables
        fAuth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Sign-up Successful", Toast.LENGTH_SHORT).show()
                    //Save user info as string
                    val firstName = fname.text.toString()
                    val lastName = lname.text.toString()
                    val gender = usergndr.selectedItem.toString()
                    genderTranslate()
//                    val preference = prefgndr.selectedItem.toString()


                    //Get UID, create DB reference with UID, Add to DB
                    firebaseUserID = fAuth.currentUser!!.uid
                    refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserID)

                    val userHashMap = HashMap<String, Any>()
                    userHashMap["uid"] = firebaseUserID
                    userHashMap["first_name"] = firstName
                    userHashMap["last_name"] = lastName
                    userHashMap["gender"] = gender
                    userHashMap["profile_picture"] = "https://firebasestorage.googleapis.com/v0/b/choice-23fc3.appspot.com/o/images%2Fdefaultpfp.png?alt=media&token=7fce8ca7-f830-45f7-a19a-acde736d7711"
                    userHashMap["bio"] = " "
                    userHashMap["gender_pref"] = genPref

                    refUsers.updateChildren(userHashMap)
                        .addOnCompleteListener{ task ->
                            if (task.isSuccessful)
                            {
                                //Navigate to home fragment
//                                val navHome = activity as FragmentNavigation
//                                navHome.navigateFrag(MatchFragment(),addToStack = true)
                                val intent = Intent (activity, BottomNav::class.java)
                                activity?.startActivity(intent)
                            }else{
                                Toast.makeText(context,"Unable to save user information",Toast.LENGTH_SHORT).show()
                            }
                        }

                } else {
                    Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
                    regBtn.isEnabled = true
                    regBtn.alpha = 1.0f
                }
            }
    }

    private fun validateEmptyForm() {
        //Show warning icon (currently placeholder) when a criteria is met
        val icon = AppCompatResources.getDrawable(
            requireContext(),//icon from resources
            R.drawable.warningph
        )

        icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)
        //check string user has entered is not empty
        when {
            TextUtils.isEmpty((email.text.toString().trim())) -> {

                email.setError("Please Enter an Email Address", icon)
            }
            TextUtils.isEmpty((fname.text.toString().trim())) -> {

                email.setError("Please Enter your First Name", icon)
            }
            TextUtils.isEmpty((lname.text.toString().trim())) -> {

                email.setError("Please Enter your Last Name(s)", icon)
            }
            TextUtils.isEmpty((password.text.toString().trim())) -> {

                password.setError("Please Enter Password", icon)
            }
            TextUtils.isEmpty((cnfrmPassword.text.toString().trim())) -> {

                cnfrmPassword.setError("Please Confirm Password", icon)
            }
            //check if field are empty
            email.text.toString().isNotEmpty() &&
                    fname.text.toString().isNotEmpty() &&
                    lname.text.toString().isNotEmpty() &&
                    password.text.toString().isNotEmpty() &&
                    cnfrmPassword.text.toString().isNotEmpty()
            -> {//Check if email is valid format
                if (email.text.toString().matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
                    //Correct if matches format, then check password
                    if (fname.text.toString().matches(Regex("[a-zA-Z]+"))) {
                        if (lname.text.toString().matches(Regex("[a-zA-Z]+"))) {
                            if (password.text.toString().length >= 5) {
                                //If more than 5 characters check confirm password
                                if (password.text.toString() == cnfrmPassword.text.toString()) {

                                    //Firebase signup to register user
                                    firebaseSignUp()
                                    //Toast.makeText(context,"Account Creation Successful",Toast.LENGTH_SHORT).show()
                                } else {
                                    cnfrmPassword.setError("Password Does Not Match", icon)
                                }
                            } else {
                                password.setError("Please enter at least 5 characters", icon)
                            }
                        } else {
                            lname.setError("Name can only contain letters", icon)
                        }
                    } else {
                        fname.setError("Name can only contain letters", icon)
                    }
                } else {
                    email.setError("Please Enter Valid Email Address", icon)
                }
            }//Checks if all fields are valid for registration. Email: Base on format, last and first name: letters only, password: more than 5 chars, confirm password: must match password
        }
    }

    fun genderTranslate(){
        when(prefgndr.selectedItem.toString()) {
            "Men" -> genPref = "Male"
            "Women" -> genPref = "Female"
            "Everyone" -> genPref = "Everyone"
        }
    }
}