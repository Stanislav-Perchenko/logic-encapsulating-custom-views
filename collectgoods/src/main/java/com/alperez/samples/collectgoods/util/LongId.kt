package com.alperez.samples.collectgoods.util

import android.os.Parcel
import android.os.Parcelable
import com.google.common.hash.HashCode

/**
 * Created by stanislav.perchenko on 11/27/2019, 3:23 PM.
 */
class LongId<T : IdProvidingModel> constructor(val value: Long) : Parcelable {

    @Transient
    private lateinit var cachedText: String

    override fun toString(): String {
        if (! ::cachedText.isInitialized) {
            synchronized(this) {
                if (! ::cachedText.isInitialized) {
                    cachedText = ""+value;
                }
            }
        }
        return cachedText
    }

    override fun equals(o: Any?): Boolean {
        return if (o is LongId<*>) {
            o.value == this.value
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return HashCode.fromLong(value).asInt()
    }

    override fun describeContents() = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(value)
    }

    private constructor(parcel: Parcel) : this(value = parcel.readLong())

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<LongId<IdProvidingModel>> {
            override fun createFromParcel(parcel: Parcel) = LongId<IdProvidingModel>(parcel)
            override fun newArray(size: Int) = arrayOfNulls<LongId<IdProvidingModel>>(size)
        }

        fun <E : IdProvidingModel> valueOf(value: Long): LongId<E> {
            return LongId(value)
        }
    }

}