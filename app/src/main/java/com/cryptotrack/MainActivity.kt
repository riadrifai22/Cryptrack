package com.cryptotrack

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.cryptotrack.adapters.CryptoCurrenciesAdapter
import com.cryptotrack.model.CryptoCurrency
import com.squareup.moshi.Moshi
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar_main.title = resources.getString(R.string.app_name)

        val cryptoCurrenciesList = ArrayList<CryptoCurrency>()

        recyclerView_main.layoutManager = LinearLayoutManager(this)

        val adapter = CryptoCurrenciesAdapter(this, cryptoCurrenciesList)
        recyclerView_main.adapter = adapter


        var allCurrencies: Array<CryptoCurrency> ? = null

        doAsync {
            val result = URL("https://api.coinmarketcap.com/v1/ticker/").readText()
            uiThread {
                val moshi = Moshi.Builder()
                        .build()

                val jsonAdapter = moshi.adapter<Array<CryptoCurrency>>(Array<CryptoCurrency>::class.java)

                allCurrencies = jsonAdapter.fromJson(result)

                for (c in allCurrencies!!){
                    cryptoCurrenciesList.add(c)
                }

                progressBar_loadingCurrencies.visibility = View.GONE
                recyclerView_main.visibility = View.VISIBLE

                recyclerView_main.adapter.notifyDataSetChanged()

            }
        }
    }


}
