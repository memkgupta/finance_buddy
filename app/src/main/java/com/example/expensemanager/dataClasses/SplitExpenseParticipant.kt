package com.example.expensemanager.dataClasses

data class SplitExpenseParticipant(var id:String,var contribution:Double,var contact:String){
    constructor():this(
        id="", contribution=0.0, contact=""
    )
}
