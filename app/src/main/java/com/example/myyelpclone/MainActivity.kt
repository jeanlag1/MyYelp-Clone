package com.example.myyelpclone

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


private const val BASE_URL = "https://api.yelp.com/v3/"
private const val TAG = "MainActivity"
private const val API_key = "oijRAWa0cE-Y5hWYf7dv8eOs1RHbkO5PyjrAQAk_Sxx9HEldVemGkCygdPGJz2xTPCl0aTWAjp-VfB0WWGMGvx4cX1ymAXFqT_pbFT7wTg9SR7xN3xNquZ2D4D6rX3Yx"
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val restaurants = mutableListOf<YelpRestaurant>()
        val adapter = RestaurantsAdapter(this, restaurants)
        val searchTerm = "Avocado Toast"
        rvRestaurants.adapter = adapter
        rvRestaurants.layoutManager = LinearLayoutManager(this)

        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val yelpService = retrofit.create(YelpService::class.java)
        yelpService.searchRestaurants("Bearer $API_key", searchTerm, "New York").enqueue(object :
            Callback<YelpSearchResult> {
            override fun onResponse(
                call: Call<YelpSearchResult>,
                response: Response<YelpSearchResult>
            ) {
                Log.i(TAG, "on response $response")
                val body = response.body()
                if (body == null) {
                    return
                }
                restaurants.addAll(body.restaurants)
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<YelpSearchResult>, t: Throwable) {
                Log.i(TAG, "on failure $t")
                if (!isOnline()) {
                    Toast.makeText(this@MainActivity, "We are having connection problems", Toast.LENGTH_LONG).show()
                }

            }

        })
    }
    fun isOnline(): Boolean {
        val runtime = Runtime.getRuntime()
        try {
            val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
            val exitValue = ipProcess.waitFor()
            return exitValue == 0
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return false
    }
}