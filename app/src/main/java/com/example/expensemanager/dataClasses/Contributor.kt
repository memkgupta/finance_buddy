package com.example.expensemanager.dataClasses

data class Contributor(var id:String,var name:String,var totalOut:Double,var totalIn:Double,var contact:String?){
    constructor():this(
        id="",name="", totalOut=0.0, totalIn=0.0, contact=""
    )
    }

