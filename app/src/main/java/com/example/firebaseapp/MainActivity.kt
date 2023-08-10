package com.example.firebaseapp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions

class MainActivity : AppCompatActivity() {
    private lateinit var edTitle:AppCompatEditText
    private lateinit var edDescription:AppCompatEditText
    private lateinit var btnSave:AppCompatButton
    private lateinit var btnLoad:AppCompatButton
    private lateinit var tvOutput:TextView
    private lateinit var btnUpdate:AppCompatButton
    private lateinit var btnDeleteDescrition:AppCompatButton
    private lateinit var btnDeleteNote:AppCompatButton
    private val KEY_TITLE:String = "Title"
    private val KEY_DESCRIPTION:String = "Description"
    private var db:FirebaseFirestore = FirebaseFirestore.getInstance()
    private val documentRef:DocumentReference = db.collection("Notebook").document("First Note")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        edTitle = findViewById(R.id.edTitle)
        edDescription = findViewById(R.id.edDescription)
        btnSave = findViewById(R.id.buttonSave)
        btnLoad = findViewById(R.id.buttonLoad)
        btnUpdate = findViewById(R.id.buttonUpdate)
        btnDeleteDescrition = findViewById(R.id.buttonDeleteDescription)
        btnDeleteNote = findViewById(R.id.buttonDeleteNote)
        tvOutput = findViewById(R.id.txtOutput)

        btnSave.setOnClickListener {
            saveData()
        }
        btnLoad.setOnClickListener {
            loadData()
        }
        btnUpdate.setOnClickListener {
            updateTitle()
        }
        btnDeleteDescrition.setOnClickListener {
            deleteDescription()
        }
        btnDeleteNote.setOnClickListener {
            deleteNote()
        }
    }

    //Automatically run when changes occur
    override fun onStart() {
        super.onStart()
         documentRef.addSnapshotListener(this){document,error->
            error?.let {
                return@addSnapshotListener
            }
            document?.let {
                if(it.exists()) {
                    val title = it.getString(KEY_TITLE)
                    val description = it.getString(KEY_DESCRIPTION)
//                    val note = mutableMapOf<String, Any>()
//                    note.put(KEY_TITLE, title!!)
//                    note.put(KEY_DESCRIPTION, description!!)
                    tvOutput.text = "Title: $title\n Description: $description"
                    tvOutput.setBackgroundColor(Color.parseColor("#EDECE6"))
                }else{
                    tvOutput.text = ""
                    Toast.makeText(this,"The document doesn't exist",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun saveData(){
        if (edTitle.text.toString().isEmpty() || edDescription.text.toString().isEmpty())
        {
            Toast.makeText(this,"Fields cannot be empty!",Toast.LENGTH_SHORT).show()
        }
        else{
            var title = edTitle.text.toString()
            var description = edDescription.text.toString()
            var note = mutableMapOf<String,Any>()
            note.put(KEY_TITLE,title)
            note.put(KEY_DESCRIPTION,description)
            documentRef.set(note)
                .addOnSuccessListener {
                    Toast.makeText(this,"Your note has been saved successfully",Toast.LENGTH_SHORT).show()
                }.addOnFailureListener{
                    Toast.makeText(this,"Error occurred",Toast.LENGTH_SHORT).show()
                }
        }


    }

    private fun loadData(){
        documentRef.get()
            .addOnSuccessListener {
                if(it.exists()) {
                    val title = it.getString(KEY_TITLE)
                    val description = it.getString(KEY_DESCRIPTION)
//                    val note = mutableMapOf<String, Any>()
//                    note.put(KEY_TITLE, title!!)
//                    note.put(KEY_DESCRIPTION, description!!)
                    tvOutput.text = "Title: $title\n Description: $description"
                    tvOutput.setBackgroundColor(Color.parseColor("#EDECE6"))
                }else{
                    Toast.makeText(this,"The document doesn't exist",Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener{
                Toast.makeText(this,"Failed to load data",Toast.LENGTH_SHORT).show()
            }
    }
    //Update the title
    private fun updateTitle(){
        var title = edTitle.text.toString()
        var note = mutableMapOf<String,Any>()
        note[KEY_TITLE] = title
        //set(): It update the document if its not exist it will create new one
        documentRef.set(note, SetOptions.merge())
        //update(): Only update the document if exists and it will not create new document
        //documentRef.update(KEY_TITLE,title)
    }
    //Delete the description
    private fun deleteDescription(){
        val note = mutableMapOf<String,Any>()
        note[KEY_DESCRIPTION] = FieldValue.delete()
        documentRef.update(note)
    }
    //Delete the entire note or document
    private fun deleteNote(){
        documentRef.delete()
    }
}