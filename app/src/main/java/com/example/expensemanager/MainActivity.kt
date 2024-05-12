package com.example.expensemanager

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import com.example.expensemanager.dataClasses.Contributor
import com.example.expensemanager.dataClasses.Split
import com.example.expensemanager.dataClasses.Transaction
import com.example.expensemanager.dataClasses.TransactionCategory
import com.example.expensemanager.dataClasses.TransactionType
import com.example.expensemanager.databinding.ActivityMainBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.to_bottom_anim) }
    private var clicked:Boolean = false
    lateinit var expenseDialog:Dialog
    lateinit var expenseDialogAddBt:Button
    lateinit var splitDialog: Dialog
    lateinit var expenseDialogCancelBt:Button
    companion object {
        lateinit var auth:FirebaseAuth
        lateinit var database: DatabaseReference
    }
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

//

        if(auth.currentUser==null){
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        replaceFragment(ExpenseTracker())

        expenseDialog = Dialog(this)
        expenseDialog.setContentView(R.layout.add_expense_dialog)
        expenseDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
//        expenseDialog.window?.setBackgroundDrawable(getDrawable(R.drawable.dialog_background))
        expenseDialog.window?.decorView?.setPadding(50)
        expenseDialog.setCancelable(false)
        val expenseCategories = resources.getStringArray(R.array.expense_categories)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, expenseCategories)
        val autoCompleteTV =expenseDialog.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView);
        autoCompleteTV.setAdapter(arrayAdapter)
        expenseDialogAddBt = expenseDialog.findViewById(R.id.addExpenseBTEDialog)
        expenseDialogCancelBt = expenseDialog.findViewById(R.id.cancelBTEDialog)
        val amountEditText = expenseDialog.findViewById<TextInputEditText>(R.id.expenseAmount)
        val descriptionEditText = expenseDialog.findViewById<TextInputEditText>(R.id.expenseDescription)
        var category = TransactionCategory.EDUCATION
       var type = TransactionType.EXPENSE
        expenseDialog.findViewById<RadioGroup>(R.id.radio_group).setOnCheckedChangeListener { group, i ->
            val radio: RadioButton = expenseDialog.findViewById(i);
             type = TransactionType.valueOf(radio.text.toString());
        }

        expenseDialogAddBt.setOnClickListener {
            try {
                category = TransactionCategory.valueOf(autoCompleteTV.text.toString()) }
            catch (e:Exception){

            }
            val transaction = Transaction("",amountEditText.text.toString().toInt(),type,category,descriptionEditText.text.toString(),
                LocalDate.now().toString())

            database.child("expenses").child(auth.uid.toString()).push().setValue(transaction).addOnFailureListener {
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
                expenseDialog.dismiss()
            }.addOnCompleteListener {
                Toast.makeText(this,"Expense Added",Toast.LENGTH_SHORT).show()
                amountEditText.setText("")
                descriptionEditText.setText("")
                expenseDialog.findViewById<RadioGroup>(R.id.radio_group).check(R.id.transactionTypeExpense)
                expenseDialog.dismiss()

            }
        }
        expenseDialogCancelBt.setOnClickListener {
            expenseDialog.dismiss()
        }
        binding.floatingActionButton.setOnClickListener{
onFloatClicked()
        }
        binding.addExpenseBT.setOnClickListener {
            expenseDialog.show()


        }
//        Split dialog
        splitDialog = Dialog(this)
        splitDialog.setContentView(R.layout.split_dialog)
        splitDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
