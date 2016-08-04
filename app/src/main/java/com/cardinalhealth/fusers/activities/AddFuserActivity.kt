package com.cardinalhealth.fusers.activities

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import com.cardinalhealth.fusers.R
import com.cardinalhealth.fusers.models.Fuser
import io.realm.Realm
import io.realm.RealmConfiguration
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddFuserActivity : AppCompatActivity(), View.OnClickListener
{
	private val ENABLE_READ_REQUEST: Int = 1
	private val IMAGE_CAPTURE: Int = 2

	private lateinit var realm: Realm

	private lateinit var _name: EditText
	private lateinit var _team: EditText
	private lateinit var _thumbnail: ImageView
	private lateinit var _cameraButton: ImageButton
	private lateinit var _cancelButton: Button
	private lateinit var _okButton: Button

	private var _currentImagePath: String? = null
	private var _fullSizeImage: Bitmap? = null

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_add_fuser)

		val realmConfiguration = RealmConfiguration.Builder(this).build()
		realm = Realm.getInstance(realmConfiguration)

		_name = findViewById(R.id.name) as EditText
		_team = findViewById(R.id.team) as EditText
		_thumbnail = findViewById(R.id.thumbnail) as ImageView

		_cameraButton = findViewById(R.id.camera_button) as ImageButton
		_cancelButton = findViewById(R.id.cancel) as Button
		_okButton = findViewById(R.id.ok) as Button

		_cameraButton.setOnClickListener(this)
		_cancelButton.setOnClickListener(this)
		_okButton.setOnClickListener(this)
	}

	override fun onDestroy()
	{
		realm.close()
		super.onDestroy()
	}

	override fun onClick(view: View?)
	{
		if (view == _cancelButton)
		{
			finish()
		}
		else if (view == _okButton)
		{
			realm.beginTransaction()
			val fuser = realm.createObject(Fuser::class.java)
			fuser.name = _name.text.toString()
			fuser.team = _team.text.toString()
			fuser.image = _fullSizeImage
			realm.commitTransaction()
			finish()
		}
		else if (view == _cameraButton)
		{
			val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
			if (intent.resolveActivity(packageManager) != null)
			{
				var photoFile: File? = null
				try
				{
					photoFile = createImageFile()
				}
				catch (exception: IOException)
				{
					exception.printStackTrace()
				}

				photoFile?.let { file ->
					val uri = FileProvider.getUriForFile(this@AddFuserActivity, "com.cardinalhealth.fusers.fileprovider", file)
					intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
					startActivityForResult(intent, IMAGE_CAPTURE)
				}
			}
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
	{
		if (requestCode == IMAGE_CAPTURE && resultCode == Activity.RESULT_OK)
		{
			_fullSizeImage = BitmapFactory.decodeFile(_currentImagePath)
			_thumbnail.setImageBitmap(_fullSizeImage)
		}
	}

	@Throws(IOException::class)
	private fun createImageFile(): File
	{
		val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
		if (!dir.exists() && !dir.mkdirs())
		{
			Log.e("File Stuff", "Failed to create directories")
		}

		if (!dir.canWrite())
		{
			Log.e("File Stuff", "Directory is not writable")
		}

		val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
		val fileName = "JPEG_${timeStamp}.jpg"
		val file = File(dir, fileName)


		_currentImagePath = "file:" + file.absolutePath

		return file
	}
}
