# Music Chord Dictionary

An Android application that identifies music chords based on input notes. Users can input notes using either letter notation (C, D, E, etc.) or numeric notation (1, 2, 3, etc.), and the app will identify what chord these notes form.

## Features

- Identify chords from a set of input notes
- Support for both letter notation (C, D, E) and numeric notation (1, 2, 3)
- Support for sharps (#) and flats (b) in note input
- Select the musical key for context
- Recognizes various chord types including:
  - Major and minor triads
  - 7th chords (major, minor, dominant)
  - Sus chords
  - Augmented and diminished chords
  - Power chords (5th)
  - Extended chords (9th, 6/9)
- Clean, modern Material Design UI

## How to Use

1. Select your preferred notation type (letter or numeric)
2. Enter the notes you want to identify, separated by spaces or commas
   - For letter notation: `C E G` or `C,E,G`
   - For numeric notation: `1 3 5` or `1,3,5`
3. Select the key for context (especially important for numeric notation)
4. Press "Identify Chord" to see the result
5. Use "Clear" to start over

## Examples

- `C E G` → C Major
- `A C# E` → A Major
- `D F A` → D Minor
- `G B D F` → G Major 7th
- `E G# B D` → E Dominant 7th
- In the key of C, `1 3 5` → C Major
- In the key of A, `1 3 5` → A Major

## Building the Project

This project uses Gradle as the build system. To build and run:

1. Clone the repository
2. Open in Android Studio
3. Sync the Gradle files
4. Run on an emulator or physical device

## Requirements

- Android 5.0 (API level 21) or higher
- Gradle 7.0 or higher
- Android Studio Arctic Fox or newer

## License

This project is open source and available under the MIT License. 