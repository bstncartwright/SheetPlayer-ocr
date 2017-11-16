package com.munkurious.sheetplayer

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.koushikdutta.ion.Ion
import com.munkurious.sheetplayer.FilePathFromUri.getPath
import com.scanlibrary.ScanActivity
import com.scanlibrary.ScanConstants
import kotlinx.android.synthetic.main.activity_main.*
import nl.changer.audiowife.AudioWife
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity()
{

	val items = ArrayList<MyData>()
	private var adapter : RecyclerViewAdapter by Delegates.notNull<RecyclerViewAdapter>()
	private var refreshLayout : SwipeRefreshLayout by Delegates.notNull<SwipeRefreshLayout>()
	//private var mediaPlayerView : MediaPlayerView by Delegates.notNull<MediaPlayerView>()



	private var MY_PERMISSIONS_REQUEST_STORAGE: Int = 0

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		setSupportActionBar(toolbar)

		fab.setOnClickListener { view ->
			//Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
			//		.setAction("Action", null).show()
			openCameraScanner()
		}


		val recyclerView: RecyclerView = findViewById(R.id.recycler)
		recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

		refreshLayout = findViewById(R.id.swipeRefreshLayout)

		refreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
				refreshData()
		})

		adapter = RecyclerViewAdapter(items, this@MainActivity)
		recyclerView.adapter = adapter

		refreshData()

		if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {
			requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
					MY_PERMISSIONS_REQUEST_STORAGE)
		}

	}

	fun refreshData()
	{
		Ion.with(this@MainActivity)
				.load("http://sheettomidi.eastus.cloudapp.azure.com:8080/outs")
				.asString()
				.setCallback { e, result ->
					// do stuff with the result or error
					//Log.v("HERERE", result.toString())
					val list = convertToArray(result)
					//Log.v("HEREE", list.toString())
					finishDataRefresh(list)
				}
	}

	fun playSong(name: String)
	{
		Log.v("PLAYSONG", "attempting to play song" + name)
		//val thread = Thread({
		//	val conn = URL("http://sheettomidi.eastus.cloudapp.azure.com:8080/outs/$name.mid").openConnection()
		//	val input = conn.getInputStream()
		//	val output = this.openFileOutput(name + ".mid", android.content.Context.MODE_PRIVATE)
		//	val buffer = ByteArray(4096)
		//	while (true) {
		//		val len = input.read(buffer)
		//		if (len <= 0) {
		//			break
		//		}
		//		output.write(buffer, 0, len)
		//		output.flush()
		//	}
		//	output.close()
		//	input.close()
//
		//	this.filesDir.listFiles().forEach {
		//		if (it.name.contains(name)) {
		//			Log.v("PLAYSONG", "ATTEMPTING TO PLAY" + it.absolutePath)
		//			mediaPlayerView.releasePlayer()
		//			mediaPlayerView.setupPlayer(it.absolutePath)
		//		}
		//	}
		//})
		//thread.start()



		val fo = File(Environment.getExternalStorageDirectory().path + "/" + name + ".mid")
		//Log.v("DOWNLOADING", "http://sheettomidi.eastus.cloudapp.azure.com:8080/outs/" + name + ".mid")
		Ion.with(this@MainActivity)
				.load("http://sheettomidi.eastus.cloudapp.azure.com:8080/outs/" + name + ".mid")
				.write(fo)
				.setCallback { e, file2 ->
					if(file2 != null)
					{
						//mediaPlayerView.setupPlayer(file2.absolutePath)
						AudioWife.getInstance().init(this@MainActivity, Uri.fromFile(file2))
								.useDefaultUi(playerContainer, layoutInflater)
						AudioWife.getInstance().addOnCompletionListener(object : MediaPlayer.OnCompletionListener
						{

							override fun onCompletion(mp: MediaPlayer)
							{
								Toast.makeText(baseContext, "Completed", Toast.LENGTH_SHORT)
										.show()
								fab.show()
								// do you stuff
							}
						})

						AudioWife.getInstance().addOnPlayClickListener(object : View.OnClickListener
						{

							override fun onClick(v: View)
							{
								Toast.makeText(baseContext, "Play", Toast.LENGTH_SHORT)
										.show()
								fab.hide()
								// Lights-Camera-Action. Lets dance.
							}
						})

						AudioWife.getInstance().addOnPauseClickListener(object : View.OnClickListener
						{

							override fun onClick(v: View)
							{
								Toast.makeText(baseContext, "Pause", Toast.LENGTH_SHORT)
										.show()
								fab.show()
								// Your on audio pause stuff.
							}
						})
						//Log.v("PLAYSONG", "PLAYINGS SONG WOO")
					}
					else
					{
						Log.v("ERROROROR", e.toString())
					}
					}
	}//

	fun finishDataRefresh(list : List<String>)
	{
		items.clear()

		for(e: String in list)
		{
			val str = e.replace(".mid", "")
			items.add(MyData(str, BitmapFactory.decodeResource(resources, android.R.drawable.ic_media_play)))
			//Log.v("ADDING ITEM", str)
		}

		onItemsLoadComplete()
	}

	fun onItemsLoadComplete()
	{
		adapter.notifyDataSetChanged()
		refreshLayout.isRefreshing = false
	}

	fun convertToArray(res: String): List<String>
	{
		val step2 = res.replace("[", "")
		//Log.v("CONVERTING", step2)
		val step3 = step2.replace("\"", "")
		//Log.v("CONVERTING", step3)
		val step4 = step3.replace("]", "")
		val array = step4.split(",")
		//Log.v("CONVERTING", array.toString())
		return array
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		menuInflater.inflate(R.menu.menu_main, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		return when (item.itemId)
		{
			R.id.action_settings -> openImageSelector()
			else -> super.onOptionsItemSelected(item)
		}
	}

	private val PICK_IMAGE: Int = 0

	private fun openImageSelector(): Boolean
	{
		val photoPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
		photoPickerIntent.type = "image/*"
		startActivityForResult(photoPickerIntent, PICK_IMAGE)
		return true
	}

	private fun openCameraScanner(): Boolean
	{
		val REQUEST_CODE = 99
		val preference = ScanConstants.OPEN_CAMERA
		val intent = Intent(this, ScanActivity::class.java)
		intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference)
		startActivityForResult(intent, REQUEST_CODE)
		return true;
	}


	public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent)
	{
		super.onActivityResult(requestCode, resultCode, data)
		if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE)
		{
			val imageURI = data.data
			//Log.v("SHEETPLAYER", "msg" + imageURI);
			uploadImage(imageURI)
		}
		if (requestCode === 99 && resultCode === Activity.RESULT_OK)
		{
			val uri = data.extras.getParcelable<Uri>(ScanConstants.SCANNED_RESULT)
			uploadImage(uri)
		}
	}

	fun uploadImage(imageURI: Uri)
	{
		val filePath = getPath(applicationContext, imageURI)
		Snackbar.make(findViewById(android.R.id.content), "Uploading...", Snackbar.LENGTH_LONG).show()
		//Log.v("SHEETPLAYER", "msg UPLOAD IMAGE" + filePath)

		val f = File(filePath)

		//Log.d("DEBUG", "Choose: " + f.path)



		//Log.v("SHEETPLAYER", "file " + f.nameWithoutExtension)
		val uploading = Ion.with(this@MainActivity)
				.load("http://sheettomidi.eastus.cloudapp.azure.com:8080/upload")
				.setMultipartFile("image", f)
				.asString()
				.withResponse()
				.setCallback { e, result ->
					try
					{
						val jobj = JSONObject(result.result)
						Toast.makeText(applicationContext, jobj.getString("response"), Toast.LENGTH_SHORT).show()

					}
					catch (e1: JSONException)
					{
						e1.printStackTrace()
					}
				}
	}


}
