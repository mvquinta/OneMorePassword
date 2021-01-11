package com.example.onemorepassword

import android.util.Log
import androidx.lifecycle.ViewModel

class OneMorePasswordViewModel: ViewModel() {

    var finalPass: String = "1 + Password"
    var realTimeStrengthLevel: Int = 10
    var otherString: CharSequence = "myStrong"

    fun passStrengthLevelVM(realTimeLengthVM: Int, passStrengthSwitchLevelVM: Int): Int {
        var realTimeStrengthLevel: Int = 1
        if (passStrengthSwitchLevelVM == 2) {
            realTimeStrengthLevel = 16 + realTimeLengthVM * 2
        } else if (passStrengthSwitchLevelVM == 3) {
            realTimeStrengthLevel = 32 + realTimeLengthVM * 2
        } else if (passStrengthSwitchLevelVM == 4) {
            realTimeStrengthLevel = 50 + realTimeLengthVM * 2
        } else {
            realTimeStrengthLevel = passStrengthSwitchLevelVM
        }
        return realTimeStrengthLevel
    }


    //My Main Function where password is generated. Wrote this code in IntelliJ and tweaked a little to better suit this app
    fun randomPass(sizeOfPass: Int, lettersLow: Boolean, lettersUp: Boolean, numbers: Boolean, symbols: Boolean): String {

        //Declare list of characters to create password
        val listAbc = listOf<Char>('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z')
        val listNumbers = listOf<Char>('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
        val listSymbols = listOf<Char>('!', '?', '(', ')', '[', ']', '{', '}', '"', '<', '>', '"', ':', ';', '.', ',',
            '-', '_', '|', '^', '~', '@', '&', '$')

        //Declare needed variables and initialize them
        val tempListRandChar = mutableListOf<Char>()
        val tempListPassChar = mutableListOf<Char>()

        //Loop that creates random chars list to generate a password.
        var count: Int = 0
        while (count < sizeOfPass ) {
            //First I create a small list with an element from each different list of chars
            if (lettersLow == false) {
                print("")
            } else {
                val a = listAbc.shuffled()
                tempListRandChar.add(a.random())
            }

            if (lettersUp == false) {
                print("")
            } else {
                val b = listAbc.shuffled()
                tempListRandChar.add(b.random().toUpperCase())
            }

            if (numbers == false) {
                print("")
            } else {
                val c = listNumbers.shuffled()
                tempListRandChar.add(c.random())
            }

            if (symbols == false) {
                print("")
            } else {
                val d = listSymbols.shuffled()
                tempListRandChar.add(d.random())
            }
            //Than from that small list I randomized it again an add that element to final char list
            //val randomChar: Int = (0 until tempListRandChar.size).random()
            tempListPassChar.add(tempListRandChar[(0 until tempListRandChar.size).random()])
            tempListRandChar.clear()
            count += 1
        }

        //Transform to a CharArray to be able to print a string out of the mutableLisOf<Char>
        finalPass = String(tempListPassChar.toCharArray())
        return finalPass
    }

    /////////////////////////////////////////////////
    /////////////////////////////////////////////////
    fun savePassWarning(myString: CharSequence): CharSequence {
        otherString = myString
        Log.i("TAG", "fun savePass no view model executada -> ${otherString}")
        return otherString
    }
    /////////////////////////////////////////////////
    /////////////////////////////////////////////////
}