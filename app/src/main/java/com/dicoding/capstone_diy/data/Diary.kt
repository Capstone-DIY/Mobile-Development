package com.dicoding.capstone_diy.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_table")
data class Diary(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Primary key otomatis bertambah
    val date: Long, // Timestamp (sama seperti sebelumnya)
    val title: String?,
    val story: String?, // Mengganti 'description' menjadi 'story'
    val emotion: String?, // Menambahkan emotion
    val response: String?, // Menambahkan response
    val favorited: Boolean, // Menambahkan favorited
    val createdAt: String?, // Menambahkan created_at dari API
    val updatedAt: String?, // Menambahkan updated_at dari API
    val userId: Int? // Menambahkan userId untuk menyesuaikan dengan API
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(), // untuk 'favorited'
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeLong(date)
        parcel.writeString(title)
        parcel.writeString(story)
        parcel.writeString(emotion)
        parcel.writeString(response)
        parcel.writeByte(if (favorited) 1 else 0) // Menyimpan 'favorited' sebagai byte
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeInt(userId ?: 0) // Menambahkan userId
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Diary> {
        override fun createFromParcel(parcel: Parcel): Diary {
            return Diary(parcel)
        }

        override fun newArray(size: Int): Array<Diary?> {
            return arrayOfNulls(size)
        }
    }
}
