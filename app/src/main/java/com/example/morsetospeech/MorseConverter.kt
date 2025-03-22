package com.example.morsetospeech

object MorseConverter {
    private val morseToTextMap = mapOf(
        ".-" to "A", "-..." to "B", "-.-." to "C", "-.." to "D", "." to "E",
        "..-." to "F", "--." to "G", "...." to "H", ".." to "I", ".---" to "J",
        "-.-" to "K", ".-.." to "L", "--" to "M", "-." to "N", "---" to "O",
        ".--." to "P", "--.-" to "Q", ".-." to "R", "..." to "S", "-" to "T",
        "..-" to "U", "...-" to "V", ".--" to "W", "-..-" to "X", "-.--" to "Y",
        "--.." to "Z",
        ".----" to "1", "..---" to "2", "...--" to "3", "....-" to "4", "....." to "5",
        "-...." to "6", "--..." to "7", "---.." to "8", "----." to "9", "-----" to "0",
        ".-.-.-" to ".", "--..--" to ",", "..--.." to "?", "-.-.--" to "!",
        "...---..." to "SOS"
    )

    fun convertMorseToText(morseCode: String): Result<String> {
        return try {
            val words = morseCode.trim().split("/")
            val convertedText = words.map { word ->
                word.trim().split(" ").map { symbol ->
                    morseToTextMap[symbol] ?: throw IllegalArgumentException("Invalid Morse code: $symbol")
                }.joinToString("")
            }.joinToString(" ")
            
            Result.success(convertedText)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isValidMorseCode(input: String): Boolean {
        if (input.isEmpty()) return false
        val validChars = setOf('.', '-', ' ', '/')
        return input.all { it in validChars }
    }

    fun getValidMorseCharacters(): String {
        return "Use dots (.) and dashes (-). Separate letters with space, words with '/'"
    }
}