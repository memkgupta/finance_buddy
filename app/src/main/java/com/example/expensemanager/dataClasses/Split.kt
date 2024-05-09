package com.example.expensemanager.dataClasses

data class Split(
    var id:String,
    var title:String,
    var contributors: ArrayList<String>,
    var totalAmount:Int,
    var status:String = "Not Settled")
{
    constructor():this(
        id="",title="",contributors=ArrayList<String>(),totalAmount=0,status="Not Settled"
    )
}
 fun calculateAmountOwed( userId:String):Int{
     return 0;
 }
