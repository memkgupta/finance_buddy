package com.example.expensemanager.dataClasses

data class SplitExpense(val id:String,val description:String,val amount:Double,val timestamp: Long,val participants: List<SplitExpenseParticipant>)
