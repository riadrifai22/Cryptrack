package com.cryptotrack.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.util.SortedList
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.util.SortedListAdapterCallback
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cryptotrack.R
import com.cryptotrack.model.CryptoCurrency
import kotlinx.android.synthetic.main.item_crypto_currency.view.*

/**
 * Created by Riad on 02-Dec-17.
 */
class CryptoCurrenciesAdapter(private val mComparator: Comparator<CryptoCurrency>):
        RecyclerView.Adapter<CryptoCurrenciesAdapter.CryptoViewHolder>() {

    private val currencySortedList: SortedList<CryptoCurrency>
    init {
        currencySortedList = SortedList(CryptoCurrency::class.java, object : SortedListAdapterCallback<CryptoCurrency>(this){

            override fun areContentsTheSame(oldItem: CryptoCurrency?, newItem: CryptoCurrency?) = oldItem == newItem

            override fun compare(o1: CryptoCurrency?, o2: CryptoCurrency?) = mComparator.compare(o1, o2)

            override fun areItemsTheSame(item1: CryptoCurrency?, item2: CryptoCurrency?) = item1?.id == item2?.id
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CryptoViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_crypto_currency, parent, false)
        return CryptoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CryptoViewHolder?, position: Int) {
        holder?.bind(currencySortedList[position])
    }

    fun add(toBeAddedList: List<CryptoCurrency>) {
        currencySortedList.addAll(toBeAddedList)
    }

    fun replaceAll(newList: List<CryptoCurrency>) {
        currencySortedList.beginBatchedUpdates()
        for (i in currencySortedList.size()-1 downTo 0) {
            val model: CryptoCurrency = currencySortedList[i]
            if (!newList.contains(model)) {
                currencySortedList.remove(model)
            }
        }
        currencySortedList.addAll(newList)
        currencySortedList.endBatchedUpdates()
    }

    override fun getItemViewType(position: Int) = position

    override fun getItemCount() = currencySortedList.size()

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