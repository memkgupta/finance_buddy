package com.example.expensemanager

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.expensemanager.dataClasses.Split
import com.example.expensemanager.dataClasses.calculateAmountOwed
import com.example.expensemanager.databinding.SplitCardViewBinding

class SplitAdapter(val context: Context, var splits:ArrayList<Split>):RecyclerView.Adapter<SplitAdapter.MyViewHolder>() {
    class MyViewHolder(binding: SplitCardViewBinding):ViewHolder(binding.root) {
val root = binding.root;
        val title = binding.titleSV
        val amountOwed = binding.amountOwed
        val status = binding.status
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(SplitCardViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun getItemCount(): Int {
       return splits.size;
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
      val split = splits[position]
        holder.title.text = split.title
        holder.amountOwed.text = calculateAmountOwed(MainActivity.auth.uid.toString()).toString()
        holder.status.text = split.status;
        holder.root.setOnClickListener {
           val intent  = Intent(context,SplitActivity::class.java);
            intent.putExtra("id",split.id);
            ContextCompat.startActivity(context,intent,null);
        }
    }
    fun updateData(data:ArrayList<Split>){
        splits = ArrayList();
        splits.addAll(data)
        notifyDataSetChanged()
    }
}