package com.cryptotrack.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cryptotrack.R
import com.cryptotrack.model.CryptoCurrency
import kotlinx.android.synthetic.main.item_crypto_currency.view.*

/**
 * Created by Riad on 02-Dec-17.
 */
class CryptoCurrenciesAdapter(private val context: Context, private val cryptoCurrencyList: List<CryptoCurrency>):
        RecyclerView.Adapter<CryptoCurrenciesAdapter.CryptoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CryptoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_crypto_currency, parent, false)
        return CryptoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CryptoViewHolder?, position: Int) {
        holder!!.bind(cryptoCurrencyList.get(position))
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
    override fun getItemCount() = cryptoCurrencyList.size

    class CryptoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(cryptoCurrency: CryptoCurrency) {
            itemView.textView_symbol.text = cryptoCurrency.symbol
            itemView.textView_name.text = cryptoCurrency.name

            val price = cryptoCurrency.price_usd
            val finalPrice = "$price$"
            itemView.textView_price.text = finalPrice

            //24 hrs rate
            val hrs = cryptoCurrency.percent_change_24h
            if (hrs.contains('-')){
                itemView.textView_24h.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
            }
            val finalHrs = "24h: $hrs%"
            itemView.textView_24h.text = finalHrs

            //7 days rate
            val days = cryptoCurrency.percent_change_7d
            if (days.contains('-')){
                itemView.textView_7d.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
            }
            val finalDays = "7d: $days%"
            itemView.textView_7d.text = finalDays
        }
    }
}