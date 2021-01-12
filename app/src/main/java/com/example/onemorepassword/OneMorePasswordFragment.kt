package com.example.onemorepassword

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.onemorepassword.databinding.OneMorePasswordFragmentBinding

class OneMorePasswordFragment : Fragment() {

    private lateinit var binding: OneMorePasswordFragmentBinding
    private lateinit var viewModel: OneMorePasswordViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<OneMorePasswordFragmentBinding>(inflater, R.layout.one_more_password_fragment, container, false)
        viewModel = ViewModelProvider(this).get(OneMorePasswordViewModel::class.java)

        /*All fun that update UI from viewModel and sharedPreferences
        loadData loads textSize and switches position to update stars-password-strength
        savedGeneratedPass loads viewModel data
        I execute warningLevel again to update stars-password-strength in case switches were not used in previous session*/
        loadUIData()
        savedGeneratedPass()
        warningPassLevel()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val generateButton: Button = binding.generateButton
        val copyBtn: Button = binding.copyTextButton
        val copyTxt: TextView = binding.passText
        val lengthPassTextNumber: TextView = binding.lengthPassText

        //UI Animations
        val animScale: Animation = AnimationUtils.loadAnimation(requireActivity(), R.anim.anim_scale)
        animations()

        /*Initializing clipBoardManager and clip data
        Code from geeksforgeeks.org - clipboard-in-android*/
        var clipBoardManager =
                this.activity?.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
        var clipData: ClipData

        //Action when copy button is clicked
        copyBtn.setOnClickListener {
            // Text from the edit text is stored in a val
            val txtCopy = copyTxt.text.toString()
            // clip data is initialized with the text variable declared above
            clipData = ClipData.newPlainText("textMy", txtCopy)
            // Clipboard saves this clip object
            clipBoardManager.setPrimaryClip(clipData)
            //Button Animation
            copyBtn.startAnimation(animScale)
            Toast.makeText(requireActivity(), "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        /*Set seekBar Listener
        onProgressChanged checks progress in realtime and update level strength warning
        /onStopTrackingTouch sends a toast with password length selected. I think I'll delete this*/
        val lengthSizeBar: SeekBar = binding.lengthSizeSeekBar
        lengthSizeBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("NewApi")
            override fun onProgressChanged(lengthSizeBar: SeekBar, progress: Int, fromUser: Boolean) {
                lengthPassTextNumber.text = progress.toString()
                warningPassLevel(lengthPassTextNumber.text.toString().toInt())
            }

            override fun onStartTrackingTouch(lengthSizeBar: SeekBar) {
            }

            override fun onStopTrackingTouch(lengthSizeBar: SeekBar) {
                //Toast.makeText(requireActivity(),"Size of password: " + lengthSizeBar.progress, Toast.LENGTH_SHORT).show()
                //a()
            }
        })

        //Set switches to check and update level strength warning when toggled
        val switchLowButton = binding.lowLettersSwitch
        switchLowButton.setOnClickListener {
            warningPassLevel(lengthPassTextNumber.text.toString().toInt())
            saveUIData()
        }
        val switchUpButton = binding.upLettersSwitch
        switchUpButton.setOnClickListener {
            warningPassLevel(lengthPassTextNumber.text.toString().toInt())
            saveUIData()
        }
        val switchNumbersButton = binding.numbersSwitch
        switchNumbersButton.setOnClickListener {
            warningPassLevel(lengthPassTextNumber.text.toString().toInt())
            saveUIData()
        }
        val switchSymbolsButton = binding.symbolsSwitch
        switchSymbolsButton.setOnClickListener {
            warningPassLevel(lengthPassTextNumber.text.toString().toInt())
            saveUIData()
        }

