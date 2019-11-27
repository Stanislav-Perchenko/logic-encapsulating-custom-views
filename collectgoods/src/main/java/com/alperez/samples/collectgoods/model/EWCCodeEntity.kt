package com.alperez.samples.collectgoods.model

import android.os.Parcel
import android.os.Parcelable
import com.alperez.samples.collectgoods.util.IdProvidingModel
import com.alperez.samples.collectgoods.util.LongId
import com.alperez.utils.StringJoinerCompat
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by stanislav.perchenko on 11/27/2019, 3:37 PM.
 */
@JsonClass(generateAdapter = true)
data class EWCCodeEntity (
        @Json(name = "id")
        val id: LongId<EWCCodeEntity>,

        @Json(name = "ewc_code")
        val code: String,

        @Json(name = "ewc_category_title")
        val title: String
) : IdProvidingModel, Parcelable {

    override fun id(): LongId<EWCCodeEntity> {
        return id
    }

    override fun describeContents() = 0

    private constructor(p: Parcel) : this(
            id = p.readParcelable(EWCCodeEntity.javaClass.classLoader),
            code = p.readString(),
            title = p.readString()
    )

    override fun writeToParcel(p: Parcel, flags: Int) {
        p.writeParcelable(id, flags)
        p.writeString(code)
        p.writeString(title)
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<EWCCodeEntity> {
            override fun createFromParcel(parcel: Parcel) = EWCCodeEntity(parcel)
            override fun newArray(size: Int) = arrayOfNulls<EWCCodeEntity>(size)
        }
    }

    class Builder {
        private lateinit var id: LongId<EWCCodeEntity>
        private lateinit var code: String
        private lateinit var title: String

        fun setId(id: LongId<EWCCodeEntity>) = apply { this.id = id }
        fun setCode(code: String) = apply { this.code = code }
        fun setTitle(title: String) = apply { this.title = title }

        fun build(): EWCCodeEntity {
            val missed = StringJoinerCompat(", ", "", "")

            if (! ::id.isInitialized) missed.add("id")
            if (! ::code.isInitialized) missed.add("code")
            if (! ::title.isInitialized) missed.add("title")

            if (missed.length() == 0) {
                return EWCCodeEntity(id = id,
                        code = code,
                        title = title )
            } else {
                throw IllegalStateException("EWCCodeEntity.Builder - some fields are missed: $missed")
            }
        }
    }

}