//        expenseDialog.window?.setBackgroundDrawable(getDrawable(R.drawable.dialog_background))
        splitDialog.window?.decorView?.setPadding(50)
        splitDialog.setCancelable(false)
      val titleText = splitDialog.findViewById<TextInputEditText>(R.id.splitTitle);
      val addSplitBt =   splitDialog.findViewById<Button>(R.id.addSpliyBTEDialog);
      val cancelSplitBT = splitDialog.findViewById<Button>(R.id.cancelBTEDialog)
        addSplitBt.setOnClickListener {
            val contributors = ArrayList<Contributor>();
            contributors.add(Contributor(auth.uid.toString(),auth.currentUser!!.displayName!!,0.0,0.0,""));

            val split:Split = Split("",titleText.text.toString(),contributors,0,);
database.child("splits").child(auth.uid.toString()).push().setValue(split).addOnFailureListener {
    Log.e("FIREBASE",it.message.toString());
    Toast.makeText(this,"Some error occured",Toast.LENGTH_SHORT).show();
}
    .addOnCompleteListener {
        Toast.makeText(this,"Split Added Successfully",Toast.LENGTH_SHORT).show();
        splitDialog.dismiss()
        replaceFragment(SplitFragment());

    }


        }
        cancelSplitBT.setOnClickListener {
            titleText.setText("")
            splitDialog.dismiss();
        }
        binding.addSplitBT.setOnClickListener {
           splitDialog.show()
        }
        binding.addTransferBT.setOnClickListener {
            Toast.makeText(this,"Transfer",Toast.LENGTH_SHORT).show()
        }
binding.bottomNavigationView.setOnItemSelectedListener {
    when(it.itemId){
        R.id.expense->replaceFragment(ExpenseTracker())
        R.id.split->replaceFragment(SplitFragment())
        R.id.transfer->replaceFragment(TransferFragment())
        R.id.dashboard->replaceFragment(DashboardFragment())

        else->{
            replaceFragment(ExpenseTracker())
        }
    }
    true
}

    }

    override fun onResume() {
        super.onResume()
        if(auth.currentUser==null){
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun onFloatClicked(){
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked
    }

    private fun setAnimation(clicked:Boolean) {
   if(!clicked){
       binding.addTransferBT.visibility = View.VISIBLE
       binding.addSplitBT.visibility = View.VISIBLE
       binding.addExpenseBT.visibility = View.VISIBLE

   }
        else{
       binding.addTransferBT.visibility = View.INVISIBLE
       binding.addSplitBT.visibility = View.INVISIBLE
       binding.addExpenseBT.visibility = View.INVISIBLE
   }
    }

    private fun setVisibility(clicked:Boolean) {
  if(!clicked){
      binding.addTransferBT.startAnimation(fromBottom)
      binding.addSplitBT.startAnimation(fromBottom)
      binding.addExpenseBT.startAnimation(fromBottom)
      binding.floatingActionButton.startAnimation(rotateOpen)
  }
        else{
      binding.addTransferBT.startAnimation(toBottom)
      binding.addSplitBT.startAnimation(toBottom)
      binding.addExpenseBT.startAnimation(toBottom)
      binding.floatingActionButton.startAnimation(rotateClose)
  }
    }
private fun setClickable(clicked: Boolean){
    if(clicked){
        binding.addTransferBT.isClickable = false
        binding.addSplitBT.isClickable = false
        binding.addExpenseBT.isClickable = false
    }
    else{
        binding.addTransferBT.isClickable = true
        binding.addSplitBT.isClickable = true
        binding.addExpenseBT.isClickable = true
    }
}
    private fun replaceFragment(fragment: Fragment){

     supportFragmentManager.beginTransaction().apply {
//         when(fragment){
//             ExpenseTracker()->binding.bottomNavigationView.selectedItemId =R.id.expense
//             SplitFragment()->binding.bottomNavigationView.selectedItemId =R.id.split
//             TransferFragment()->binding.bottomNavigationView.selectedItemId =R.id.transfer
//             DashboardFragment()->binding.bottomNavigationView.selectedItemId =R.id.dashboard
//             else->{
//                 binding.bottomNavigationView.selectedItemId =R.id.expense
//             }
//         }

        replace(R.id.frame_layout,fragment)
        commit()
    }

}
}