package com.example.expensemanager

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.expensemanager.dataClasses.Transaction
import com.example.expensemanager.dataClasses.TransactionCategory
import com.example.expensemanager.dataClasses.TransactionType
import com.example.expensemanager.databinding.ExpenseViewBinding
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalDate

class ExpenseAdapter(private val context:Context, var transactions:ArrayList<Transaction>):RecyclerView.Adapter<ExpenseAdapter.MyVHolder> (){
    class MyVHolder(binding:ExpenseViewBinding):ViewHolder(binding.root) {
        val  root = binding.root
          val amount = binding.textAmount
          val category = binding.textCategory

        val description = binding.textDescription
        val date = binding.textDate
        val deleteBt = binding.deleteTransactionBT
        val editBt = binding.editTransactionBT

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVHolder {
       return MyVHolder(ExpenseViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    override fun onBindViewHolder(holder: MyVHolder, position: Int) {
      holder.amount.text = transactions[position].amount.toString();
        holder.category.text = transactions[position].category.toString()
        holder.date.text = transactions[position].date.toString()
        holder.description.text = transactions[position].description
        if(transactions[position].type.equals(TransactionType.EXPENSE)){
            holder.root.setBackgroundColor(ContextCompat.getColor(context,R.color.red_light))
        }else{
            holder.root.setBackgroundColor(ContextCompat.getColor(context,R.color.green_light))
        }
        holder.deleteBt.setOnClickListener{

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete this transaction")
            builder.setMessage("Sure you want to delete ?")
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setPositiveButton("Yes"){
                dialogInterface,which->
                run {
                    ExpenseTracker.deleteRef!!.child(transactions[position].id).removeValue().addOnFailureListener {
                        Log.e("FIREBASE",it.localizedMessage)
                    }
                        .addOnSuccessListener {
                            Toast.makeText(context,"Transaction Deleted Successfully",Toast.LENGTH_SHORT).show();

                        }
                }
            }
          
            builder.setNeutralButton("Cancel"){dialogInterface , which ->
                Toast.makeText(context,"Not deleting this one",Toast.LENGTH_LONG).show()
            }
            val alertDialog: AlertDialog = builder.create()

            alertDialog.setCancelable(false)
            alertDialog.show()
        }

        holder.editBt.setOnClickListener {
            val transaction = transactions[position]
             val expenseDialog = Dialog(context)
            expenseDialog.setContentView(R.layout.add_expense_dialog)
            expenseDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            expenseDialog.window?.decorView?.setPadding(50)
            expenseDialog.setCancelable(false)

            val expenseCategories = context.resources.getStringArray(R.array.expense_categories)
            val arrayAdapter = ArrayAdapter(context, R.layout.dropdown_item, expenseCategories)
            val autoCompleteTV =expenseDialog.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView);
            val amountEditText = expenseDialog.findViewById<TextInputEditText>(R.id.expenseAmount)
            val descriptionEditText = expenseDialog.findViewById<TextInputEditText>(R.id.expenseDescription)

            autoCompleteTV.setAdapter(arrayAdapter)
           amountEditText.setText(transaction.amount.toString())
           descriptionEditText.setText(transaction.description)


            expenseDialog.findViewById<Button>(R.id.addExpenseBTEDialog).setText("Update Transaction")
            var type = TransactionType.EXPENSE
            expenseDialog.findViewById<RadioGroup>(R.id.radio_group).setOnCheckedChangeListener { group, i ->
                val radio: RadioButton = expenseDialog.findViewById(i);
                type = TransactionType.valueOf(radio.text.toString());
            }
            expenseDialog.findViewById<Button>(R.id.addExpenseBTEDialog).setOnClickListener {
                val category = autoCompleteTV.text.toString()

                val transactionUpdated = Transaction("",amountEditText.text.toString().toInt(),type,
                    TransactionCategory.valueOf(category),descriptionEditText.text.toString(),
                    LocalDate.now().toString())
                MainActivity.database.child("expenses").child(MainActivity.auth.uid.toString()).child(transaction.id).setValue(transactionUpdated).addOnFailureListener {
                    Toast.makeText(context,it.localizedMessage,Toast.LENGTH_LONG).show()
                    expenseDialog.dismiss()
                }.addOnCompleteListener {
                    Toast.makeText(context,"Expense Updated",Toast.LENGTH_SHORT).show()
                    expenseDialog.dismiss()

                }
            }
            expenseDialog.findViewById<Button>(R.id.cancelBTEDialog).setOnClickListener{
                expenseDialog.dismiss()
            }
           expenseDialog.show()
        }
    }
    fun updateData(expenses:List<Transaction>){
        transactions = ArrayList();
        transactions.addAll(expenses)
        notifyDataSetChanged()
    }
}