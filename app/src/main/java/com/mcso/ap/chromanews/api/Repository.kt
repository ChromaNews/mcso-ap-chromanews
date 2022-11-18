package com.mcso.ap.chromanews.api

import com.mcso.ap.chromanews.R

data class RecyclerData(
    var title: String,
    var imgid: Int
)

class Repository {
    companion object {
        private var initialDataList = listOf (
            RecyclerData("Business", R.drawable.business_image),
            RecyclerData("Environment", R.drawable.env_image),
            RecyclerData("Entertainment", R.drawable.entertainment_image),
            RecyclerData("Food", R.drawable.food_news_image),
            RecyclerData("Health", R.drawable.health_news_image),
            RecyclerData("Politics", R.drawable.political_news_image),
            RecyclerData("Science", R.drawable.science_news_image),
            RecyclerData("Sports", R.drawable.sports_imagee),
            RecyclerData("Technology", R.drawable.tech_news_image),
            RecyclerData("Top", R.drawable.topnewsimage),
           // RecyclerData("World", R.drawable.world_news_image),
        )

        fun fetchData(): List<RecyclerData> {
            return initialDataList
        }
    }
}