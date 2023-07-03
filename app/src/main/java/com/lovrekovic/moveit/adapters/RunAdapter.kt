package com.lovrekovic.moveit.adapters

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lovrekovic.moveit.R
import com.lovrekovic.moveit.database.Run
import com.lovrekovic.moveit.other.TrackingUtility
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class RunAdapter: RecyclerView.Adapter<RunAdapter.RunViewHolder>() {
    inner class RunViewHolder(itemVIew: View):RecyclerView.ViewHolder(itemVIew)

    //tool koji racuna razlike izmedu 2 liste,ne moramo updejtat sve samo ono sto je novo

    val diffCallback = object : DiffUtil.ItemCallback<Run> (){
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return  oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list : List<Run>) = differ.submitList(list)

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
        val ivRunImage: ImageView = holder.itemView.findViewById(R.id.ivRunImage)
        val tvDate : TextView = holder.itemView.findViewById(R.id.tvDate)
        val tvAvgSpeed : TextView = holder.itemView.findViewById(R.id.tvAvgSpeed)
        val tvDistance: TextView = holder.itemView.findViewById(R.id.tvDistance)
        val tvTime : TextView = holder.itemView.findViewById(R.id.tvTime)
        val tvCalories : TextView = holder.itemView.findViewById(R.id.tvCalories)



        val run = differ.currentList[position]
        holder.itemView.apply{
            Glide.with(this).load(run.img).into(ivRunImage)

            val calendar = Calendar.getInstance().apply {
                timeZone = TimeZone.getTimeZone(run.timestamp.toString())
            }
            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            tvDate.text = dateFormat.format(calendar.time)

            val avgSpeed = "${run.avgSpeedKMH}km/h"
            tvAvgSpeed.text = avgSpeed

            val distanceInKm = "${run.distanceInMeters / 1000f}km"
            tvDistance.text = distanceInKm

            tvTime.text = TrackingUtility.getFormattedStopWatchTime(run.timeInMillis)

            val caloriesBurned = "${run.caloriesBurned}kcal"
            tvCalories.text = caloriesBurned
        }
    }
}