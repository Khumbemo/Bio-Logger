package com.biologger.forest

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.biologger.R
import com.biologger.data.TimberVolume
import com.biologger.viewmodel.ScientificViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlin.math.*

class TimberVolumeFragment : Fragment() {

    private val viewModel: ScientificViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_timber_volume, container, false)

        val editLogId = view.findViewById<TextInputEditText>(R.id.editLogId)
        val editLength = view.findViewById<TextInputEditText>(R.id.editLength)
        val editDBase = view.findViewById<TextInputEditText>(R.id.editDBase)
        val editDMid = view.findViewById<TextInputEditText>(R.id.editDMid)
        val editDTop = view.findViewById<TextInputEditText>(R.id.editDTop)
        val checkBark = view.findViewById<MaterialCheckBox>(R.id.checkBarkCorrection)
        val editBark = view.findViewById<TextInputEditText>(R.id.editBark)
        val btnCalc = view.findViewById<MaterialButton>(R.id.btnCalculateSave)
        val cardResults = view.findViewById<MaterialCardView>(R.id.cardResults)

        checkBark.setOnCheckedChangeListener { _, isChecked ->
            view.findViewById<View>(R.id.layoutBark).visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        btnCalc.setOnClickListener {
            val length = editLength.text.toString().toDoubleOrNull() ?: 0.0
            val bark = if (checkBark.isChecked) (editBark.text.toString().toDoubleOrNull() ?: 0.0) else 0.0
            val dBase = (editDBase.text.toString().toDoubleOrNull() ?: 0.0) - 2 * bark
            val dMid = (editDMid.text.toString().toDoubleOrNull() ?: 0.0) - 2 * bark
            val dTop = (editDTop.text.toString().toDoubleOrNull() ?: 0.0) - 2 * bark

            val vHuber = (PI / 4.0) * (dMid / 100.0).pow(2) * length
            val vSmalian = (PI / 8.0) * ((dBase / 100.0).pow(2) + (dTop / 100.0).pow(2)) * length
            val vNewton = (length / 6.0) * ((PI/4)*(dBase/100).pow(2) + 4*(PI/4)*(dMid/100).pow(2) + (PI/4)*(dTop/100).pow(2))

            view.findViewById<TextView>(R.id.resHuber).text = "%.3f m³".format(vHuber)
            view.findViewById<TextView>(R.id.resSmalian).text = "%.3f m³".format(vSmalian)
            view.findViewById<TextView>(R.id.resNewton).text = "%.3f m³".format(vNewton)
            cardResults.visibility = View.VISIBLE

            viewModel.insertTimberVolume(TimberVolume(
                logId = editLogId.text.toString(), species = "", length = length,
                dBase = dBase, dMid = dMid, dTop = dTop, barkThickness = bark,
                volumeHuber = vHuber, volumeSmalian = vSmalian, volumeNewton = vNewton,
                formFactor = vNewton / ((PI/4)*(dBase/100).pow(2)*length), formulaUsed = "All",
                latitude = null, longitude = null
            ))
        }

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            val navHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            v.setPadding(0, 0, 0, maxOf(imeHeight, navHeight))
            insets
        }
        view.setOnClickListener {
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        return view
    }
}