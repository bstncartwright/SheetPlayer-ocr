package com.munkurious.sheetplayer

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by Boston on 10/23/2017.
 */
class RecyclerViewAdapter(val list:ArrayList<MyData>, val activity:MainActivity):RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.ViewHolder {
		val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
		return ViewHolder(v, activity)
	}

	//this method is binding the data on the list
	override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolder, position: Int) {
		holder.bindItems(list[position])
	}

	//this method is giving the size of the list
	override fun getItemCount(): Int {
		return list.size
	}

	class ViewHolder(view: View, val activity: MainActivity): RecyclerView.ViewHolder(view) {
		fun bindItems(data : MyData){
			val _textView:TextView = itemView.findViewById(R.id.textview)
			val _imageView:ImageView = itemView.findViewById(R.id.imageview)
			_textView.text = data.text
			_imageView.setImageBitmap(data.image)

			//set the onclick listener for the singlt list item
			itemView.setOnClickListener({
				Log.e("ItemClicked", data.text)
				activity.playSong(data.text)
			})
		}

	}
}

