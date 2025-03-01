package com.example.musicchorddic

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.musicchorddic.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private var useLetterNotation = true
    private var showDetailedInversions = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupKeyDropdown()
        setupNotationRadioGroup()
        setupInversionSwitch()
        setupButtonListeners()
        setupKeyboardDismissal()
    }
    
    private fun setupKeyDropdown() {
        val keys = ChordIdentifier.getAllKeys()
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, keys)
        (binding.keyAutoCompleteTextView as? AutoCompleteTextView)?.setAdapter(adapter)
        binding.keyAutoCompleteTextView.setText("C", false) // Default to C major
    }
    
    private fun setupNotationRadioGroup() {
        binding.notationRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            useLetterNotation = (checkedId == binding.letterRadioButton.id)
            
            // Update helper text based on notation type
            if (useLetterNotation) {
                binding.notesInputLayout.helperText = "Example: C E G or C,E,G"
            } else {
                binding.notesInputLayout.helperText = "Example: 1 3 5 or 1,3,5"
            }
        }
    }
    
    private fun setupInversionSwitch() {
        binding.inversionSwitch.setOnCheckedChangeListener { _, isChecked ->
            showDetailedInversions = isChecked
        }
    }
    
    private fun setupButtonListeners() {
        binding.identifyButton.setOnClickListener {
            identifyChord()
        }
        
        binding.clearButton.setOnClickListener {
            binding.notesEditText.text?.clear()
            binding.chordResultTextView.text = ""
        }
    }
    
    private fun setupKeyboardDismissal() {
        // Set up the notes input to dismiss keyboard on Enter key
        binding.notesEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || 
                actionId == EditorInfo.IME_ACTION_NEXT || 
                actionId == EditorInfo.IME_ACTION_GO) {
                // Hide keyboard
                hideKeyboard()
                // Identify chord
                identifyChord()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }
    
    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusView = currentFocus
        if (currentFocusView != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
        }
    }
    
    private fun identifyChord() {
        val notesInput = binding.notesEditText.text.toString().trim()
        val selectedKey = binding.keyAutoCompleteTextView.text.toString()
        
        if (notesInput.isEmpty()) {
            Toast.makeText(this, "Please enter notes", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (selectedKey.isEmpty()) {
            Toast.makeText(this, "Please select a key", Toast.LENGTH_SHORT).show()
            return
        }
        
        val chordName = if (useLetterNotation) {
            // Process letter notation
            val notes = ChordIdentifier.parseNotes(notesInput)
            if (showDetailedInversions) {
                ChordIdentifier.identifyChordWithInversion(notes, selectedKey)
            } else {
                ChordIdentifier.identifyChord(notes, selectedKey)
            }
        } else {
            // Process numeric notation
            val numericNotes = ChordIdentifier.parseNumericNotes(notesInput)
            ChordIdentifier.identifyChordFromNumeric(numericNotes, selectedKey, !showDetailedInversions)
        }
        
        // Display the result
        binding.chordResultTextView.text = getString(R.string.chord_result, chordName)
        
        // Hide keyboard after identification
        hideKeyboard()
    }
} 