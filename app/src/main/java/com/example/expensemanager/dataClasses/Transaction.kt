package com.example.expensemanager.dataClasses

data class Transaction(
     var id:String,
     var amount:Int,
     var type:TransactionType,
     var category: TransactionCategory,
     var description:String,
     var date: String
){
    constructor():this( id="",amount = 0,
        type = TransactionType.EXPENSE,
        category = TransactionCategory.TRAVEL,
        description = "",
        date = "")
}

enum class TransactionType{
    EXPENSE,
    INCOME
}

enum class TransactionCategory{
    GROCERIES,
    TRANSPORTATION,
    UTILITIES,
    ENTERTAINMENT,
    DINING_OUT,
    HEALTHCARE,
    EDUCATION,
    SHOPPING,
    TRAVEL,
    OTHER
}