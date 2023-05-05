package com.zybooks.to_do_list_app

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationRequest
import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import kotlin.system.exitProcess
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationServices
//import android.os.Parcelable
//import android.view.ContextMenu
//import android.view.MenuItem
//import android.widget.Toast.*
//import android.util.SparseBooleanArray

class MainActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var items: ArrayList<String>

    //For play music button
    private lateinit var mediaPlayer: MediaPlayer

    //For Take-photo button
    private lateinit var imageView: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //For play music button
        val soundButton = findViewById<Button>(R.id.sound_button)

        // Initialize MediaPlayer object
        mediaPlayer = MediaPlayer.create(this, R.raw.music)

        // Set click listener for the music button
        soundButton.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                // Stop the MediaPlayer if it is already playing
                mediaPlayer.stop()
            }
            // Start the MediaPlayer
            mediaPlayer.start()

            // Load the animation
            val animation = AnimationUtils.loadAnimation(this, R.anim.bounce_anim)

            // Start the animation on the button
            soundButton.startAnimation(animation)
        }

        /*The code below is to show animation-animation button*/
        val fadeButton = findViewById<Button>(R.id.fade_button)

        fadeButton.setOnClickListener {
            // Load the animation from fade_in.xml
            val fadeIn = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
            // Apply the animation to the button
            fadeButton.startAnimation(fadeIn)
        }



        // Initialize the ListView, Adapter, and ArrayList
        listView = findViewById(R.id.listView)
        items = ArrayList()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, items)
        listView.adapter = adapter

        // Initialize the addR button
        val addButton = findViewById<Button>(R.id.add)

        // Initialize the delete button
        val deleteButton = findViewById<Button>(R.id.delete)

        // Initialize the clear button
        val clearButton = findViewById<Button>(R.id.clear)


        /*The code below is to set button click listener to add items to the list- add button*/
        addButton.setOnClickListener {
            val newItem = findViewById<EditText>(R.id.editText).text.toString()
            if (newItem.isNotEmpty()) {
                items.add(newItem)
                Toast.makeText(this, "ITEM ADDED", Toast.LENGTH_LONG).show()
                adapter.notifyDataSetChanged()
                findViewById<EditText>(R.id.editText).text.clear()
            }
        }

        /*The code below is to set a click listener for the delete button*/
        deleteButton.setOnClickListener {
            // Get a reference to the ListView
            val listView = findViewById<ListView>(R.id.listView)

            // Get the selected items in the ListView
            val checkedItems = listView.checkedItemPositions

            // Create a list to hold the positions of the checked items
            val positions = mutableListOf<Int>()

            // Iterate through the checked items and add their positions to the list
            for (i in 0 until checkedItems.size()) {
                val position = checkedItems.keyAt(i)
                if (checkedItems.get(position)) {
                    positions.add(position)
                }
            }

            // Sort the positions in descending order
            positions.sortDescending()

            // Remove the checked items from the adapter
            val adapter = listView.adapter as ArrayAdapter<String>
            for (position in positions) {
                adapter.remove(adapter.getItem(position))
            }
            // Clear the checked items in the ListView
            listView.clearChoices()
            Toast.makeText(this, "CHECKED ITEM DELETED", Toast.LENGTH_LONG).show()
        }


        /*The code below is to set a click listener for the clear button*/
        clearButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Clear List")
            builder.setMessage("Are you sure you want to clear the list?")
            builder.setPositiveButton("Yes") { _, _ ->
                val adapter = listView.adapter as ArrayAdapter<String>
                adapter.clear()
            }
            builder.setNegativeButton("No", null)
            val dialog = builder.create()
            dialog.show()

        }

        /*The code below is to show the map while clicking the - map button*/
        val showMapButton = findViewById<Button>(R.id.show_map_button)
        showMapButton.setOnClickListener {
            // Open Google Maps app with a specific location
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("geo:0,0?q=New+York+City")
            }
            startActivity(intent)
        }

        /*The code below is for take-photo button*/
        // Get references to UI elements
        val cameraButton = findViewById<Button>(R.id.camera_button)
        imageView = findViewById(R.id.view)

        // Set click listener for camera button
        cameraButton.setOnClickListener {
            // Check if camera permission is granted
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISSIONS_REQUEST_CAMERA)
            } else {
                // Launch camera activity
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            }
        }


        /* The code below is for the exit button to exit from app*/
         val exitButton=findViewById<Button>(R.id.button_exit)
         exitButton.setOnClickListener {
             exitApp()
         }


}//create function end



    /*The code below is for Take-photo button*/
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            // Check if camera permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Launch camera activity
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Get the captured photo as bitmap
            val imageBitmap = data?.extras?.get("data") as Bitmap
            // Set the bitmap in ImageView
            imageView.setImageBitmap(imageBitmap)
        }
    }

    companion object {
        private const val PERMISSIONS_REQUEST_CAMERA = 1
        private const val REQUEST_IMAGE_CAPTURE = 2
    }




/*The code below will destroy media player-play music button*/
override fun onDestroy() {
 super.onDestroy()
 // Release the MediaPlayer when the activity is destroyed
 mediaPlayer.release()
}

/* The code below will terminate the app */
private fun exitApp() {
 exitProcess(0)
}

}

