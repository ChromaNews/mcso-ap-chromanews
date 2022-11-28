package com.mcso.ap.chromanews.model.savedNews

import com.google.firebase.firestore.DocumentId

data class NewsMetaData(
    var newsID : String = "",
    var title: String = "",
    var description: String = "",
    var imageURL: String = "",
    var link: String = "",
    var pubDate: String = "",
    @DocumentId var firestoreID: String = ""
)