package com.alperez.samples.collectgoods.model

import android.os.Parcel
import android.os.Parcelable
import com.alperez.samples.collectgoods.util.IdProvidingModel
import com.alperez.samples.collectgoods.util.LongId
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by stanislav.perchenko on 12/3/2019, 12:09 PM.
 */
@JsonClass(generateAdapter = true)
data class UserEntity (
        @Json(name = "id")
        val id: LongId<UserEntity>,

        @Json(name = "full_name")
        val fullName: String,

        @Json(name = "icon")
        val iconPath: String?
) : IdProvidingModel, Parcelable {

    override fun id(): LongId<UserEntity> {
        return id
    }

    override fun describeContents() = 0

    private constructor(p: Parcel) : this(
            id = p.readParcelable(UserEntity.javaClass.classLoader),
            fullName = p.readString(),
            iconPath = p.readString()
    )

    override fun writeToParcel(p: Parcel, flags: Int) {
        p.writeParcelable(id, flags)
        p.writeString(fullName)
        p.writeString(iconPath)
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<UserEntity> {
            override fun createFromParcel(parcel: Parcel) = UserEntity(parcel)
            override fun newArray(size: Int) = arrayOfNulls<UserEntity>(size)
        }
    }


}