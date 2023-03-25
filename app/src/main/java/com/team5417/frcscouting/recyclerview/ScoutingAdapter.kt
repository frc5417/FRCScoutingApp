package com.team5417.frcscouting.recyclerview

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.recyclerviewapp.ScoutingViewHolder
import com.team5417.frcscouting.DataModel
import com.team5417.frcscouting.R
import com.team5417.frcscouting.ScoutingActivity
import java.io.FileOutputStream
import java.util.NoSuchElementException


class ScoutingAdapter(activity: ScoutingActivity) : RecyclerView.Adapter<ScoutingViewHolder>() {

    val context = activity
    private val adapterData = mutableListOf<DataModel>()
    private lateinit var scoutingViewHolder : ScoutingViewHolder;

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ScoutingViewHolder {

        val layout = when (viewType) {
            TYPE_NUMBER -> R.layout.number
            TYPE_SLIDER -> R.layout.slider
            TYPE_CHECKBOX -> R.layout.checkbox
            TYPE_TEXT -> R.layout.text
            TYPE_HEADER -> R.layout.header
            TYPE_MATCH_AND_TEAM_NUM -> R.layout.match_and_team_numbers
            else -> throw IllegalArgumentException("Invalid type")
        }

        val view = LayoutInflater
            .from(parent.context)
            .inflate(layout, parent, false)

        scoutingViewHolder = ScoutingViewHolder(this, view)

        return scoutingViewHolder
    }


    override fun onBindViewHolder(
        holder: ScoutingViewHolder,
        position: Int
    ) {
        holder.bind(adapterData[position])
    }

    override fun getItemCount(): Int = adapterData.size

    override fun getItemViewType(position: Int): Int {
        return when (adapterData[position]) {
            is DataModel.Number -> TYPE_NUMBER
            is DataModel.Slider -> TYPE_SLIDER
            is DataModel.Checkbox -> TYPE_CHECKBOX
            is DataModel.Text -> TYPE_TEXT
            is DataModel.MatchAndTeamNum -> TYPE_MATCH_AND_TEAM_NUM
            else -> TYPE_HEADER
        }
    }

    fun getData() : List<DataModel> {
        return adapterData;
    }

    fun setData(data: List<DataModel>) {
        adapterData.apply {
            clear()
            addAll(data)
        }

        notifyDataSetChanged()
    }

    fun setMatchNum(match: Int) {
        for (model in adapterData) {
            if (model is DataModel.MatchAndTeamNum) {
                model.matchNum = match
            }
        }
        notifyDataSetChanged()
    }

    private fun getDataToSave() : String {
        var dataToSave = ""
        for (model in adapterData) {
            val toAdd = when (model) {
                is DataModel.Number -> model.id+"="+model.value.toString()
                is DataModel.Checkbox -> model.id+"="+if (model.value) "1" else "0"
                is DataModel.Text -> model.id+"="+model.value.replace("\n", "")
                is DataModel.Slider -> model.id+"="+model.value.toString()
                is DataModel.MatchAndTeamNum -> "mn="+model.matchNum.toString()+",tn="+model.teamNum.toString()
                else -> ""
            }

            if(toAdd != "") {
                dataToSave += if(dataToSave == "") toAdd
                else ",$toAdd"
            }
        }

        return dataToSave;
    }

    fun saveUnsavedData() {
        val dataToSave = getDataToSave();

        if(dataToSave.isNotEmpty()) {
            Handler().postDelayed({
                if(dataToSave == getDataToSave()) {
                    val filename = "storageFile"

                    val fos: FileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)
                    fos.write(dataToSave.toByteArray())
                    fos.close()

                    println("Saving unsaved")
                }
            }, 500)
        }
    }

    companion object {
        private const val TYPE_NUMBER = 0
        private const val TYPE_SLIDER = 1
        private const val TYPE_CHECKBOX = 2
        private const val TYPE_TEXT = 3
        private const val TYPE_HEADER = 4
        private const val TYPE_MATCH_AND_TEAM_NUM = 5
    }
}