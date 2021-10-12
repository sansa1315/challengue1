package com.alkemy.challengue

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class DetalleAdapter(val context: Context, val castList: List<Cast>): RecyclerView.Adapter<DetalleAdapter.ViewHolder>() {
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var imagen : ImageView = itemView.findViewById(R.id.image_movieDetalle)
        var actorName : TextView = itemView.findViewById(R.id.actor_name)
        var charName : TextView = itemView.findViewById(R.id.char_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var itemView = LayoutInflater.from(context).inflate(R.layout.row_detalle, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageBasePath = "https://image.tmdb.org/t/p/w300/"
        holder.actorName.text = castList[position].name
        holder.charName.text = castList[position].character
//        if (castList[position].profile_path.isNotEmpty()){
            Picasso.get().load(imageBasePath + castList[position].profile_path).into(holder.imagen)
//        }else{
//            holder.imagen.setImageResource(R.drawable.ic_launcher_background)
//        }

//        holder.itemView.setOnClickListener {
//            var intent = Intent(holder.itemView.context, DetalleActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            intent.putExtra("movie_id", movieList[position].id.toString())
//            holder.itemView.context.startActivity(intent)
////            Toast.makeText(holder.itemView.context, holder.movieId.text, Toast.LENGTH_SHORT).show()

//        }
    }

    override fun getItemCount(): Int {
        return castList.size
    }
}