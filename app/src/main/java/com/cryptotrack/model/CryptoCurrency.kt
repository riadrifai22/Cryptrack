package com.cryptotrack.model

/**
 * Created by Riad on 02-Dec-17.
 */
data class CryptoCurrency (val id:String, val name:String, val symbol:String, val price_usd:String,
                            val percent_change_24h:String, val percent_change_7d:String){

    //TODO Check if I need to override these for SortedList
//    override fun equals(other: Any?): Boolean {
//        return super.equals(other)
//    }
//
//    override fun hashCode(): Int {
//        return super.hashCode()
//    }
}