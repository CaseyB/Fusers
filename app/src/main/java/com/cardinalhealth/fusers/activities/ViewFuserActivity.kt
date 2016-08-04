package com.cardinalhealth.fusers.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.cardinalhealth.fusers.R
import com.cardinalhealth.fusers.models.Fuser

class ViewFuserActivity : AppCompatActivity()
{
	private lateinit var _image: ImageView
	private lateinit var _name: TextView
	private lateinit var _team: TextView

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_view_fuser)

		_image = findViewById(R.id.headshot) as ImageView
		_name = findViewById(R.id.name) as TextView
		_team = findViewById(R.id.team) as TextView
	}

	override fun onResume()
	{
		super.onResume()

		val fuser = intent.getParcelableExtra<Fuser>(Fuser.EXTRA_FUSER)

		_name.text = fuser.name
		_team.text = fuser.team
		fuser.image?.let { image ->
			_image.setImageBitmap(image)
		}
	}

	override fun onNewIntent(intent: Intent?)
	{
		this.intent = intent
	}
}
