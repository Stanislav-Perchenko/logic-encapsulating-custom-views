package com.alperez.samples.collectgoods.model

import com.alperez.samples.collectgoods.util.IdProvidingModel
import com.alperez.samples.collectgoods.util.LongId
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * This class is not implemented because it is not used on the client level directly
 * This stub is created just to parametrize LongId entity
 *
 * Created by stanislav.perchenko on 11/27/2019, 4:17 PM.
 */
@JsonClass(generateAdapter = true)
data class VisitEntity(
        @Json(name = "price_id")
        val id: LongId<VisitEntity>
) : IdProvidingModel {

    override fun id(): LongId<VisitEntity> {
        return id
    }

}