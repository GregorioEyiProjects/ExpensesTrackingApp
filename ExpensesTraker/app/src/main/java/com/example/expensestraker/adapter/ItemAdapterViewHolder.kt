package com.example.expensestraker.adapter

import android.content.ContentValues
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.expensestraker.data.Item
import com.example.expensestraker.databinding.ItemLayoutBinding
import com.google.firebase.firestore.DocumentSnapshot

class ItemAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemLayoutBinding.bind(view)

    fun render(itemList: Item, onClickDelete: (String) -> Unit) {

        Log.d(ContentValues.TAG, "IS IT REACHING HERE => ${itemList.category}")
        Glide.with(binding.ivItemLogo.context).load(itemList.itemImage).into(binding.ivItemLogo)
        binding.tvItemCategory.text = itemList.category
        binding.tvItemAmount.text = itemList.amount.toString()
        binding.ivDeleteItem.setOnClickListener { onClickDelete(adapterPosition.toString()) } // adapterPosition is embedded variable

    }

}