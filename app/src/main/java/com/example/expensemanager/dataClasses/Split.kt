package com.example.expensemanager.dataClasses

data class Split(
    var id:String,
    var title:String,
    var contributors: ArrayList<Contributor>,
    var totalAmount:Int,
    var status:String = "Not Settled",
    var expenses:HashMap<String,SplitExpense> = HashMap()
)

{
    constructor():this(
        id="",title="",contributors=ArrayList<Contributor>(),totalAmount=0,status="Not Settled"
    )
}
 fun calculateAmountOwed( userId:String):Int{
     return 0;
 }
