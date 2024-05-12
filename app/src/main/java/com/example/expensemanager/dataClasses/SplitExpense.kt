package com.example.expensemanager.dataClasses

data class SplitExpense(var id:String,var description:String,var amount:Double,var timestamp: Long,var participants: ArrayList<SplitExpenseParticipant>){
    constructor():this(
        id="",description="", amount=0.0, timestamp= 0L, participants= ArrayList()
    )
}
