package com.wallpaper.hdnature.data.model.category

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryModel(
    val image: String,
    val title: String,
    val collectionId: String = ""
) : Parcelable {
    companion object {

        fun getPagerCategories(): List<CategoryModel> {
            return listOf(
                CategoryModel(
                    "https://images.unsplash.com/photo-1667236414031-987b80517338?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxjb2xsZWN0aW9uLXBhZ2V8MTR8X2JWYlA3WmhYNzR8fGVufDB8fHx8&auto=format&fit=crop&w=600&q=60",
                    "Waterfront",
                    "_bVbP7ZhX74"
                ),
                CategoryModel(
                    "https://images.unsplash.com/photo-1643892648551-241d371de669?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxjb2xsZWN0aW9uLXBhZ2V8MXxHcXF1Y2dDMENEY3x8ZW58MHx8fHw%3D&auto=format&fit=crop&w=600&q=60",
                    "Aerial View",
                    "GqqucgC0CDc"
                ),
                CategoryModel(
                    "https://images.unsplash.com/photo-1508881669417-574b54552d73?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxjb2xsZWN0aW9uLXBhZ2V8MjR8M1cxNjhYT2J2Qll8fGVufDB8fHx8&auto=format&fit=crop&w=600&q=60",
                    "Rain",
                    "3W168XObvBY"
                ),
                CategoryModel(
                    "https://images.unsplash.com/photo-1559816756-b1937c5a1127?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxjb2xsZWN0aW9uLXBhZ2V8MzB8RFBjcDFxdVpvY1V8fGVufDB8fHx8&auto=format&fit=crop&w=600&q=60",
                    "Leaves",
                    "DPcp1quZocU"
                ),
                CategoryModel(
                    "https://images.unsplash.com/photo-1558521958-0a228e77e984?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxjb2xsZWN0aW9uLXBhZ2V8MjB8dTFVMGMtR3Y0VTR8fGVufDB8fHx8&auto=format&fit=crop&w=600&q=60",
                    "Flowers",
                    "h8sm0cgbatc"
                ),
                CategoryModel(
                    "https://images.unsplash.com/photo-1651938978984-1893dc1db106?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxjb2xsZWN0aW9uLXBhZ2V8M3xIOGROb202X0M3SXx8ZW58MHx8fHw%3D&auto=format&fit=crop&w=600&q=60",
                    "Mountains",
                    "H8dNom6_C7I"
                ),
            )
        }

        fun getOtherCategories(): List<CategoryModel> {
            return listOf(
                CategoryModel(
                    "https://images.unsplash.com/photo-1646987628115-97798a910065?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxjb2xsZWN0aW9uLXBhZ2V8MnxzMWE2Y3h6ZHowWXx8ZW58MHx8fHw%3D&auto=format&fit=crop&w=600&q=60",
                    "Winter session",
                    "s1a6cxzdz0Y"
                ),
                CategoryModel(
                    "https://images.unsplash.com/photo-1670540120738-71bc11f57f38?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxjb2xsZWN0aW9uLXBhZ2V8MXwyZ25UNDVoUHgyd3x8ZW58MHx8fHw%3D&auto=format&fit=crop&w=600&q=60",
                    "Autumn session",
                    "2gnT45hPx2w"
                ),
                CategoryModel(
                    "https://images.unsplash.com/uploads/1412748786298aacc1dc7/f2e5b5da?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxjb2xsZWN0aW9uLXBhZ2V8MTJ8S0NkUnJVMGo3Z3N8fGVufDB8fHx8&auto=format&fit=crop&w=600&q=60",
                    "Spring session",
                    "KCdRrU0j7gs"
                ),
                CategoryModel(
                    "https://images.unsplash.com/photo-1552589252-70f32f048b36?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxjb2xsZWN0aW9uLXBhZ2V8MnxDcW1ZOFg2cDkzNHx8ZW58MHx8fHw%3D&auto=format&fit=crop&w=600&q=60",
                    "Summer",
                    "CqmY8X6p934"
                ),
                CategoryModel(
                    "https://images.unsplash.com/photo-1559682895-7b9e9f439fb5?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxjb2xsZWN0aW9uLXBhZ2V8MjF8Y08wYWNsTldHOWN8fGVufDB8fHx8&auto=format&fit=crop&w=600&q=60",
                    "Cool background",
                    "cO0aclNWG9c"
                ),
                CategoryModel(
                    "https://images.unsplash.com/photo-1562690879-edcd9a8fb60a?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxjb2xsZWN0aW9uLXBhZ2V8MjV8TUVCMmtPN1R1X2t8fGVufDB8fHx8&auto=format&fit=crop&w=600&q=60",
                    "Rose",
                    "MEB2kO7Tu_k"
                ),
                CategoryModel(
                    "https://images.unsplash.com/photo-1554493767-1fa592e96e26?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxjb2xsZWN0aW9uLXBhZ2V8MzJ8d0Rld0x3QndmeDR8fGVufDB8fHx8&auto=format&fit=crop&w=600&q=60",
                    "Trees",
                    "wDewLwBwfx4"
                ),
                CategoryModel(
                    "https://images.unsplash.com/photo-1563372949-0483fa40e5da?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8ZHJvcHxlbnwwfHwwfHw%3D&auto=format&fit=crop&w=600&q=60",
                    "Drop",
                    "kkXKDpAHj4Y"
                ),
            )
        }
    }
}