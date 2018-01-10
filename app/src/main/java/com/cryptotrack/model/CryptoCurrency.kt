package com.cryptotrack.model

/**
 * Created by Riad on 02-Dec-17.
 */
data class CryptoCurrency (val id:String, val name:String, val symbol:String, val price_usd:String,
                            val percent_change_24h:String, val percent_change_7d:String){

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as CryptoCurrency?

        if (id != that!!.id) return false
        if (name != that.name) return false
        if (symbol != that.symbol) return false
        if (price_usd != that.price_usd) return false
        return if (percent_change_24h != that.percent_change_24h) false else percent_change_7d == that.percent_change_7d

    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + symbol.hashCode()
        result = 31 * result + price_usd.hashCode()
        result = 31 * result + percent_change_24h.hashCode()
        result = 31 * result + percent_change_7d.hashCode()
        return result
    }
}