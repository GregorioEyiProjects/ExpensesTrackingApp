package com.example.expensestraker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expensestraker.R
import com.example.expensestraker.data.Item

class ItemAdapter(
    private val itemList: List<Item>,
    private val onClickDelete: (String) -> Unit
) :
    RecyclerView.Adapter<ItemAdapterViewHolder>() {

    override fun getItemCount(): Int = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapterViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ItemAdapterViewHolder(layoutInflater.inflate(R.layout.item_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ItemAdapterViewHolder, position: Int) {
        val item = itemList[position]
        holder.render(item, onClickDelete)
    }

    private fun fillUpTheList(item: Item, listOfItems: List<Item>) {
        var oishi:Int = 0
        var vendingMachine:Int = 0
        var food:Int = 0
        var transportation:Int = 0
        var bigC:Int = 0
        var others:Int = 0
        var nextId = 1
        for (item in listOfItems){
            when (item.category) {
                "Oishi" -> oishi += item.amount
                "Vending machine" -> vendingMachine += item.amount
                "Food" -> food += item.amount
                "Transportation" -> transportation += item.amount
                "Big C" -> bigC += item.amount
                else -> others += item.amount // Covers remaining cases
            }
        }
    }


}