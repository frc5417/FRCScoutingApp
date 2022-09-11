package com.recyclerviewapp

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.marginBottom
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.Slider
import com.team5417.frcscouting.DataModel
import com.team5417.frcscouting.R
import kotlinx.android.synthetic.main.slider.view.*


class ScoutingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private fun bindNumber(item: DataModel.Number) {
        var label : TextView = itemView.findViewById(R.id.label)
        label.text = item.title

        var num : TextView = itemView.findViewById(R.id.number)
        num.setText(item.value.toString())

        var downBtn : Button = itemView.findViewById(R.id.btnDown)
        downBtn.setOnClickListener {
            if(item.value - item.step >= item.min) {
                item.value -= item.step
                num.setText(item.value.toString())
            }
        }

        var upBtn : Button = itemView.findViewById(R.id.btnUp)
        upBtn.setOnClickListener {
            if(item.value + item.step <= item.max) {
                item.value += item.step
                num.setText(item.value.toString())
            }
        }
    }

    private fun bindSlider(item: DataModel.Slider) {
        var label : TextView = itemView.findViewById(R.id.label)
        label.text = item.title + " (" + item.value.toInt() + ")"

        var slider: Slider = itemView.findViewById(R.id.slider)
        slider.value = item.value
        slider.valueFrom = item.min
        slider.valueTo = item.max
        slider.stepSize = item.step

        slider.addOnChangeListener(Slider.OnChangeListener { _, value, _ ->
            run {
                label.text = item.title + " (" + value.toInt() + ")"
                item.value = value
            }
        })
    }

    private fun bindCheckbox(item: DataModel.Checkbox) {
        var label : TextView = itemView.findViewById(R.id.label)
        label.text = item.title

        var checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
        checkBox.isChecked = item.value
        checkBox.setOnCheckedChangeListener { _, b ->
            item.value = b
        }
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
            }
        })
    }

    private fun bindMatchAndTeamNum(item: DataModel.MatchAndTeamNum) {
        var matchNumEdit : EditText = itemView.findViewById(R.id.matchNum)
        matchNumEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int,count: Int, after: Int) {}
            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if(matchNumEdit.text.toString().length == 0) {
                    item.teamNum = -1
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
                } catch (e: NumberFormatException) {
                    matchNumEdit.setText("")
                }
            }
        })

        var teamNumEdit : EditText = itemView.findViewById(R.id.teamNum)
        teamNumEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int,count: Int, after: Int) {}
            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if(teamNumEdit.text.toString().length == 0) {
                    item.teamNum = -1
                    return
                }

                if(teamNumEdit.text.toString().contains('-')) {
                    teamNumEdit.setText(teamNumEdit.text.toString().replace("-", ""));
                    teamNumEdit.setSelection(teamNumEdit.length())
                }

                if(teamNumEdit.text.toString().length > 4) {
                    teamNumEdit.setText(teamNumEdit.text.toString().substring(0, 4))
                    teamNumEdit.setSelection(teamNumEdit.length())
                }
                try {
                    item.teamNum = teamNumEdit.text.toString().toInt()
                } catch (e: NumberFormatException) {
                    teamNumEdit.setText("")
                }
            }
        })
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
            is DataModel.MatchAndTeamNum -> bindMatchAndTeamNum(dataModel)
        }
    }
}