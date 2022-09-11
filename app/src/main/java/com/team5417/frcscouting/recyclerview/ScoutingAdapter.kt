package com.team5417.frcscouting.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.recyclerviewapp.ScoutingViewHolder
import com.team5417.frcscouting.DataModel
import com.team5417.frcscouting.R

class ScoutingAdapter : RecyclerView.Adapter<ScoutingViewHolder>() {

    private val adapterData = mutableListOf<DataModel>()

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

        return ScoutingViewHolder(view)
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