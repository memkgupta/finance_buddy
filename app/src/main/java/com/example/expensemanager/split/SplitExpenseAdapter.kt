package com.example.expensemanager.split

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.expensemanager.dataClasses.SplitExpense
import com.example.expensemanager.databinding.SplitExpenseViewBinding
import java.text.DateFormat
import java.util.Date

class SplitExpenseAdapter(val context: Context,val expenses:ArrayList<SplitExpense>): RecyclerView.Adapter<SplitExpenseAdapter.MyViewHolder>() {
    class MyViewHolder(binding:SplitExpenseViewBinding):ViewHolder(binding.root) {
val root = binding.root
        var amount = binding.splitExpenseAmount
        var description = binding.splitTextDescription
        val date = binding.textDate
        val contributorRV = binding.contributorsRV
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return SplitExpenseAdapter.MyViewHolder(
            SplitExpenseViewBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
      return expenses.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val expense = expenses[position]
        val dateFormat: DateFormat = DateFormat.getDateTimeInstance()
        holder.date.text =dateFormat.format(Date(expense.timestamp))
        holder.amount.text = expense.amount.toString()
        holder.description.text = expense.description

    }
}