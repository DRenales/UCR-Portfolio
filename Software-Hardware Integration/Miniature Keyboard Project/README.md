# CS120B Miniature Keyboard Final Project
## SUMMARY
This project holds the C code for a small scale keyboard that supports a full octave
of keys starting at C.

## COMPONENTS
### HARDWARE
- Atmega1284 (x2)

- 7-Segment LED Display

- Push Buttons (x16)

- 1-bit Speaker/Buzzer (x2)

- 8x8 LED Matrix

- 300Ω Resistors (x10)


### SOFTWARE
- Atmel Studio v7.1

## FEATURES AND FUNCTIONS
1. Supports a full octave of keys starting from C (13 piano keys).
2. A demo of the intro portion of Thunderstruck by ACDC along with an LED light display to emulate a tutorial.
3. Count down timer to display when the song is going to start.
4. Transpose buttons that provide the keys a range from C2-C3 up to C7-C8.
5. EEPROM saves the transpose of the last octave played.

## POTENTIAL ADDITIONS / POLISHING
1. 5x5 LED matrix to properly display when sharps/flats should be played
2. Have LED lights scroll down LED matrix instead of lighting the
3. Add an LCD display to prompt user to press button to play song
4. Add a keypad to play different songs
5. Use EEPROM to save recording snippets

## ~~KNOWN BUGS~~ ADDITIONAL FEATURES
1. EEPROM does not hold the proper transpose value upon turning the project off and back on
~~2. Play button requires SPECIFIC timing in order to play the tutorial song~~ FIXED
3. Can not play chords
