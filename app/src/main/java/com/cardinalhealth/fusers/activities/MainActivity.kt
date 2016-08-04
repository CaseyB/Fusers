package com.cardinalhealth.fusers.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.*
import com.cardinalhealth.fusers.R
import com.cardinalhealth.fusers.models.Fuser
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmBaseAdapter
import io.realm.RealmConfiguration

class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener
{
	private lateinit var list: ListView
	private lateinit var adpater: FuserAdapter
	private lateinit var realm: Realm

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		val realmConfiguration = RealmConfiguration.Builder(this).build()

		// Create a new empty instance of Realm
		realm = Realm.getInstance(realmConfiguration)

		if(realm.where(Fuser::class.java).count() == 0L)
		{
			realm.beginTransaction()
			var fuser = realm.createObject(Fuser::class.java)
			fuser.name = "Casey Borders"
			fuser.team = "Hector"

			fuser = realm.createObject(Fuser::class.java)
			fuser.name = "Peter Hewitt"
			fuser.team = "Muppets"
			realm.commitTransaction()
		}

		val query = realm.where(Fuser::class.java)
		val results = query.findAll()
		results.addChangeListener { updates ->
			adpater.updateData(updates)
		}

		adpater = FuserAdapter(this, results)
		list = findViewById(R.id.list_view) as ListView
		list.emptyView = findViewById(R.id.empty_view)
		list.adapter = adpater
		list.onItemClickListener = this
	}

	override fun onDestroy()
	{
		realm.close()
		super.onDestroy()
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean
	{
		menuInflater.inflate(R.menu.menu, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean
	{
		if (item != null && item.itemId == R.id.add_action)
		{
			val intent = Intent(this, AddFuserActivity::class.java)
			startActivity(intent)
			return true
		}
		else
		{
			return super.onOptionsItemSelected(item)
		}
	}

	override fun onItemClick(list: AdapterView<*>?, row: View?, position: Int, id: Long)
	{
		val fuser = list?.adapter?.getItem(position) as Fuser
		var intent = Intent(this, ViewFuserActivity::class.java)
		intent.putExtra(Fuser.EXTRA_FUSER, fuser)
		startActivity(intent)
	}

	private class FuserAdapter(context: Context, list: OrderedRealmCollection<Fuser>) : RealmBaseAdapter<Fuser>(context, list), ListAdapter
	{
		val _inflator: LayoutInflater

		companion object
		{
			data class ViewHolder(val name: TextView, val team: TextView, val image: ImageView)
			{}
		}

		init
		{
			_inflator = LayoutInflater.from(context)
		}

		override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View
		{
			val fuser = adapterData[position]
			val holder: ViewHolder?
			val result: View?

			if (convertView == null)
			{
				result = _inflator.inflate(R.layout.fuser_row, parent, false)
				holder = ViewHolder(result?.findViewById(R.id.name) as TextView,
						result?.findViewById(R.id.team) as TextView,
						result?.findViewById(R.id.image) as ImageView)
				result?.tag = holder
			}
			else
			{
				result = convertView
				holder = result.tag as ViewHolder
			}

			holder.name.text = fuser.name
			holder.team.text = fuser.team

			if (fuser.image != null) holder.image.setImageBitmap(fuser.image)
			else holder.image.setImageResource(R.mipmap.ic_launcher)

			return result!!
		}
	}
}
