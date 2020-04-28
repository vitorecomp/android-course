package com.androiddesenv.opiniaodetudo.infra.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

object ReviewTableInfo{
    const val TABLE_NAME = "Review"
    const val COLUMN_ID = "id"
    const val COLUMN_NAME = "name"
    const val COLUMN_REVIEW = "review"
    const val COLUMN_PHOTO_PATH = "photo_path"
    const val COLUMN_THUMBNAIL = "thumbnail"
    const val COLUMN_LONGITUDE = "longitude"
    const val COLUMN_LATITUDE = "latitude"
}

@Entity
data class Review(
    @PrimaryKey
    val id: String,
    val name: String,
    val review: String?,
    @ColumnInfo(name="photo_path")
    val photoPath: String?,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val thumbnail: ByteArray?,
    @ColumnInfo(typeAffinity = ColumnInfo.REAL)
    var latitude: Double?,
    @ColumnInfo(typeAffinity = ColumnInfo.REAL)
    var longitude: Double?) : Parcelable {



    constructor(id: String, name: String, review: String?) : this(id, name, review, null, null, null, null)
    constructor(id: String, name: String, review: String?, photoPath: String?, thumbnail: ByteArray?) : this(id, name, review, photoPath, thumbnail, null, null )

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.createByteArray()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(review)
        parcel.writeString(photoPath)
        parcel.writeByteArray(thumbnail)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Review

        if (id != other.id) return false
        if (name != other.name) return false
        if (review != other.review) return false
        if (photoPath != other.photoPath) return false
        if (thumbnail != null) {
            if (other.thumbnail == null) return false
            if (!thumbnail.contentEquals(other.thumbnail)) return false
        } else if (other.thumbnail != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (review?.hashCode() ?: 0)
        result = 31 * result + (photoPath?.hashCode() ?: 0)
        result = 31 * result + (thumbnail?.contentHashCode() ?: 0)
        return result
    }

    companion object CREATOR : Parcelable.Creator<Review> {
        override fun createFromParcel(parcel: Parcel): Review {
            return Review(parcel)
        }

        override fun newArray(size: Int): Array<Review?> {
            return arrayOfNulls(size)
        }
    }
}
