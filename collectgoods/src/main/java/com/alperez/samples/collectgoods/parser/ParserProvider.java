package com.alperez.samples.collectgoods.parser;

import com.alperez.samples.collectgoods.model.EWCCodeEntity;
import com.alperez.samples.collectgoods.model.UserEntity;
import com.alperez.samples.collectgoods.util.LongId;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory;

/**
 * Created by stanislav.perchenko on 12/2/2019, 8:11 PM.
 */
public final class ParserProvider {

    public static Moshi getMoshiParser() {
        Moshi m = new Moshi.Builder()
                .add(Types.newParameterizedType(LongId.class, UserEntity.class), new LongIdJsonAdapter<UserEntity>())
                .add(Types.newParameterizedType(LongId.class, EWCCodeEntity.class), new LongIdJsonAdapter<EWCCodeEntity>())
                .build();

        return m.newBuilder()
                .add(new KotlinJsonAdapterFactory())
                .build();
    }

    private ParserProvider() { }
}
