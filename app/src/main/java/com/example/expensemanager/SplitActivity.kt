package com.example.expensemanager

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensemanager.dataClasses.Contributor
import com.example.expensemanager.dataClasses.Split
import com.example.expensemanager.dataClasses.SplitExpense
import com.example.expensemanager.dataClasses.SplitExpenseParticipant
import com.example.expensemanager.databinding.ActivitySplitBinding
import com.example.expensemanager.databinding.AddSplitExpenseDialogBinding
import com.example.expensemanager.databinding.AddSplitMemberDialogBinding
import com.example.expensemanager.split.SplitExpenseAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class SplitActivity : AppCompatActivity() {
    lateinit var binding:ActivitySplitBinding
    lateinit var id: String
    private lateinit var dbRef:DatabaseReference
    lateinit var split: Split
    private lateinit var memberDialog: Dialog
    private lateinit var expenseDialog:Dialog
    private lateinit var expenseAdapter:SplitExpenseAdapter
    lateinit var expenses:ArrayList<SplitExpense>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplitBinding.inflate(layoutInflater);

        setContentView(binding.root);

initLayout()
        binding.expensesRVSA.setHasFixedSize(true)
        binding.expensesRVSA.setItemViewCacheSize(13)
        expenses = ArrayList<SplitExpense>()



binding.btBackSplit.setOnClickListener{
finish()
}
        val memberDialogBinding = AddSplitMemberDialogBinding.inflate(layoutInflater)
        memberDialog = Dialog(this)
       memberDialog.setContentView(memberDialogBinding.root)
        memberDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//        expenseDialog.window?.setBackgroundDrawable(getDrawable(R.drawable.dialog_background))
        memberDialog.window?.decorView?.setPadding(50)
        memberDialog.setCancelable(false)


        val contact = memberDialogBinding.memberContact
        val name =  memberDialogBinding.memberName



memberDialogBinding.addMemberFinalBT.setOnClickListener {
    val contributor = Contributor((split.contributors.size-1).toString(),name.text.toString(),0.0,0.0,contact.text.toString())
    split.contributors.add(contributor)
    dbRef.setValue(split).addOnFailureListener {
        Log.e("FIREBASE",it.message.toString())
        Toast.makeText(this@SplitActivity,"Failed",Toast.LENGTH_SHORT).show();
    }
        .addOnSuccessListener {
            Toast.makeText(this@SplitActivity,"Member Added",Toast.LENGTH_SHORT).show();
        }
        .addOnCompleteListener {
            memberDialog.dismiss()
            name.setText("")
            contact.setText("")
        }

}

        memberDialogBinding.cancelBTEDialog.setOnClickListener {
            memberDialog.dismiss()
            name.setText("")
            contact.setText("")
        }


        binding.addMemberBt.setOnClickListener {
memberDialog.show()

        }

        val expenseDialogBinding = AddSplitExpenseDialogBinding.inflate(layoutInflater)
        expenseDialog = Dialog(this)
        expenseDialog.setContentView(expenseDialogBinding.root)
        expenseDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//        expenseDialog.window?.setBackgroundDrawable(getDrawable(R.drawable.dialog_background))
        expenseDialog.window?.decorView?.setPadding(50)
        expenseDialog.setCancelable(false)


        val description = expenseDialogBinding.expenseDescription
        val amount = expenseDialogBinding.expenseAmount

val expenseParticipant = ArrayList<Pair<String,String>>();
val container = expenseDialogBinding.contributorsCheckBoxLayout;


       expenseDialogBinding.addExpenseBTEDialog.setOnClickListener {
val participants = ArrayList<SplitExpenseParticipant>()
           val amountEach:Double = amount.text.toString().toDouble()/expenseParticipant.size;
           var i :Int = 0;
           Log.d("FFTC",expenseParticipant.toString())
           expenseParticipant.forEach {
               pair->
               run {

                   participants.add(SplitExpenseParticipant(pair.first, amountEach, ""))
                   val contributor = split.contributors.find { it.id == pair.first }
                  Log.e("FIREBASE_DE",contributor.toString())
                   dbRef.child("contributors").child(i.toString()).child("totalIn").setValue(contributor!!.totalIn+amountEach).addOnFailureListener {
Log.d("FFTCTC","Hellp " + contributor.toString())
                       throw RuntimeException(it.message);

                   }
                   i++
               }
           }
           val expense = SplitExpense("",description.text.toString(),amount.text.toString().toDouble(), System.currentTimeMillis(),participants)
           dbRef.child("expenses").push().setValue(expense).addOnFailureListener {
               Log.e("FIREBASE",it.message.toString())
               Toast.makeText(this@SplitActivity,"Some error occured",Toast.LENGTH_SHORT).show();
           }.addOnSuccessListener {
               dbRef.child("totalAmount").setValue(split.totalAmount+amount.text.toString().toDouble())
               Toast.makeText(this@SplitActivity,"Expense Added Successfully",Toast.LENGTH_SHORT).show();
           }
               .addOnCompleteListener {
                   expenseDialog.dismiss()
                   amount.setText("")
                   description.setText("")
               }

        }

        expenseDialogBinding.cancelBTEDialog.setOnClickListener {
           expenseDialog.dismiss()

        }


        binding.addMemberBt.setOnClickListener {
            memberDialog.show()

        }
        binding.addExpenseBTSA.setOnClickListener {
            for( contributor in split.contributors){
                val checkBox = CheckBox(expenseDialog.context)
                checkBox.text = contributor.name

                checkBox.setOnClickListener {
                    if(checkBox.isChecked){
                        Log.d("FFTC","checked")
                        expenseParticipant.add(Pair(contributor.id,contributor.name))

                    }
                    else{
                        Log.d("FFTC","removed")
                        expenseParticipant.removeIf { it.first.equals(contributor.id) }
                    }
                }
                container.addView(checkBox)
            }
            expenseDialog.show()
        }
    }

    private fun initLayout(){

        id = intent.getStringExtra("id")!!;

        dbRef = MainActivity.database.child("splits").child(MainActivity.auth.uid.toString()).child(id);
        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.progressBarSA.visibility = View.GONE
                if(snapshot.hasChildren()){
                    split = snapshot.getValue<Split>()!!
                    binding.titleSA.text = split.title
                    binding.totalExpenseSA.text ="Total Amount "+ split.totalAmount.toString()
                    split.expenses.forEach {
                        expenses.add(it.value)
                        expenseAdapter = SplitExpenseAdapter(this@SplitActivity,expenses)
                        binding.expensesRVSA.layoutManager = LinearLayoutManager(this@SplitActivity)


                        binding.expensesRVSA.adapter = expenseAdapter
                    }
                }
                else{
                    Toast.makeText(this@SplitActivity,"Some Error Occured",Toast.LENGTH_SHORT).show();
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FIREBASE",error.message)
                Toast.makeText(this@SplitActivity,"Some Error Occured",Toast.LENGTH_SHORT).show();
            }

        })

    }
}