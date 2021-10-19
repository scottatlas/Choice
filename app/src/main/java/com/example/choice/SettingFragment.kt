package com.example.choice

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

private lateinit var fAuth: FirebaseAuth
private var firebaseUserID : String = ""
private lateinit var database: DatabaseReference
private lateinit var biofield : EditText
private lateinit var FirstNamefield : EditText
private lateinit var LastNamefield : EditText
private lateinit var Userimage : ImageView
private var selectedPhotoUri: Uri? = null

class SettingFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_setting,container,false)

        // setting the object

        fAuth = Firebase.auth
        firebaseUserID = fAuth.currentUser!!.uid
        biofield = view.findViewById(R.id.setting_bio_update)
        FirstNamefield = view.findViewById(R.id.FName_text)
        LastNamefield = view.findViewById(R.id.LName_text)
        Userimage = view.findViewById(R.id.setting_user_head_image)
        database = FirebaseDatabase.getInstance().getReference("Users")

        //set button function -- go to the Logout Activity
        view.findViewById<Button>(R.id.setting_button).setOnClickListener {

            Log.d("Setting Fragment", "Go to Logout Fragment")
            val intent = Intent(activity, LogoutActivity::class.java)
            startActivity(intent)
        }

        //button to update the bio
        view.findViewById<Button>(R.id.setting_updatee_button).setOnClickListener(){

            val settingbioupdate = biofield.text.toString()
            val settingFirstName = FirstNamefield.text.toString()
            val settingLastName = LastNamefield.text.toString()

            database.child(firebaseUserID).child("bio").setValue(settingbioupdate).addOnSuccessListener {
                Toast.makeText(context,"Update Complete", Toast.LENGTH_SHORT).show()
            }
            database.child(firebaseUserID).child("first_name").setValue(settingFirstName).addOnSuccessListener {
                Toast.makeText(context,"Update Complete", Toast.LENGTH_SHORT).show()
            }
            database.child(firebaseUserID).child("last_name").setValue(settingLastName).addOnSuccessListener {
                Toast.makeText(context,"Update Complete", Toast.LENGTH_SHORT).show()
            }

        }


        //set button function -- this is the button to start the upload image
        view.findViewById<Button>(R.id.setting_upload_photo).setOnClickListener {
            Log.d("Setting Fragment", "Upload photo")

            uploadPhotoToFirebase()
        }

        //set button function -- Let the imageview catch the image
        view.findViewById<ImageView>(R.id.setting_user_head_image).setOnClickListener {
            Log.d("Setting Fragment", "Try to select photo")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type="image/*"

            // the line is because google is not suggest to use but still work
            startActivityForResult(intent,0)
        }

        //get the user information from the database
        if (firebaseUserID != null){
            database.child(firebaseUserID).get().addOnSuccessListener {
                if (it.exists()){
                    val fname = it.child("first name").value
                    val lname = it.child("last name").value
                    val gender = it.child("gender").value
                    val bio = it.child("bio").value
                    val firstname = it.child("first_name").value
                    val lastname = it.child("last_name").value
                    view.findViewById<TextView>(R.id.SettingGender).text= (gender.toString())
                    biofield.setText((bio.toString()))
                    FirstNamefield.setText((firstname.toString()))
                    LastNamefield.setText((lastname.toString()))

                }else{
                }
            }
        }else {
        }

        return view
    }

    //method for upload the image
    private fun uploadPhotoToFirebase() {
        if (selectedPhotoUri == null) return

        //create the file name by using uuid to generate random text
        val filename = UUID.randomUUID().toString()
        //set reference
        val ref = FirebaseStorage.getInstance().getReference("/profile/Userimage/$filename"+ " UID: " + fAuth.currentUser?.uid)

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("Setting Fragment", "Successfully uploaded image: ${it.metadata?.path}")
            }
    }

    //onActivityResult is for show the photo in the imageview and same as the startActivityForResult, google is not suggest to use but still work
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("Setting Activity", "Photo was selected")

            selectedPhotoUri = data.data

            Userimage.setImageURI(selectedPhotoUri)
        }
    }
}