package com.example.expensemanager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensemanager.dataClasses.Transaction
import com.example.expensemanager.dataClasses.TransactionType
import com.example.expensemanager.databinding.FragmentExpenseTrackerBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ExpenseTracker.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExpenseTracker : Fragment() {
    lateinit var binding:FragmentExpenseTrackerBinding
    lateinit var transactionAdapter: ExpenseAdapter

 companion object{
     lateinit var dbRef:DatabaseReference
     lateinit var transactions:ArrayList<Transaction>
     lateinit var readDbRef:DatabaseReference
     lateinit var deleteRef:DatabaseReference
 }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_expense_tracker, container, false)
        binding = FragmentExpenseTrackerBinding.bind(view)


        transactions = ArrayList()

initLayout()
        // Inflate the layout for this fragment
        return view
    }

private fun initLayout(){

    binding.transactionsRV.setHasFixedSize(true)
    binding.transactionsRV.setItemViewCacheSize(13)
    transactionAdapter = ExpenseAdapter(requireContext(), transactions)
    binding.transactionsRV.layoutManager = LinearLayoutManager(requireContext())


    binding.transactionsRV.adapter = transactionAdapter
    dbRef = MainActivity.database.child("expenses");
    readDbRef = dbRef.child(MainActivity.auth.uid.toString())
//deleteRef = FirebaseDatabase.getInstance().getReference().child("expenses")
deleteRef = dbRef.child(MainActivity.auth.uid.toString())
    readDbRef.addValueEventListener(object :ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
        binding.progressBar.visibility = View.GONE
            val tempTransactions: ArrayList<Transaction> = ArrayList();
            var totalIncome = 0
            var totalExpense = 0
          snapshot.children.forEach{

               val transactionObj = it.getValue<Transaction>()!!
               transactionObj.id = it.key.toString();
               tempTransactions.add(transactionObj)
              if(transactionObj.type.equals(TransactionType.INCOME)){
                  totalIncome+=transactionObj.amount
              }
              else{
                  totalExpense+=transactionObj.amount
              }
           }

            binding.totalExpenseTV.text = "Total Expense: $totalExpense"
            binding.totalIncomeTV.text = "Total Income: $totalIncome"
            transactionAdapter.updateData(tempTransactions)
        }

        override fun onCancelled(error: DatabaseError) {
            binding.progressBar.visibility = View.GONE
            Log.e("FIREBASE", "error: ${error.message}")
            Toast.makeText(
                context,
                "Some error occured",
                Toast.LENGTH_SHORT,
            ).show()
        }

    })
}
}