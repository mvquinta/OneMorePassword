package com.example.onemorepassword

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
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

        //All fun that update UI from viewModel
        savedGeneratedPass()

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

        //Initializing clipBoardManager and clip data
        //Code from geeksforgeeks.org - clipboard-in-android
        var clipBoardManager =
            this.activity?.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
        var clipData: ClipData

        //Action when copy button is clicked
        copyBtn.setOnClickListener {
            // Text from the edit text is stored in a val
            val txtCopy = copyTxt!!.text.toString()
            // clip data is initialized with the text variable declared above
            clipData = ClipData.newPlainText("textMy", txtCopy)
            // Clipboard saves this clip object
            clipBoardManager.setPrimaryClip(clipData)
            //Button Animation
            copyBtn.startAnimation(animScale)
            Toast.makeText(requireActivity(), "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        //Set seekBar Listener
        //onProgressChanged checks progress in realtime and update level strength warning
        //onStopTrackingTouch sends a toast with password length selected. I think I'll delete this
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
            }
        })

        //Set switches to check and update level strength warning when toggled
        val switchLowButton = binding.lowLettersSwitch
        switchLowButton.setOnClickListener { warningPassLevel(lengthPassTextNumber.text.toString().toInt()) }
        val switchUpButton = binding.upLettersSwitch
        switchUpButton.setOnClickListener { warningPassLevel(lengthPassTextNumber.text.toString().toInt()) }
        val switchNumbersButton = binding.numbersSwitch
        switchNumbersButton.setOnClickListener { warningPassLevel(lengthPassTextNumber.text.toString().toInt()) }
        val switchSymbolsButton = binding.symbolsSwitch
        switchSymbolsButton.setOnClickListener { warningPassLevel(lengthPassTextNumber.text.toString().toInt()) }

        //MAIN FUNCTION executed
        //When Generate button is clicked I check if all switches are OFF. Is so, a toast warning shows.
        //If at least one of them is ON, generatePass() is executed
        generateButton.setOnClickListener {
            //if (switchLowLetters() == false && switchUpLetters() == false && switchNumbers() == false && switchSymbols() == false) {
            if (passStrengthSwitchLevel() == 0) {
                Toast.makeText(requireActivity(), "You must select at least one option please.", Toast.LENGTH_SHORT).show()
            } else {
                generatePass()
            }
            //Button Animation
            generateButton.startAnimation(animScale)
        }

    }


    //This function is executed when Generate button is clicked. It looks for the TextView and edit/inserts generated password
    private fun generatePass() {
        viewModel.randomPass(mySizePass(), switchLowLetters(), switchUpLetters(), switchNumbers(), switchSymbols())
        savedGeneratedPass()
    }

    //This fun() checks how many switch are ON and saves it to an Int variable.
    //If all switches are off, numSwitchesOn = 0, I wont be able to create a password
    //If numSwitchesOn = 1, 2, 3 or 4 it means, respectively, a weak, good, strong and very strong password
    private fun passStrengthSwitchLevel(): Int {
        val myLowLettersSwitch = binding.lowLettersSwitch
        val myUpLettersSwitch = binding.upLettersSwitch
        val myNumbersSwitch = binding.numbersSwitch
        val mySymbolsSwitch = binding.symbolsSwitch
        var numSwitchesOn: Int = 0
        val mySwitchList = listOf(myLowLettersSwitch, myUpLettersSwitch, myNumbersSwitch, mySymbolsSwitch)

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
        return value.getText().toString().toInt()
    }

    //Methods to update UI and respect viewModel set up
    private fun savedGeneratedPass() {
        binding.passText.text = viewModel.finalPass
        val generatedPassTextSize = binding.lengthPassText.text.toString().toInt()
        when (generatedPassTextSize) {
            in 8..17 -> binding.passText.textSize = 28f
            in 18..25-> binding.passText.textSize = 24f
            in 26..50 -> binding.passText.textSize = 20f
            else -> binding.passText.textSize = 28f
        }
    }

    //Funtions with all animations that are created programmatically
    fun animations() {
        val generateButtonAnim = binding.generateButton
        val copyBtnAnim = binding.copyTextButton
        val copyTxtAnim = binding.passText
        val lengthPassTxtAnim = binding.lengthPassText
        val passStrengthTxtAnim = binding.passStrengthText
        val lengthSizeSeekBarAnim = binding.lengthSizeSeekBar
        val lowLettersSwitchAnim = binding.lowLettersSwitch
        val upLettersSwitchAnim = binding.upLettersSwitch
        val numbersSwitchAnim = binding.numbersSwitch
        val symbolsSwitchAnim = binding.symbolsSwitch
        val ompHeaderImageImageAnim = binding.ompHeaderImageImageView
        val myList = listOf(generateButtonAnim, copyBtnAnim, copyTxtAnim, lengthPassTxtAnim, passStrengthTxtAnim,
            lengthSizeSeekBarAnim, lowLettersSwitchAnim, upLettersSwitchAnim, numbersSwitchAnim, symbolsSwitchAnim, ompHeaderImageImageAnim)
        for (i in myList) {
            i?.alpha = 0f
            i?.translationY = 50f
            i?.animate()?.alpha(1f)?.translationYBy(-50f)?.setStartDelay(200)?.duration = 1500
        }
    }


    fun warningPassLevel(seekBarNumber: Int) {
        val myLengthSizeBar: SeekBar = binding.lengthSizeSeekBar
        val passStrengthWarning = binding.passStrengthText
        val myProgressiveNum = passStrengthLevel(seekBarNumber)
        //Toast.makeText(requireActivity(),"Pass Strength level actual é: " + myProgressiveNum, Toast.LENGTH_SHORT).show()
        when (myProgressiveNum) {
            in 2..24 -> {
                passStrengthWarning.text = getString(R.string.passStrength_weak)
                passStrengthWarning.setTextColor(
                        ContextCompat.getColor(
                                requireActivity(),
                                R.color.passStrength_color_weak
                        )
                )
                myLengthSizeBar.thumb.setTint(
                        ContextCompat.getColor(
                                requireActivity(),
                                R.color.passStrength_color_weak
                        )
                )
                myLengthSizeBar.progressDrawable.setTint(
                        ContextCompat.getColor(
                                requireActivity(),
                                R.color.passStrength_color_weak
                        )
                )
            }
            in 25..49 -> {
                passStrengthWarning.text = getString(R.string.passStrength_good)
                passStrengthWarning.setTextColor(
                        ContextCompat.getColor(
                                requireActivity(),
                                R.color.passStrength_color_good
                        )
                )
                myLengthSizeBar.thumb.setTint(
                        ContextCompat.getColor(
                                requireActivity(),
                                R.color.passStrength_color_good
                        )
                )
                myLengthSizeBar.progressDrawable.setTint(
                        ContextCompat.getColor(
                                requireActivity(),
                                R.color.passStrength_color_good
                        )
                )
            }
            in 50..89 -> {
                passStrengthWarning.text = getString(R.string.passStrength_strong)
                passStrengthWarning.setTextColor(
                        ContextCompat.getColor(
                                requireActivity(),
                                R.color.passStrength_color_strong
                        )
                )
                myLengthSizeBar.thumb.setTint(
                        ContextCompat.getColor(
                                requireActivity(),
                                R.color.passStrength_color_strong
                        )
                )
                myLengthSizeBar.progressDrawable.setTint(
                        ContextCompat.getColor(
                                requireActivity(),
                                R.color.passStrength_color_strong
                        )
                )
            }
            in 90..1000 -> {
                passStrengthWarning.text = getString(R.string.passStrength_very_strong)
                passStrengthWarning.setTextColor(
                        ContextCompat.getColor(
                                requireActivity(),
                                R.color.passStrength_color_very_strong
                        )
                )
                myLengthSizeBar.thumb.setTint(
                        ContextCompat.getColor(
                                requireActivity(),
                                R.color.passStrength_color_very_strong
                        )
                )
                myLengthSizeBar.progressDrawable.setTint(
                        ContextCompat.getColor(
                                requireActivity(),
                                R.color.passStrength_color_very_strong
                        )
                )
            }
            else -> {
                passStrengthWarning.text = getString(R.string.passStrength_very_weak)
                passStrengthWarning.setTextColor(
                        ContextCompat.getColor(
                                requireActivity(),
                                R.color.passStrength_color_very_weak
                        )
                )
                myLengthSizeBar.thumb.setTint(
                        ContextCompat.getColor(
                                requireActivity(),
                                R.color.passStrength_color_very_weak
                        )
                )
                myLengthSizeBar.progressDrawable.setTint(
                        ContextCompat.getColor(
                                requireActivity(),
                                R.color.passStrength_color_very_weak
                        )
                )
            }
        }
    }

}