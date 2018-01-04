package com.cryptotrack

import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import com.cryptotrack.adapters.CryptoCurrenciesAdapter
import com.cryptotrack.model.CryptoCurrency
import com.squareup.moshi.Moshi
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.filter_dialog.*
import kotlinx.android.synthetic.main.filter_dialog.view.*
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
     * The comparator used to sort the list from least price to highest
     */
    private val INCREASING_PRICE: Comparator<CryptoCurrency> =
            Comparator { cryptoCurrency1, cryptoCurrency2 -> cryptoCurrency1.price_usd.toDouble().
                    compareTo(cryptoCurrency2.price_usd.toDouble()) }

    /**
     * The comparator used to sort the list from highest price to lowest. Used initially by adapter
     */
    private val DECREASING_PRICE: Comparator<CryptoCurrency> =
            Comparator { cryptoCurrency1, cryptoCurrency2 -> cryptoCurrency2.price_usd.toDouble().
                    compareTo(cryptoCurrency1.price_usd.toDouble()) }

    /**
     * The adapter for the cryptocurrency recycler view
     */
    var adapter: CryptoCurrenciesAdapter

    /**
     * The list that will contain the crypto currencies after api call.
     * Note: This list will be used by the adapter but will NOT be re-sorted, as I'm using RecyclerView's
     * SortedList in the adapter which only changes position of items in the adapter
     */
    val cryptoCurrenciesList: MutableList<CryptoCurrency>

    //The radiobutton id of which filter is currently chosen
    var selectedFilterId: Int

    init {
        adapter = CryptoCurrenciesAdapter(DECREASING_PRICE)
        //Todo: store as enum? better than ids
        selectedFilterId = R.id.radioButton_decreasing
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.item_filter){
            showFiltersDialog()
            return true
        }
        return false
    }

    fun showFiltersDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.filter_dialog, null)

        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle("Select Filter")

        (dialogView.findViewById(selectedFilterId) as RadioButton).isChecked = true

        val alertDialog = dialogBuilder.create()

        dialogView.button_selectFilter.setOnClickListener({
            selectedFilterId = dialogView.radioGroup_filters.checkedRadioButtonId
            filter(selectedFilterId)
            alertDialog.cancel()
        })

        alertDialog.show()
    }

    fun filter(id: Int?) {
        if (id == R.id.radioButton_alphabetical)
            adapter = CryptoCurrenciesAdapter(ALPHABETICAL_COMPARATOR)
        else if (id == R.id.radioButton_increasing)
            adapter = CryptoCurrenciesAdapter(INCREASING_PRICE)
        else if (id == R.id.radioButton_decreasing)
            adapter = CryptoCurrenciesAdapter(DECREASING_PRICE)

        adapter.add(cryptoCurrenciesList)
        recyclerView_main.swapAdapter(adapter, true)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText == null) return false

        val filteredList : List<CryptoCurrency> = search(cryptoCurrenciesList, newText)
        adapter.replaceAll(filteredList)
        recyclerView_main.scrollToPosition(0)
        return true
    }

    /**
     * Allow searching by CryptoCurrency's name
     */
    private fun search(models: List<CryptoCurrency>, query: String): List<CryptoCurrency>{
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
