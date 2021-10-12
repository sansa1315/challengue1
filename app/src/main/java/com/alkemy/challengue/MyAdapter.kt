package com.alkemy.challengue

import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.content.Intent
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso


class MyAdapter(val context: Context, val movieList: List<Result>):RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var rate: TextView = itemView.findViewById(R.id.rate)
        var title: TextView = itemView.findViewById(R.id.movie_name)
        var imagen : ImageView = itemView.findViewById(R.id.image_moviePopular)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var itemView = LayoutInflater.from(context).inflate(R.layout.row_items, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageBasePath = "https://image.tmdb.org/t/p/w300"
        holder.rate.text = movieList[position].vote_average.toString()
        holder.title.text = movieList[position].title

        Picasso.get().load(imageBasePath + movieList[position].poster_path).into(holder.imagen)

        holder.itemView.setOnClickListener {
            var intent = Intent(holder.itemView.context, DetalleActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("movie_id", movieList[position].id.toString())
            intent.putExtra("title", movieList[position].title)
            holder.itemView.context.startActivity(intent)
//            Toast.makeText(holder.itemView.context, holder.movieId.text, Toast.LENGTH_SHORT).show()

        }
    }

    override fun getItemCount(): Int {
       return movieList.size
    }
}