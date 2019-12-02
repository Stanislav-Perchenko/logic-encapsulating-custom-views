package com.alperez.samples.collectgoods.parser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alperez.samples.collectgoods.util.IdProvidingModel;
import com.alperez.samples.collectgoods.util.LongId;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;

import java.io.IOException;

/**
 * Created by stanislav.perchenko on 12/2/2019, 8:10 PM.
 */
public class LongIdJsonAdapter<T extends IdProvidingModel> extends JsonAdapter<LongId<T>> {

    @Nullable
    @Override
    public LongId<T> fromJson(JsonReader reader) throws IOException {
        Object value = reader.readJsonValue();
        return (value instanceof Number) ? LongId.Companion.valueOf(((Number) value).longValue()) : null;
    }

    @Override
    public void toJson(JsonWriter writer, @Nullable LongId<T> value) throws IOException {
        writer.value(value == null ? null : value.getValue());
    }

    @NonNull
    @Override
    public String toString() {
        return "LongIdJsonAdapter<T extends IdProvidingModel>";
    }
}
