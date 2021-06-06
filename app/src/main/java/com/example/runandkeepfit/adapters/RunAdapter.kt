package com.example.runandkeepfit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.runandkeepfit.R
import com.example.runandkeepfit.db.Run
import com.example.runandkeepfit.others.TrackingUtility
import java.text.SimpleDateFormat
import java.util.*



class RunAdapter : RecyclerView.Adapter<RunAdapter.RunViewHolder>() {

    inner class RunViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
    val differCallBack = object : DiffUtil.ItemCallback<Run>(){
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
         return oldItem.hashCode() == newItem.hashCode()
            }
    }

    val differ = AsyncListDiffer(this,differCallBack)

    fun submitList(list:List<Run>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        return RunViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_run,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = differ.currentList[position]
        val ivRunImage = holder.itemView.findViewById<ImageView>(R.id.ivRunImage)
        holder.itemView.apply {
            Glide.with(this).load(run.img).into(ivRunImage)
        }

                val calendar = Calendar.getInstance().apply {
            timeInMillis = run.timeStamp
        }
        val tvDate = holder.itemView.findViewById<TextView>(R.id.tvDate)
        val dateFormat = SimpleDateFormat("dd.MM.yy",Locale.getDefault())
        tvDate.text = dateFormat.format(calendar.time)

        val tvAvgSpeed = holder.itemView.findViewById<TextView>(R.id.tvAvgSpeed)
        val avgSpeed = "${run.avgSpeedInKmH}Km/H"
        tvAvgSpeed.text = avgSpeed


        val tvDistance = holder.itemView.findViewById<TextView>(R.id.tvDistance)
        val distanceInKM = "${run.distanceInMeters/ 1000f}km"
        tvDistance.text = distanceInKM

        val tvTime = holder.itemView.findViewById<TextView>(R.id.tvTime)
        tvTime.text = TrackingUtility.getFormattedStopWatchTime(run.timeInMilli)

        val tvCaloriesBurned = holder.itemView.findViewById<TextView>(R.id.tvCalories)
        val caloriesBurned = "${run.caloriesBurned}kcal"
        tvCaloriesBurned.text = caloriesBurned

    }

}