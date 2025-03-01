package com.example.musicchorddic

/**
 * Utility class to identify music chords from a set of notes
 */
class ChordIdentifier {
    
    companion object {
        // Standard notes in letter notation
        private val NOTES = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
        
        // Alternative notation for flats
        private val FLAT_EQUIVALENTS = mapOf(
            "Db" to "C#",
            "Eb" to "D#",
            "Gb" to "F#",
            "Ab" to "G#",
            "Bb" to "A#"
        )
        
        // Define chord types by their intervals in semitones from the root note
        private val CHORD_TYPES = mapOf(
            "Major" to setOf(0, 4, 7),
            "Minor" to setOf(0, 3, 7),
            "Diminished" to setOf(0, 3, 6),
            "Augmented" to setOf(0, 4, 8),
            "Sus2" to setOf(0, 2, 7),
            "Sus4" to setOf(0, 5, 7),
            "Major 7th" to setOf(0, 4, 7, 11),
            "Minor 7th" to setOf(0, 3, 7, 10),
            "Dominant 7th" to setOf(0, 4, 7, 10),
            "Diminished 7th" to setOf(0, 3, 6, 9),
            "Half-Diminished 7th" to setOf(0, 3, 6, 10),
            "Augmented 7th" to setOf(0, 4, 8, 10),
            "Major 6th" to setOf(0, 4, 7, 9),
            "Minor 6th" to setOf(0, 3, 7, 9),
            "9th" to setOf(0, 4, 7, 10, 14),
            "Minor 9th" to setOf(0, 3, 7, 10, 14),
            "Major 9th" to setOf(0, 4, 7, 11, 14),
            "6/9" to setOf(0, 4, 7, 9, 14),
            "5 (Power Chord)" to setOf(0, 7)
        )
        
        /**
         * Identifies a chord from a set of notes in letter notation (e.g., "C", "Eb", "G")
         * @param notes List of notes in letter notation
         * @param key Optional key for context, may help resolve ambiguities
         * @param identifyInversions Whether to identify chord inversions
         * @return The identified chord name or "Unknown chord" if not recognized
         */
        fun identifyChord(notes: List<String>, key: String? = null, identifyInversions: Boolean = true): String {
            if (notes.isEmpty()) return "No notes provided"
            if (notes.size == 1) return "${notes[0]} note"
            
            // Normalize notes (convert flats to sharps for internal processing)
            val normalizedNotes = notes.map { normalizeNote(it) }
            
            // Convert notes to numeric indices (0-11)
            val noteIndices = normalizedNotes.mapNotNull { NOTES.indexOf(it) }.filter { it >= 0 }
            
            if (noteIndices.size < notes.size) {
                return "Invalid note(s) found"
            }
            
            // Assume the first note is the bass note (lowest note)
            val bassNote = normalizedNotes.first()
            val bassNoteIndex = NOTES.indexOf(bassNote)
            
            // Try each note as the potential root
            for (potentialRoot in noteIndices.distinct()) {
                // Calculate intervals from this root
                val intervals = calculateIntervals(noteIndices, potentialRoot)
                
                // Check if these intervals match any known chord type
                for ((chordType, chordIntervals) in CHORD_TYPES) {
                    if (intervals == chordIntervals) {
                        val rootNoteName = NOTES[potentialRoot]
                        
                        // If we want to identify inversions and the bass note is not the root
                        if (identifyInversions && potentialRoot != bassNoteIndex) {
                            // This is an inverted chord - use slash notation
                            return "$rootNoteName $chordType/$bassNote"
                        }
                        
                        return "$rootNoteName $chordType"
                    }
                }
            }
            
            // If we haven't returned by now, try to identify incomplete chords
            return identifyIncompleteChord(noteIndices, normalizedNotes.first(), identifyInversions) ?: "Unknown chord"
        }
        
        /**
         * Identifies a chord from a set of notes, with specific information about inversions
         * @param notes List of notes in letter notation
         * @param key Optional key for context
         * @return The identified chord name with inversion information
         */
        fun identifyChordWithInversion(notes: List<String>, key: String? = null): String {
            if (notes.isEmpty()) return "No notes provided"
            if (notes.size == 1) return "${notes[0]} note"
            
            // Normalize notes (convert flats to sharps for internal processing)
            val normalizedNotes = notes.map { normalizeNote(it) }
            
            // Convert notes to numeric indices (0-11)
            val noteIndices = normalizedNotes.mapNotNull { NOTES.indexOf(it) }.filter { it >= 0 }
            
            if (noteIndices.size < notes.size) {
                return "Invalid note(s) found"
            }
            
            // Assume the first note is the bass note (lowest note)
            val bassNote = normalizedNotes.first()
            val bassNoteIndex = NOTES.indexOf(bassNote)
            
            // Try each note as the potential root
            for (potentialRoot in noteIndices.distinct()) {
                // Calculate intervals from this root
                val intervals = calculateIntervals(noteIndices, potentialRoot)
                
                // Check if these intervals match any known chord type
                for ((chordType, chordIntervals) in CHORD_TYPES) {
                    if (intervals == chordIntervals) {
                        val rootNoteName = NOTES[potentialRoot]
                        
                        // Identify the inversion based on which chord tone is in the bass
                        val inversionInfo = when {
                            potentialRoot == bassNoteIndex -> "Root position"
                            chordType.contains("Major") && (bassNoteIndex - potentialRoot + 12) % 12 == 4 -> 
                                "First inversion (bass: 3rd) - $rootNoteName $chordType/$bassNote"
                            chordType.contains("Minor") && (bassNoteIndex - potentialRoot + 12) % 12 == 3 -> 
                                "First inversion (bass: 3rd) - $rootNoteName $chordType/$bassNote"
                            chordType.contains("Major") && (bassNoteIndex - potentialRoot + 12) % 12 == 7 -> 
                                "Second inversion (bass: 5th) - $rootNoteName $chordType/$bassNote"
                            chordType.contains("Minor") && (bassNoteIndex - potentialRoot + 12) % 12 == 7 -> 
                                "Second inversion (bass: 5th) - $rootNoteName $chordType/$bassNote"
                            chordType.contains("7th") && (bassNoteIndex - potentialRoot + 12) % 12 == 10 -> 
                                "Third inversion (bass: 7th) - $rootNoteName $chordType/$bassNote"
                            chordType.contains("Major 7th") && (bassNoteIndex - potentialRoot + 12) % 12 == 11 -> 
                                "Third inversion (bass: 7th) - $rootNoteName $chordType/$bassNote"
                            else -> "Inversion with bass note: $bassNote - $rootNoteName $chordType/$bassNote"
                        }
                        
                        return inversionInfo
                    }
                }
            }
            
            // If we haven't returned by now, try incomplete chords
            val incompleteResult = identifyIncompleteChord(noteIndices, bassNote, true)
            return incompleteResult ?: "Unknown chord"
        }
        
        /**
         * Identifies a chord from a set of numeric notes (scale degrees) relative to a key
         * @param numericNotes List of notes in numeric notation (1-7)
         * @param key The key to interpret the numeric notes in
         * @param identifyInversions Whether to identify chord inversions
         * @return The identified chord name or "Unknown chord" if not recognized
         */
        fun identifyChordFromNumeric(numericNotes: List<Int>, key: String, identifyInversions: Boolean = true): String {
            if (numericNotes.isEmpty()) return "No notes provided"
            if (numericNotes.size == 1) return "Single note"
            
            val keyIndex = NOTES.indexOf(normalizeNote(key))
            if (keyIndex < 0) return "Invalid key"
            
            // Convert numeric notation to letter notation based on the key
            val majorScale = calculateMajorScale(keyIndex)
            
            // Map scale degrees to actual notes
            val letterNotes = numericNotes.map { degree -> 
                if (degree < 1 || degree > 7) return "Invalid scale degree: $degree"
                NOTES[majorScale[degree - 1]]
            }
            
            return identifyChord(letterNotes, key, identifyInversions)
        }
        
        /**
         * Normalizes a note name, converting flats to their sharp equivalents
         */
        private fun normalizeNote(note: String): String {
            return FLAT_EQUIVALENTS[note] ?: note
        }
        
        /**
         * Calculates intervals in semitones from a root note
         */
        private fun calculateIntervals(noteIndices: List<Int>, rootIndex: Int): Set<Int> {
            return noteIndices.map { (it - rootIndex + 12) % 12 }.toSet()
        }
        
        /**
         * Attempts to identify incomplete chords (missing some notes)
         */
        private fun identifyIncompleteChord(noteIndices: List<Int>, bassNote: String, identifyInversions: Boolean): String? {
            if (noteIndices.size < 2) return null
            
            val bassNoteIndex = NOTES.indexOf(bassNote)
            
            // For triads with missing fifths (just root and third)
            if (noteIndices.size == 2) {
                for (potentialRoot in noteIndices) {
                    val intervals = calculateIntervals(noteIndices, potentialRoot)
                    
                    if (intervals.contains(4)) {
                        val rootNoteName = NOTES[potentialRoot]
                        
                        if (identifyInversions && potentialRoot != bassNoteIndex) {
                            return "$rootNoteName Major (no 5th)/$bassNote"
                        }
                        
                        return "${NOTES[potentialRoot]} Major (no 5th)"
                    }
                    
                    if (intervals.contains(3)) {
                        val rootNoteName = NOTES[potentialRoot]
                        
                        if (identifyInversions && potentialRoot != bassNoteIndex) {
                            return "$rootNoteName Minor (no 5th)/$bassNote"
                        }
                        
                        return "${NOTES[potentialRoot]} Minor (no 5th)"
                    }
                    
                    if (intervals.contains(7)) {
                        val rootNoteName = NOTES[potentialRoot]
                        
                        if (identifyInversions && potentialRoot != bassNoteIndex) {
                            return "$rootNoteName 5 (Power Chord)/$bassNote"
                        }
                        
                        return "${NOTES[potentialRoot]} 5 (Power Chord)"
                    }
                }
            }
            
            // For four-note chords with one missing note
            if (noteIndices.size == 3) {
                for (potentialRoot in noteIndices) {
                    val intervals = calculateIntervals(noteIndices, potentialRoot)
                    
                    // Check for triads
                    if (intervals.containsAll(setOf(0, 4, 7).minus(setOf(7)))) {
                        val rootNoteName = NOTES[potentialRoot]
                        
                        if (identifyInversions && potentialRoot != bassNoteIndex) {
                            return "$rootNoteName Major/$bassNote"
                        }
                        
                        return "${NOTES[potentialRoot]} Major"
                    }
                    
                    if (intervals.containsAll(setOf(0, 3, 7).minus(setOf(7)))) {
                        val rootNoteName = NOTES[potentialRoot]
                        
                        if (identifyInversions && potentialRoot != bassNoteIndex) {
                            return "$rootNoteName Minor/$bassNote"
                        }
                        
                        return "${NOTES[potentialRoot]} Minor"
                    }
                    
                    // Other common incomplete chords could be added here
                }
            }
            
            return null
        }
        
        /**
         * Calculates the major scale starting from a given root note index
         * @return Array of indices for the 7 notes of the major scale
         */
        private fun calculateMajorScale(rootIndex: Int): Array<Int> {
            // Major scale intervals in semitones: W-W-H-W-W-W-H (2-2-1-2-2-2-1)
            val scaleIntervals = arrayOf(0, 2, 4, 5, 7, 9, 11)
            return scaleIntervals.map { (rootIndex + it) % 12 }.toTypedArray()
        }
        
        /**
         * Parse notes string into a list of notes
         * @param notesStr Space or comma-separated notes string
         * @return List of individual notes
         */
        fun parseNotes(notesStr: String): List<String> {
            // Split by space or comma and trim
            return notesStr.split(Regex("[\\s,]+"))
                .map { it.trim() }
                .filter { it.isNotEmpty() }
        }
        
        /**
         * Parse numeric notes string into a list of integers
         * @param numericNotesStr Space or comma-separated numeric notes
         * @return List of scale degrees as integers
         */
        fun parseNumericNotes(numericNotesStr: String): List<Int> {
            return numericNotesStr.split(Regex("[\\s,]+"))
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .mapNotNull { 
                    // Handle sharps and flats in numeric notation
                    when {
                        it.endsWith("#") -> it.substring(0, it.length - 1).toIntOrNull()?.let { num -> num }
                        it.endsWith("b") -> it.substring(0, it.length - 1).toIntOrNull()?.let { num -> num }
                        else -> it.toIntOrNull()
                    }
                }
        }
        
        /**
         * Returns a list of all available keys
         */
        fun getAllKeys(): List<String> {
            return NOTES.toList()
        }
    }
} 