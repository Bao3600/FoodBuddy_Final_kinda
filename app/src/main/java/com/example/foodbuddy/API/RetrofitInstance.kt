package com.example.foodbuddy.API

import com.example.foodbud.UpdateFoodBody
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.net.HttpCookie

interface ApiInterface {
    @Headers("Content-Type:application/json")
    @POST("api/login")
    fun signin(
            @Body info: SignInBody
    ): retrofit2.Call<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST("api/register")
    fun registerUser(
        @Body info: UserBody
    ): retrofit2.Call<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST("api/logout")
    fun logout(
            @Body info: LogoutBody
    ): retrofit2.Call<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST("api/addFood")
    fun addFood(
            @Body info: AddFoodBody
    ): retrofit2.Call<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST("api/deleteFood")
    fun deleteFood(
            @Body info: DeleteFoodBody
    ): retrofit2.Call<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST("api/editFood")
    fun editFood(
            @Body info: UpdateFoodBody
    ): retrofit2.Call<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST("api/loadFridge")
    fun loadFridge(
            @Body info: LoadFridgeBody
    ): retrofit2.Call<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST("api/getRecipes")
    fun getRecipes(
            @Body info: GetRecipesBody,
            @Query("search") item: String
    ): retrofit2.Call<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST("api/loadFeed")
    fun loadFeed(
            @Body info: LoadFeedBody
    ): retrofit2.Call<ResponseBody>

}

class RetrofitInstance {
    companion object {
        val BASE_URL: String = "https://group1largeproject.herokuapp.com/"

        val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        val client: OkHttpClient = OkHttpClient.Builder().apply {
            this.addInterceptor(interceptor)
        }.build()

        fun getRetrofitInstance(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}
