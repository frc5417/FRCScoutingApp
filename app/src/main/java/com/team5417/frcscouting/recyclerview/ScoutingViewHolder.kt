package com.recyclerviewapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.Slider
import com.team5417.frcscouting.DataModel
import com.team5417.frcscouting.R
import com.team5417.frcscouting.recyclerview.ScoutingAdapter

class ScoutingViewHolder(scoutingAdapter: ScoutingAdapter, itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val adapter : ScoutingAdapter = scoutingAdapter;

    private fun bindNumber(item: DataModel.Number) {
        var label : TextView = itemView.findViewById(R.id.label)
        label.text = item.title

        var num : TextView = itemView.findViewById(R.id.number)
        if(item.value == item.value.toInt().toFloat()) {
            num.text = item.value.toInt().toString()
        } else {
            num.text = item.value.toString()
        }

        if(item.value < 0f){
            num.text = "None"
        }

        var downBtn : Button = itemView.findViewById(R.id.btnDown)
        downBtn.setOnClickListener {
            if(item.value - item.step >= item.min) {
                item.value -= item.step

                if(item.value == item.value.toInt().toFloat()) {
                    num.text = item.value.toInt().toString()
                } else {
                    num.text = item.value.toString()
                }

                if(item.value < 0f){
                    num.text = "None"
                }

                adapter.saveUnsavedData();
            }
        }

        var upBtn : Button = itemView.findViewById(R.id.btnUp)
        upBtn.setOnClickListener {
            if(item.value + item.step <= item.max) {
                item.value += item.step

                if(item.value == item.value.toInt().toFloat()) {
                    num.text = item.value.toInt().toString()
                } else {
                    num.text = item.value.toString()
                }

                if(item.value < 0f){
                    num.text = "None"
                }

                adapter.saveUnsavedData()
            }
        }
    }

    private fun bindSlider(item: DataModel.Slider) {
        var label : TextView = itemView.findViewById(R.id.label)
        if(item.value.toInt() == -1) {
            label.text = item.title + " (None)"
        } else {
            label.text = item.title + " (" + item.value.toInt() + ")"
        }

        var slider: Slider = itemView.findViewById(R.id.slider)
        slider.value = item.value
        slider.valueFrom = item.min
        slider.valueTo = item.max
        slider.stepSize = item.step

        slider.addOnChangeListener(Slider.OnChangeListener { _, value, _ ->
            run {
                if(value.toInt() == -1) {
                    label.text = item.title + " (None)"
                } else {
                    label.text = item.title + " (" + value.toInt() + ")"
                }
                item.value = value
                adapter.saveUnsavedData()
            }
        })
    }

    private fun bindCheckbox(item: DataModel.Checkbox) {
        var label : TextView = itemView.findViewById(R.id.label)
        label.text = item.title

        var checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
        checkBox.isChecked = item.value
        checkBox.setOnClickListener { v ->
            item.value = (v as CheckBox).isChecked
            adapter.saveUnsavedData()
        }
    }

    private fun bindDropdown(item: DataModel.DropDown) {
        var label : TextView = itemView.findViewById(R.id.label)
        label.text = item.title

        var dropdown: Spinner = itemView.findViewById(R.id.spinner)
        val spinnerAdapter = ArrayAdapter(adapter.context, R.layout.support_simple_spinner_dropdown_item, item.options.toList())
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        dropdown.adapter = spinnerAdapter
        dropdown.setSelection(item.index)

        dropdown.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                item.index = i //i is the position selected
                adapter.saveUnsavedData()

                (dropdown.getChildAt(0) as TextView).setTextColor(Color.WHITE)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                return
            }
        })
    }

    private fun bindText(item: DataModel.Text) {
        var label : TextView = itemView.findViewById(R.id.label)
        label.text = item.title

        var textedit : EditText = itemView.findViewById(R.id.textEdit)
        textedit.setText(item.value)

        textedit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int,count: Int, after: Int) {}
            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                item.value = textedit.text.toString()
                adapter.saveUnsavedData();
            }
        })
    }

    private fun bindMatchAndTeamNum(item: DataModel.MatchAndTeamNum) {
        var matchNumEdit : EditText = itemView.findViewById(R.id.matchNum)
        if(item.matchNum != -1) matchNumEdit.setText(item.matchNum.toString())
        else matchNumEdit.setText("")

        matchNumEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int,count: Int, after: Int) {}
            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if(matchNumEdit.text.toString().isEmpty()) {
                    item.matchNum = -1
                    adapter.saveUnsavedData();
                    return
                }

                if(matchNumEdit.text.toString().contains('-')) {
                    matchNumEdit.setText(matchNumEdit.text.toString().replace("-", ""));
                    matchNumEdit.setSelection(matchNumEdit.length())
                }

                if(matchNumEdit.text.toString().length > 3) {
                    matchNumEdit.setText(matchNumEdit.text.toString().substring(0, 3))
                    matchNumEdit.setSelection(matchNumEdit.length())
                }
                try {
                    item.matchNum = matchNumEdit.text.toString().toInt()
                    adapter.saveUnsavedData();
                } catch (e: NumberFormatException) {
                    matchNumEdit.setText("")
                }
            }
        })



        var teamNumEdit : EditText = itemView.findViewById(R.id.teamNum)
        if(item.teamNum != -1) teamNumEdit.setText(item.teamNum.toString())
        else teamNumEdit.setText("")
        teamNumEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int,count: Int, after: Int) {}
            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if(teamNumEdit.text.toString().isEmpty()) {
                    item.teamNum = -1
                    adapter.saveUnsavedData();
                    return
                }

                if(teamNumEdit.text.toString().contains('-')) {
                    teamNumEdit.setText(teamNumEdit.text.toString().replace("-", ""));
                    teamNumEdit.setSelection(teamNumEdit.length())
                }

                if(teamNumEdit.text.toString().length > 5) {
                    teamNumEdit.setText(teamNumEdit.text.toString().substring(0, 5))
                    teamNumEdit.setSelection(teamNumEdit.length())
                }
                try {
                    item.teamNum = teamNumEdit.text.toString().toInt()
                    adapter.saveUnsavedData();
                } catch (e: NumberFormatException) {
                    teamNumEdit.setText("")
                }

            }
        })

        matchNumEdit.setOnFocusChangeListener { _, b ->
            if (!b) {
                if (matchNumEdit.text.isNotEmpty()) {
                    var teamNum = adapter.context.getTeamNumFromMatch(matchNumEdit.text.toString().toInt())
                    if( teamNum.isEmpty() ) return@setOnFocusChangeListener;
                    teamNumEdit.setText(teamNum)
                    item.teamNum = teamNum.toInt()
                    adapter.saveUnsavedData()
                }
            }
        }
    }

    private fun bindHeader(item: DataModel.Header) {
        var label : TextView = itemView.findViewById(R.id.label)
        label.text = item.title
    }

    fun bind(dataModel: DataModel) {
        when (dataModel) {
            is DataModel.Number -> bindNumber(dataModel)
            is DataModel.Slider -> bindSlider(dataModel)
            is DataModel.Checkbox -> bindCheckbox(dataModel)
            is DataModel.Text -> bindText(dataModel)
            is DataModel.Header -> bindHeader(dataModel)
            is DataModel.DropDown -> bindDropdown(dataModel)
            is DataModel.MatchAndTeamNum -> bindMatchAndTeamNum(dataModel)
        }
    }
}