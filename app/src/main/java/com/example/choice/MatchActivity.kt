package com.example.choice

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.protobuf.Value
import com.yuyakaido.android.cardstackview.*

//Daniel matching
class MatchActivity : AppCompatActivity(), CardStackListener {
    //Init variables
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var userDB: DatabaseReference
    private lateinit var currUserDB: DatabaseReference
    private val adapter = CardStackAdapter()
    private val cardItems = mutableListOf<CardItem>()

    //Notification
    private val CHANNEL_ID = "channel_id_example_01"
    private val notificationID = 101

    private var userGender: String = ""
    private var userGenderPref: String = ""
    //Call card manager API
    private val manager by lazy {
        CardStackLayoutManager(this, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)
        //Set references
        userDB = Firebase.database.reference.child("Users")
        currUserDB = FirebaseDatabase.getInstance().getReference("Users")

        val currentUserDB = userDB.child(getCurrentUserID())
        //Listen for current user first name, if there is one force input, error prevention
        currentUserDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("first_name").value == null) {
                    showNameInputPopup()
                    return
                }
                //Call all setup methods, moved from main body
                getUserGender()
                println("User Gender = $userGender")
                getUserGenderPref()
                println("User Pref = $userGenderPref")
                getUnSelectedUsers()
                initCardStackView()
                initButtons()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun initCardStackView() {
        val stackView = findViewById<CardStackView>(R.id.cardStackView)
        //Find card stack (api) and set adapter
        stackView.layoutManager = manager
        stackView.adapter = adapter
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)

    }
    //Get users that havent been liked or disliked
    //Also do UID
    private fun getUnSelectedUsers() {
        userDB.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.child("uid").value != getCurrentUserID()
//                    snapshot.child("likedBy").child("like").hasChild(getCurrentUserID()).not() &&
//                    snapshot.child("likedBy").child("disLike").hasChild(getCurrentUserID()).not()
////                    &&
////                            snapshot.child("gender").value == userGenderPref &&
////                            snapshot.child("gender_pref").value == userGender
                ) {
                    println("Match Checking Passed")
                    val userId = snapshot.child("uid").value.toString()
                    var name = "undecided"
                    if (snapshot.child("first_name").value != null) {
                        name = snapshot.child("first_name").value.toString()
                    }
                    val bio = snapshot.child("bio").value.toString()
                    val profilePic = snapshot.child("profile_picture").value.toString()
                    //Add to card items
                    cardItems.add(CardItem(userId, name, bio, profilePic))
                    adapter.submitList(cardItems)
                    //Notify data set changed to adapter
                    adapter.notifyDataSetChanged()

                }else if (userGender == "Everyone"){
                    val userId = snapshot.child("uid").value.toString()
                    var name = "undecided"
                    if (snapshot.child("first_name").value != null) {
                        name = snapshot.child("first_name").value.toString()
                    }
                    val bio = snapshot.child("bio").value.toString()
                    val profilePic = snapshot.child("profile_picture").value.toString()
                    //Add to card items
                    cardItems.add(CardItem(userId, name, bio, profilePic))
                    adapter.submitList(cardItems)
                    //Notify data set changed to adapter
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                cardItems.find { it.userId == snapshot.key }?.let {
                    it.name = snapshot.child("first_name").value.toString()
                }
                //Submit to adapter
                adapter.submitList(cardItems)
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

        })
    }
    //Shows name input if user has no name, removes errors that may occur with registration
    private fun showNameInputPopup() {
        val editText = EditText(this)

        AlertDialog.Builder(this)
            .setTitle("No name detected: Please enter your name")
            .setView(editText)
            .setPositiveButton("Confirm") { _, _ ->
                if (editText.text.isEmpty()) {
                    showNameInputPopup()
                } else {
                    saveUserName(editText.text.toString())
                }
            }
            .setCancelable(false)
            .show()
    }
    //Saves username into firebase
    private fun saveUserName(name: String) {

        val userId: String = getCurrentUserID()
        val currentUserDb = userDB.child(userId)
        val user = mutableMapOf<String, Any>()
        user["uid"] = userId
        user["first_name"] = name
        currentUserDb.updateChildren(user)
        getUnSelectedUsers()
    }
    //Gets the current user ID, returns if not logged in
    private fun getCurrentUserID(): String {
        if (auth.currentUser == null) {
            finish()
        }
        return auth.currentUser?.uid.orEmpty()
    }

    fun getUserGender(){
        val user = auth.currentUser
        userDB.child(user!!.uid).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("gender").value != null){
                    userGender = snapshot.child("gender").value.toString()
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    fun getUserGenderPref(){
        val user = auth.currentUser
        userDB.child(user!!.uid).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("gender_pref").value != null){
                    userGender = snapshot.child("gender_pref").value.toString()
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    //Function for liking user
    private fun like() {
        val card = cardItems[manager.topPosition - 1]
        cardItems.removeFirst()
        //Add liked tree to firebase information
        userDB.child(card.userId)
            .child("likedBy")
            .child("like")
            .child(getCurrentUserID())
            .setValue(true)

        saveMatchIfOtherUserLikeMe(card.userId)
        //Display on like
        Toast.makeText(this, "${card.name} has been liked.", Toast.LENGTH_SHORT).show()
    }

    private fun disLike() {
        val card = cardItems[manager.topPosition - 1]
        cardItems.removeFirst()

        userDB.child(card.userId)
            .child("likedBy")
            .child("disLike")
            .child(getCurrentUserID())
            .setValue(true)
        //Display on dislike
        Toast.makeText(this, "${card.name} has been disliked.", Toast.LENGTH_SHORT).show()
    }

    //Save match in firebase like tree, viewable on firebase data
    private fun saveMatchIfOtherUserLikeMe(otherUserId: String) {
        val otherUserDB =
            userDB.child(getCurrentUserID()).child("likedBy").child("like").child(otherUserId)
        otherUserDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == true) {
                    userDB.child(getCurrentUserID())
                        .child("likedBy")
                        .child("match")
                        .child(otherUserId)
                        .setValue(true)

                    userDB.child(otherUserId)
                        .child("likedBy")
                        .child("match")
                        .child(getCurrentUserID())
                        .setValue(true)

                    //Send the notification
                    createNotificationChannel()
                    sendNotification()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {}

    //Override for card adapter, set right and left actions
    override fun onCardSwiped(direction: Direction?) {
        println("Function activated")
        when (direction) {
            Direction.Right -> like()
            Direction.Left -> disLike()
            else -> {}
        }
    }
    //Rewind not implemented
    override fun onCardRewound() {}

    override fun onCardCanceled() {}
    //Set view on card appear

    override fun onCardAppeared(view: View?, position: Int) {}
    //Set view on card disappear

    override fun onCardDisappeared(view: View?, position: Int) {}

    private fun initButtons(){
        val returnMainButton = findViewById<Button>(R.id.returnToMain)
        returnMainButton.setOnClickListener {
            startActivity(Intent(this, BottomNav::class.java))
        }
        //TODO: Not working, remove at end if still broken
//        val likeButton = findViewById<View>(R.id.like_button)
//        likeButton.setOnClickListener{
//            like()
//        }
//        val skipButton = findViewById<View>(R.id.skip_button)
//        skipButton.setOnClickListener{
//            onCardSwiped(Direction.Left)
//        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Notification Title"
            val descriptionText = "Notification Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID,name,importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(){

        val intent = Intent(this, BottomNav::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this,0,intent,0)
        val bitmap = BitmapFactory.decodeResource(applicationContext.resources,R.drawable.profile_image)
        val bitmapLargeIcon = BitmapFactory.decodeResource(applicationContext.resources,R.drawable.choicepfp)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.placeholderlogo)
            .setContentTitle("You got Match!!!")
            .setContentText("Example Descirption")
            .setLargeIcon(bitmapLargeIcon)
            .setStyle(NotificationCompat.BigTextStyle().bigText("Match!!!!!!!!!WOW!!!!!!!"))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)){
            notify(notificationID,builder.build())
        }
    }
}