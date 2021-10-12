package com.alkemy.challengue

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

import retrofit2.*

import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    val api_key = "aa3f6f0aa999cf507167f479a1f98cd0"
    val Base_url = "https://api.themoviedb.org/3/"
    val Base_url2 = "https://jsonplaceholder.typicode.com/"
    val languague = "es-MX"
    lateinit var myAdapter: MyAdapter

    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getSupportActionBar()?.hide()
//        val items: List<Result> = ArrayList()
        val items: List<Result> = listOf()
//
        myAdapter = MyAdapter(baseContext, items)
        recyclerView = findViewById(R.id.rv_popularMovies)
        recyclerView.setHasFixedSize(true)

        recyclerView.adapter = myAdapter

        mostPopular()
    }

    private fun mostPopular() {

        var logg = HttpLoggingInterceptor()
        logg.setLevel(HttpLoggingInterceptor.Level.BODY)
        var okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logg)
            .build()
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Base_url)
            .client(okHttpClient)
            .build()
            .create(ApiInterface::class.java)

        val retrofitData = retrofitBuilder.getMostPopular(api_key, languague)
        retrofitData.enqueue(object : Callback<MyData> {
            override fun onResponse(call: Call<MyData>, response: Response<MyData>?) {
                if (response?.isSuccessful!!) {
                    val responseBody = response?.body()!!
                    myAdapter = MyAdapter(baseContext, responseBody.results)
                    myAdapter.notifyDataSetChanged()
                    recyclerView.adapter = myAdapter

                }else{
                    Toast.makeText(applicationContext, "response en failure", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<MyData>?, t: Throwable) {
                Toast.makeText(getApplicationContext(), "Failure: "+ t.message, Toast.LENGTH_SHORT).show()
                d("Main Activity", "onFailure: " + t.message)
            }
        })
    }


}