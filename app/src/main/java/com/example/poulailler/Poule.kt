package com.example.poulailler

import android.os.Parcel
import android.os.Parcelable

data class Poule(
    val id: String = "",
    val nom: String = "",
    val race: String = "",
    val poids: String = "",
    val caract: String? = "",
    val imageUrl: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(nom)
        parcel.writeString(race)
        parcel.writeString(poids)
        parcel.writeString(caract)
        parcel.writeString(imageUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Poule> {
        override fun createFromParcel(parcel: Parcel): Poule {
            return Poule(parcel)
        }

        override fun newArray(size: Int): Array<Poule?> {
            return arrayOfNulls(size)
        }
    }

}
