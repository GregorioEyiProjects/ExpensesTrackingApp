
/*
// Create an instance of FirebaseFirestore
val db = FirebaseFirestore.getInstance()

// Retrieve the user ID
val userId = "user123" // Replace with the retrieved user ID

// Reference the existing collection of items for the user
val userItemsCollection = db.collection("users").document(userId).collection("existingItems")

// Create a new item document and add values to it
val item = hashMapOf(
    "amount" to amount,
    "category" to category,
    "itemImage" to image,
    "Location" to itemLocation,
    "itemDate" to itemDate
)

// Add the item to the collection
userItemsCollection.add(item)
.addOnSuccessListener { documentReference ->
    println("Item added with ID: ${documentReference.id}")
}
.addOnFailureListener { e ->
    println("Error adding item: ${e.message}")
}*/