        /*MAIN FUNCTION executed
        When Generate button is clicked I check if all switches are OFF. Is so, a toast warning shows.
        If at least one of them is ON, generatePass() is executed*/
        generateButton.setOnClickListener {
            //if (switchLowLetters() == false && switchUpLetters() == false && switchNumbers() == false && switchSymbols() == false) {
            if (passStrengthSwitchLevel() == 0) {
                Toast.makeText(requireActivity(), "You must select at least one option please.", Toast.LENGTH_SHORT).show()
            } else {
                generatePass()
                saveUIData()
            }
            //Button Animation
            generateButton.startAnimation(animScale)
        }
    }

    /*saveUIData and loadUIData use sharedPreferences to save and load switches and textSize of password state*/
    private fun saveUIData() {
        val sharedPreferences: SharedPreferences?= activity?.getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.apply{
            putBoolean("KEY_LOWSWITCH", binding.lowLettersSwitch.isChecked)
            putBoolean("KEY_UPSWITCH", binding.upLettersSwitch.isChecked)
            putBoolean("KEY_NUMBERSWITCH", binding.numbersSwitch.isChecked)
            putBoolean("KEY_SYMBOLWITCH", binding.symbolsSwitch.isChecked)
            putFloat("KEY_PASSTEXTSYZE", binding.passText.textSize.toFloat())
        }?.apply()
    }

    /*In loadUIData I had to find way of convert text size float value to sp. Still not sure why, when I get
    * the value from textSize and save it would take the sp value and convert it to a float. For this, the size
    * I get was being multiplied by 3. I force it to divide and get the proper conversion back.
    * In variable declaration. I have 84f as default value because thats the default size multplied by 3 (28) */
    private fun loadUIData() {
        val sharedPreferences: SharedPreferences?= activity?.getPreferences(Context.MODE_PRIVATE)
        val savedLowSwitchBoolean = sharedPreferences!!.getBoolean("KEY_LOWSWITCH", true)
        val savedUpSwitchBoolean = sharedPreferences.getBoolean("KEY_UPSWITCH", true)
        val savedNumberSwitchBoolean = sharedPreferences.getBoolean("KEY_NUMBERSWITCH", true)
        val savedSymbolSwitchBoolean = sharedPreferences.getBoolean("KEY_SYMBOLWITCH", true)
        var savedPassTextSize = sharedPreferences.getFloat("KEY_PASSTEXTSYZE", 84f)

        binding.lowLettersSwitch.isChecked = savedLowSwitchBoolean
        binding.upLettersSwitch.isChecked = savedUpSwitchBoolean
        binding.numbersSwitch.isChecked = savedNumberSwitchBoolean
        binding.symbolsSwitch.isChecked = savedSymbolSwitchBoolean
        savedPassTextSize /= 3
        binding.passText.textSize = savedPassTextSize
    }

    /*This function is executed when Generate button is clicked. It looks for the TextView and edit/inserts generated password
    * It also updates the textSize and saves the generated password from viewModel*/
    private fun generatePass() {
        viewModel.randomPass(mySizePass(), switchLowLetters(), switchUpLetters(), switchNumbers(), switchSymbols())
        updatePassTextSize()
        savedGeneratedPass()
    }

    //This fun() checks how many switch are ON and saves it to an Int variable.
    //If all switches are off, numSwitchesOn = 0, I wont be able to create a password
    //If numSwitchesOn = 1, 2, 3 or 4 it means, respectively, a weak, good, strong and very strong password
    private fun passStrengthSwitchLevel(): Int {
        val myLowLettersLevelSwitch = binding.lowLettersSwitch
        val myUpLettersLevelSwitch = binding.upLettersSwitch
        val myNumbersLevelSwitch = binding.numbersSwitch
        val mySymbolsLevelSwitch = binding.symbolsSwitch
        var numSwitchesOn: Int = 0
        val mySwitchList = listOf(myLowLettersLevelSwitch, myUpLettersLevelSwitch, myNumbersLevelSwitch, mySymbolsLevelSwitch)

        for (i in mySwitchList) {
            if (i.isChecked) {
                numSwitchesOn += 1
            }
        }
        return numSwitchesOn
    }

    /*In this fun() I calculate a number that will serve me as strength level analyser.
    Having in account the length/size of the password and the number of switches on, I created a simple calculation that tells me if it's a weak, good, strong or very strong password.
    This fun() is called in seekBar onProgressChanged. That way I can pass in real time to the function the length selected by user as an argument */
    private fun passStrengthLevel(realTimeLength: Int): Int {
        return viewModel.passStrengthLevelVM(realTimeLength, passStrengthSwitchLevel())
    }

    //Functions that return if switch are ON or OFF.
    // It would be cool to check them all in one only function.
    private fun switchLowLetters(): Boolean {
        val myLowLettersSwitch= binding.lowLettersSwitch
        return myLowLettersSwitch.isChecked
    }

    private fun switchUpLetters(): Boolean {
        val myUpLettersSwitch = binding.upLettersSwitch
        return myUpLettersSwitch.isChecked
    }

    private fun switchNumbers(): Boolean {
        val myNumbersSwitch = binding.numbersSwitch
        return myNumbersSwitch.isChecked
    }

    private fun switchSymbols(): Boolean {
        val mySymbolsSwitch = binding.symbolsSwitch
        return mySymbolsSwitch.isChecked
    }

    //Function that determines the Size/Length of the password
    private fun mySizePass(): Int {
        val value: TextView = binding.lengthPassText
        return value.text.toString().toInt()
    }

    //Methods to update UI and respect viewModel set up
    private fun savedGeneratedPass() {
        binding.passText.text = viewModel.finalPass
    }

    //This function changes the textSize depending on number of charactets lenght from seekBar
    fun updatePassTextSize() {
        val generatedPassTextSize = binding.lengthPassText.text.toString().toInt()
        val passTextSyze = binding.passText
        when (generatedPassTextSize) {
            in 8..17 -> passTextSyze.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f)
            in 18..35-> passTextSyze.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            in 36..50 -> passTextSyze.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            else -> passTextSyze.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f)
        }
    }

    //Functions with all animations that are created programmatically
    fun animations() {
        val generateButtonAnim = binding.generateButton
        val copyBtnAnim = binding.copyTextButton
        val copyTxtAnim = binding.passText
        val lengthPassTxtAnim = binding.lengthPassText
        val charactersTxtAnim = binding.charactersTextView
        val starLevelOneAnim = binding.levelStar01ImageView
        val starLevelTwoAnim = binding.levelStar02ImageView
        val starLevelThreeAnim = binding.levelStar03ImageView
        val starLevelFourAnim = binding.levelStar04ImageView
        val starLevelFiveAnim = binding.levelStar05ImageView
        val lengthSizeSeekBarAnim = binding.lengthSizeSeekBar
        val lowLettersSwitchAnim = binding.lowLettersSwitch
        val upLettersSwitchAnim = binding.upLettersSwitch
        val numbersSwitchAnim = binding.numbersSwitch
        val symbolsSwitchAnim = binding.symbolsSwitch
        val ompHeaderImageImageAnim = binding.ompHeaderImageImageView
        val myList = listOf(generateButtonAnim, copyBtnAnim, copyTxtAnim, lengthPassTxtAnim, charactersTxtAnim,
                lengthSizeSeekBarAnim, lowLettersSwitchAnim, upLettersSwitchAnim, numbersSwitchAnim, symbolsSwitchAnim, ompHeaderImageImageAnim,
                starLevelOneAnim, starLevelTwoAnim, starLevelThreeAnim, starLevelFourAnim, starLevelFiveAnim)
        for (i in myList) {
            i?.alpha = 0f
            i?.translationY = 50f
            i?.animate()?.alpha(1f)?.translationYBy(-50f)?.setStartDelay(200)?.duration = 1500
        }
    }

    //Warning function that evaluates password strength and updates star-levels
    fun warningPassLevel(seekBarNumber: Int = 10) {
        //val myLengthSizeBar: SeekBar = binding.lengthSizeSeekBar
        val starLevelOne = binding.levelStar01ImageView
        val starLevelTwo = binding.levelStar02ImageView
        val starLevelThree = binding.levelStar03ImageView
        val starLevelFour = binding.levelStar04ImageView
        val starLevelFive = binding.levelStar05ImageView
        val myProgressiveNum = passStrengthLevel(seekBarNumber)
        when (myProgressiveNum) {
            in 2..24 -> {
                starLevelOne?.alpha = 1f
                starLevelTwo?.alpha = 1f
                starLevelThree?.alpha = 0.5f
                starLevelFour?.alpha = 0.5f
                starLevelFive?.alpha = 0.5f
                starLevelOne?.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.passStrength_color_weak))
                starLevelTwo?.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.passStrength_color_weak))
                starLevelThree?.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.passStrength_color_neutral))
                starLevelFour?.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.passStrength_color_neutral))
                starLevelFive?.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.passStrength_color_neutral))
            }
            in 25..49 -> {
                starLevelOne?.alpha = 1f
                starLevelTwo?.alpha = 1f
                starLevelThree?.alpha = 1f
                starLevelFour?.alpha = 0.5f
                starLevelFive?.alpha = 0.5f
                starLevelOne?.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.passStrength_color_good))
                starLevelTwo?.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.passStrength_color_good))
                starLevelThree?.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.passStrength_color_good))
                starLevelFour?.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.passStrength_color_neutral))
                starLevelFive?.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.passStrength_color_neutral))
            }
            in 50..89 -> {
                starLevelOne?.alpha = 1f
                starLevelTwo?.alpha = 1f
                starLevelThree?.alpha = 1f
                starLevelFour?.alpha = 1f
                starLevelFive?.alpha = 0.5f
                starLevelOne?.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.passStrength_color_strong))
                starLevelTwo?.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.passStrength_color_strong))
                starLevelThree?.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.passStrength_color_strong))
                starLevelFour?.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.passStrength_color_strong))
                starLevelFive?.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.passStrength_color_neutral))
            }
            in 90..1000 -> {
                starLevelOne?.alpha = 1f
                starLevelTwo?.alpha = 1f
                starLevelThree?.alpha = 1f
                starLevelFour?.alpha = 1f
                starLevelFive?.alpha = 1f
                starLevelOne?.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.passStrength_color_very_strong))
                starLevelTwo?.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.passStrength_color_very_strong))
                starLevelThree?.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.passStrength_color_very_strong))
                starLevelFour?.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.passStrength_color_very_strong))
                starLevelFive?.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.passStrength_color_very_strong))
            }
            else -> {
                starLevelOne?.alpha = 1f
                starLevelTwo?.alpha = 0.5f
                starLevelThree?.alpha = 0.5f
                starLevelFour?.alpha = 0.5f
                starLevelFive?.alpha = 0.5f
                starLevelOne?.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.passStrength_color_very_weak))
                starLevelTwo?.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.passStrength_color_neutral))
                starLevelThree?.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.passStrength_color_neutral))
                starLevelFour?.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.passStrength_color_neutral))
                starLevelFive?.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.passStrength_color_neutral))
            }
        }
    }
}