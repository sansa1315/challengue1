package com.alkemy.challengue
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.alkemy.challengue.databinding.ActivityDetalleBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.lang.StringBuilder

class DetalleActivity : AppCompatActivity() {
    val Base_url = "https://api.themoviedb.org/3/"
    val api_key = "aa3f6f0aa999cf507167f479a1f98cd0"
    val google_key = "AIzaSyCSvQEEf4l6W5mldrVQORNgUQxs6wdHXrI"
    lateinit var detalleAdapter: DetalleAdapter
//    lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var movie_id : String
    private lateinit var titulo : String

    private lateinit var binding: ActivityDetalleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val items: List<Cast> = listOf()
        detalleAdapter = DetalleAdapter(baseContext, items)
        binding.rvDetalle.setHasFixedSize(true)

        binding.rvDetalle.adapter = detalleAdapter

        movie_id = intent.getStringExtra("movie_id").toString()
        titulo = intent.getStringExtra("title").toString()
        getMovieDetail()
        binding.tvDetalleTitulo.text = titulo

        binding.rateBar.setOnRatingChangeListener { ratingBar, rating ->
            Toast.makeText(applicationContext, "rate: "+rating, Toast.LENGTH_SHORT).show()
        }
    }

private fun getMovieDetail() {

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

        val retrofitData = retrofitBuilder.getMovieDetail(
            movie_id,
            api_key,
            "videos",
            "es-MX"
        )

        retrofitData.enqueue(object : Callback<MovieDetail> {
            override fun onResponse(call: Call<MovieDetail>, response: Response<MovieDetail>?) {
                if (response?.isSuccessful!!) {
                    val responseBody = response?.body()!!
                        responseBody.homepage
                    if(responseBody.videos.results.isNotEmpty()) {
                        binding.youtubePlayerView.addYouTubePlayerListener(object :
                            AbstractYouTubePlayerListener() {
                            override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                                youTubePlayer.cueVideo(responseBody.videos.results[0].key, 0f)
                            }
                        })
                    }else{
                        val imageBasePath = "https://image.tmdb.org/t/p/w300/"
                        binding.youtubePlayerView.isGone = true
                        binding.imageDetalleExtra.isGone = false
                        Picasso.get().load(imageBasePath + responseBody.backdrop_path).into(binding.imageDetalleExtra)
                    }

                    var genero = StringBuilder()
                    for(g in responseBody.genres){
                        genero.append(g.name+", ")
                    }
                    binding.tvDetalleGenero.text = "Genero: "+ genero
                    binding.tvDetalleTitulo.text = responseBody.title
                    binding.tvDetalleEstreno.text = "Fecha de estreno: "+responseBody.release_date
                    binding.tvDetalleIdioma.text = "Idioma Original: "+responseBody.original_language
                    binding.tvDetallePopularidad.text = "Popularidad: "+responseBody.popularity
                    val retrofitDataCredits = retrofitBuilder.getMovieCredit(movie_id, api_key)

                    retrofitDataCredits.enqueue(object:Callback<MovieCredits>{
                        override fun onResponse(
                            call: Call<MovieCredits>,
                            response: Response<MovieCredits>
                        ) {
                            val responseBodyCredits = response.body()!!
                            detalleAdapter = DetalleAdapter(baseContext, responseBodyCredits.cast)
//                            binding.tvDetalle.text = responseBodyCredits.cast[0].name
                            detalleAdapter.notifyDataSetChanged()
                            binding.rvDetalle.adapter = detalleAdapter
                        }

                        override fun onFailure(call: Call<MovieCredits>, t: Throwable) {
                            Toast.makeText(applicationContext, "onFailure: "+t.message, Toast.LENGTH_SHORT).show()
                        }
                    })
                }else{
                    Toast.makeText(applicationContext, "response en failure", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<MovieDetail>?, t: Throwable) {
                Toast.makeText(applicationContext, "Failure: "+ t.message, Toast.LENGTH_SHORT).show()
                Log.d("Main Activity", "onFailure: " + t.message)
            }
        })
    }

    fun postRate(view : View) {
        if (binding.rateBar.rating > 0.4) {
            var logg = HttpLoggingInterceptor()
            logg.setLevel(HttpLoggingInterceptor.Level.BODY)
            var okHttpClient = OkHttpClient.Builder()
                .addInterceptor(logg)
                .build()

            val retrofitBuilder = Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Base_url)
                .client(okHttpClient)
                .build()
                .create(ApiInterface::class.java)

            val retrofitData = retrofitBuilder.postRate(
                movie_id = movie_id, api_key = api_key, value = binding.rateBar.rating.toString()
            )

            retrofitData.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>?) {
                    if (response?.isSuccessful!!) {
                        val responseBody = response?.body()!!
                        Toast.makeText(
                            applicationContext,
                            "" + responseBody,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }

                override fun onFailure(call: Call<String>?, t: Throwable) {
                    Toast.makeText(
                        applicationContext,
                        "Failure: " + t.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    Log.d("Main Activity", "onFailure: " + t.message)
                }
            })

            Toast.makeText(
                applicationContext,
                "RATE: " + binding.rateBar.rating,
                Toast.LENGTH_SHORT
            ).show()
        }else{
            Toast.makeText(
                applicationContext,
                "El rating debe ser al menos 0.5",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}