package rakib.hasan.connect.models

import com.google.gson.annotations.SerializedName


data class User (

    @SerializedName("user_id" ) var userId  : String? = null,
    @SerializedName("name"    ) var name    : String? = null,
    @SerializedName("profile" ) var profile : String? = null,
    @SerializedName("city"    ) var city    : String? = null

)