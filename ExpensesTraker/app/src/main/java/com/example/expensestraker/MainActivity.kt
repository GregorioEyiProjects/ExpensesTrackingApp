package com.example.expensestraker

import android.content.ContentValues.TAG
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensestraker.adapter.ItemAdapter
import com.example.expensestraker.data.Item
import com.example.expensestraker.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var inflater: LayoutInflater

    //pup up menu
    private lateinit var popUpView: View
    private lateinit var popUpViewInput: View
    private lateinit var rlMenuPopup: RelativeLayout
    private lateinit var btnIncome: Button
    private lateinit var btnExpense: Button

    // pop up input
    private lateinit var etAmount: TextInputEditText
    private lateinit var tvMenuOptions: AutoCompleteTextView
    private lateinit var bntAddQuantity: MaterialButton
    private lateinit var tilDropDownMenu: TextInputLayout

    //RV adapter
    private lateinit var itemAdapter: ItemAdapter

    //List for the dropdown list
    private lateinit var listOfOptions: MutableList<String>

    //List for the coming data
    private var listOfItems = mutableListOf<Item>()

    //List for the sum up items
    private var sumUpListOfItems = mutableListOf<Item>()

    //Reversed list
    private var reversedList = mutableListOf<Item>()

    //Adapter for the dropdown
    private lateinit var dropdownAdapter: ArrayAdapter<String>

    //Firebase database
    //private lateinit var db : FirebaseFirestore.getInstance();
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_main)
        Toast.makeText(this, "Loading!", Toast.LENGTH_LONG).show()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initListeners()

        // list to show in the dropdown
        listOfOptions =
            mutableListOf("Oishi", "Vending machine", "Food", "Transportation", "Big C", "Barbershop", "Tha tea", "Entrance", "Hospital", "Beer",  "Other")
        dropdownAdapter = ArrayAdapter(this, R.layout.dropdown_item, listOfOptions)

        //firebase
        db = FirebaseFirestore.getInstance()

        //Getting the data
        retrieveData()

    }

    private fun retrieveData() {

        db.collection("Items")
            .get()
            .addOnSuccessListener { result ->
                // init the empty list
                listOfItems = mutableListOf()

                //var nextId = 1

                for (document in result) {
                    println(result.toString())
                    val amount = document.data["amount"] as String
                    val category = document.data["category"] as String
                    val imageItem = document.data["itemImage"] as String

                    val newItem = Item(document.id, Integer.parseInt(amount), category, imageItem)
                    listOfItems.add(newItem)
                   // nextId++
                }

                // Recyclerview adapter
                initRecyclerview()

                //Setting the current price
                setCurrentPrice()

            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    private fun setCurrentPrice() {
        var totalAmount: Int = 0
        for (item in listOfItems) {
            totalAmount += item.amount
        }
        //addTotalAmountToFirebase(totalAmount)
        binding.tvTotalExpensePrice.text = totalAmount.toString()
    }

    private fun addTotalAmountToFirebase(totalAmount: Int) { // I think I don't need this one
        val totalExpensePrice = hashMapOf(
            "totalExpense" to totalAmount
        )
        db.collection("totalExpense")
            .add(totalExpensePrice)
            .addOnSuccessListener { item ->
                Log.w(
                    TAG,
                    "Success on adding the total expense $item"
                )
            }
            .addOnFailureListener { error -> Log.w(TAG, "Error adding totalExpense", error) }
    }

    private fun initRecyclerview() {

        // Reverse the list
        //reversedList = listOfItems.reversed().toMutableList()

        // Unique List
        val uniqueList = uniqueListOfItems(listOfItems)

        reversedList = uniqueList

        //Initializing the adapter
        itemAdapter = ItemAdapter(uniqueList, onClickDelete = { position -> onDeleteItem(position) })

        // initializing the recyclerView created
        val recyclerView: RecyclerView = binding.rcTransactionHistory

        //Setting the recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = itemAdapter

        // Notify adapter that data has changed
        itemAdapter.notifyDataSetChanged()
    }

    private fun uniqueListOfItems(listOfItems: MutableList<Item>): MutableList<Item> {
        val uniqueItems = mutableListOf<Item>()
        val groupedItems = mutableMapOf<String, Item>() // Map category to Item (initial total)
        for (item in listOfItems) {
            val category = item.category
            if (groupedItems.containsKey(category)) {
                val existingItem = groupedItems[category]!! // Category exists, update existing item with new amount
                //existingItem.names.add(item.toString())
                existingItem.amount += item.amount
            } else {
                // New category, create a new Item
                //groupedItems[category] = Item(category, item.amount, "") // Adjust other fields as needed
                groupedItems[category] = item.copy()
            }
        }
        uniqueItems.addAll(groupedItems.values)
        return uniqueItems
    }

    private fun onDeleteItem(position: String) {

        val item = reversedList[Integer.parseInt(position)] // Get the Item object
        val documentId = item.id // Access the document ID
        deleteItemFromFirebase(documentId)

        //changing the total expense value
        val currentExpense:Int = Integer.parseInt(binding.tvTotalExpensePrice.text.toString())
        val newCurrentExpense =   currentExpense - item.amount
        binding.tvTotalExpensePrice.text = newCurrentExpense.toString()

        //deleting the value
        reversedList.removeAt(Integer.parseInt(position))

        //Notifying the adapter
        itemAdapter.notifyItemRemoved(Integer.parseInt(position))
    }

    private fun deleteItemFromFirebase(documentId: String) {

       db.collection("Items")
           .document(documentId)
           .delete()
           .addOnSuccessListener { Log.d("TAG", "Success on delete!") }
           .addOnFailureListener { errorMessage-> Log.d("TAG", "Failure on delete! $errorMessage") }
    }

    private fun initListeners() {
        binding.myButton.setOnClickListener {
//           Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show()
            createPopUpMenu()
            binding.clMainActivity.setBackgroundColor(getColor(R.color.gray))
        }
        binding.tvSeeAll.setOnClickListener { Toast.makeText(this, "Not working yet!", Toast.LENGTH_SHORT).show() }
    }

    private fun createPopUpMenu() {
        inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater // this is how the cast in kotlin
        popUpView = inflater.inflate(R.layout.pop_up_menu, null)

        val with: Int = ViewGroup.LayoutParams.MATCH_PARENT
        val height: Int = ViewGroup.LayoutParams.WRAP_CONTENT
        val focusable = true;

        rlMenuPopup = popUpView.findViewById(R.id.rlMenuPopup)
        btnIncome = popUpView.findViewById(R.id.btnIncome)
        btnExpense = popUpView.findViewById(R.id.btnExpense)

        val popUpWindow = PopupWindow(popUpView, with, height, focusable)
        popUpWindow.showAtLocation(
            binding.root,
            Gravity.BOTTOM,
            0,
            0
        ) // binding.root because we have to attach the popup window to the activity's root view

        popUpWindow.setOnDismissListener {
            popUpWindow.dismiss()
            binding.clMainActivity.setBackgroundColor(getColor(R.color.white))

        }

        btnIncome.setOnClickListener {
            popUpWindow.dismiss()
            //binding.clMainActivity.setBackgroundColor(getColor(R.color.white))
            createPopUpMenuInput()
        }

        btnExpense.setOnClickListener {
            // Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show()
            popUpWindow.dismiss()
            //binding.clMainActivity.setBackgroundColor(getColor(R.color.white))
            createPopUpMenuInput()
        }

    }

    private fun createPopUpMenuInput() {

        //setting the background
        binding.clMainActivity.setBackgroundColor(getColor(R.color.gray))

        // Bring the view
        inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        popUpViewInput = inflater.inflate(R.layout.pop_up_input, null)

        //variables for the pop up when is created
        val with: Int = ViewGroup.LayoutParams.MATCH_PARENT
        val height: Int = ViewGroup.LayoutParams.WRAP_CONTENT
        val focusable = true;

        //getting the component of the view brought
        etAmount = popUpViewInput.findViewById(R.id.etAmount)
        tvMenuOptions = popUpViewInput.findViewById(R.id.tvMenuOptions)
        bntAddQuantity = popUpViewInput.findViewById(R.id.bntAddQuantity)
        tilDropDownMenu = popUpViewInput.findViewById(R.id.tilDropDownMenu)

        //Setting the dropdown
        tvMenuOptions.setTextColor(resources.getColor(R.color.black)) //Setting the text color of the auto complete
        tilDropDownMenu.setStartIconTintList(ColorStateList.valueOf(Color.BLACK)) // setting the icon color

        //Setting the dropdown
        tvMenuOptions.setAdapter(dropdownAdapter)

        //Creating the pop up
        val popupWindow = PopupWindow(popUpViewInput, with, height, focusable)

        //Displaying the created pop up
        popupWindow.showAtLocation(binding.root, Gravity.CENTER, 0, 0)

        //to disable the pop up menu
        popupWindow.setOnDismissListener {
            popupWindow.dismiss()
            binding.clMainActivity.setBackgroundColor(getColor(R.color.white))
        }

        bntAddQuantity.setOnClickListener {

            val amount = etAmount.text.toString()
            val category = tvMenuOptions.text.toString()
            val image = getImage(category)

            val item = hashMapOf(
                "amount" to amount,
                "category" to category,
                "itemImage" to image
            )

            db.collection("Items")
                .add(item)
                .addOnSuccessListener { documentReference ->
                    Log.d(
                        TAG,
                        "DocumentSnapshot added with ID: ${documentReference.id}"
                    )
                }
                .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
            popupWindow.dismiss()
            binding.clMainActivity.setBackgroundColor(getColor(R.color.white))
            retrieveData()
        }


    }

    private fun getImage(imageItem: String):String {
        var imageItemValue:String = ""
        imageItemValue = when(imageItem){
            "Oishi" -> {
                "https://i.pinimg.com/564x/eb/22/14/eb2214c7b3ca1d73351aec2b59c0cf9b.jpg"
            }
            "Vending machine" -> {
                "https://i.pinimg.com/564x/2a/de/98/2ade98f922ea34ccf9cf10b8840f8537.jpg"
            }
            "Food" -> {
                "https://i.pinimg.com/564x/31/3c/91/313c91d393779eaaa7f39a5e00eb7811.jpg"
            }
            "Transportation" -> {
                "https://i.pinimg.com/564x/04/6b/38/046b3884bbd9d16ba053a80c95b8f295.jpg"
            }
            "Big C" -> {
                "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e3/BigCLogo2022.svg/1200px-BigCLogo2022.svg.png"
            }
            "Barbershop" -> {
                "https://i.pinimg.com/564x/b2/cd/6d/b2cd6d756f17bcfb2a414e188f072e54.jpg"
            }
            "Tha tea" ->{
                "https://i.pinimg.com/564x/52/ed/ac/52edac68c6e30554a5faef16fd3a3aa9.jpg"
            }
            "Entrance" ->{
                "https://i.pinimg.com/564x/41/59/77/415977fa0031e50a4d56ef456ca88a9b.jpg"
            }
            "Beer" -> {
                "https://i.pinimg.com/564x/6a/1b/0f/6a1b0f3ad643b5d9a8cc65d7b08c3bf2.jpg"
            }
            "Hospital" ->{
                "https://i.pinimg.com/564x/27/8a/99/278a99ac7ece24bfcc4e00b469270a79.jpg"
            }
            else -> {
                "https://i.pinimg.com/564x/5d/50/dc/5d50dcb5416a1abdcc6d09dc118504e0.jpg"
            }
        }
        return imageItemValue
    }

}