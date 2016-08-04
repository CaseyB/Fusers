package com.cardinalhealth.fusers.models

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.Required
import java.io.ByteArrayOutputStream

open class Fuser(name: String, team: String) : RealmObject(), Parcelable
{
	companion object
	{
		val EXTRA_FUSER = "EXTRA_FUSER"

		@JvmField val CREATOR: Parcelable.Creator<Fuser> = object : Parcelable.Creator<Fuser>
		{
			override fun createFromParcel(source: Parcel): Fuser
			{
				val name = source.readString()
				val team = source.readString()
				return Fuser(name, team)
			}

			override fun newArray(size: Int): Array<Fuser?>
			{
				return arrayOfNulls(size)
			}
		}
	}

	@Required
	var name: String
	@Required
	var team: String

	@Ignore
	var image: Bitmap? = null
	val _imageData: ByteArray?
			get()
			{
				image?.let { bitmap ->
					val stream = ByteArrayOutputStream()
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
					return stream.toByteArray()
				}

				return null
			}

	init
	{
		this.name = name
		this.team = team
	}

	constructor() : this("", "")
	{
	}

	override fun writeToParcel(parcel: Parcel?, flags: Int)
	{
		parcel?.writeString(name)
		parcel?.writeString(team)
	}

	override fun describeContents(): Int
	{
		return 0
	}
}
