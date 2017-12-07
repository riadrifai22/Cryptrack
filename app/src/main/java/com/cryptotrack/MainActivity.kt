package com.cryptotrack

import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.SortedList
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.cryptotrack.adapters.CryptoCurrenciesAdapter
import com.cryptotrack.model.CryptoCurrency
import com.squareup.moshi.Moshi
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

class MainActivity: AppCompatActivity(), SearchView.OnQueryTextListener {

    /**
     * The comparator used to sort the list by alphabet
     */
    private val ALPHABETICAL_COMPARATOR: Comparator<CryptoCurrency> =
            Comparator { cryptoCurrency1, cryptoCurrency2 -> cryptoCurrency1.name.compareTo(cryptoCurrency2.name) }

    /**
     * The adapter for the cryptocurrency recycler view
     */
    val adapter: CryptoCurrenciesAdapter

    /**
     * The list that will contain the crypto currencies after api call.
     * Note: This list will be used by the adapter but will NOT be re-sorted, as I'm using RecyclerView's
     * SortedList in the adapter which only changes position of items in the adapter
     */
    val cryptoCurrenciesList: MutableList<CryptoCurrency>

    init {
        adapter = CryptoCurrenciesAdapter(ALPHABETICAL_COMPARATOR)
        cryptoCurrenciesList = ArrayList<CryptoCurrency>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar_main.title = resources.getString(R.string.app_name)
        setSupportActionBar(toolbar_main)

        recyclerView_main.layoutManager = LinearLayoutManager(this)
        recyclerView_main.adapter = adapter

        doAsync {
            val result = URL("https://api.coinmarketcap.com/v1/ticker/").readText()
            uiThread {
                val moshi = Moshi.Builder()
                        .build()

                val jsonAdapter = moshi.adapter<Array<CryptoCurrency>>(Array<CryptoCurrency>::class.java)

                val allCurrencies:Array<CryptoCurrency>? = jsonAdapter.fromJson(result)

                for (c in allCurrencies!!){
                    cryptoCurrenciesList.add(c)
                }

                progressBar_loadingCurrencies.visibility = View.GONE
                recyclerView_main.visibility = View.VISIBLE

                adapter.add(cryptoCurrenciesList)

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchItem: MenuItem? = menu?.findItem(R.id.item_search)
        val searchView: SearchView = MenuItemCompat.getActionView(searchItem) as SearchView
        searchView.setOnQueryTextListener(this)

        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText == null) return false

        val filteredList : List<CryptoCurrency> = filter(cryptoCurrenciesList, newText)
        adapter.replaceAll(filteredList)
        recyclerView_main.scrollToPosition(0)
        return true
    }

    /**
     * Allow filtering by CryptoCurrency's name
     */
    private fun filter(models: List<CryptoCurrency>, query: String): List<CryptoCurrency>{
        val queryToLower:String = query.toLowerCase()

        val filteredList:MutableList<CryptoCurrency> = mutableListOf<CryptoCurrency>()

        for (cryptoCurrency in models) {
            val text:String = cryptoCurrency.name.toLowerCase()
            if (text.contains(queryToLower)) {
                filteredList.add(cryptoCurrency)
            }
        }

        return filteredList
    }
}
