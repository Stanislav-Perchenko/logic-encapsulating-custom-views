package com.alperez.samples.collectgoods.model;

import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.alperez.samples.collectgoods.util.IdProvidingModel;
import com.alperez.samples.collectgoods.util.LongId;
import com.google.auto.value.AutoValue;

import java.util.Date;

/**
 * Created by stanislav.perchenko on 11/27/2019, 3:46 PM.
 */
@AutoValue
public abstract class PricedGoodEntity implements IdProvidingModel, Parcelable {
    public abstract LongId<PricedGoodEntity> id();

    public abstract String categoryName();

    public abstract LongId<PriceEntity> priceId();

    public abstract int priceValue();

    public abstract int priceScale();

    public abstract Date priceSetDate();

    @Nullable
    public abstract EWCCodeEntity ewcCode();

    @Nullable
    public abstract String unitOfMeasure();

    public static Builder builder() {
        return new AutoValue_PricedGoodEntity.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setId(LongId<PricedGoodEntity> id);
        public abstract Builder setCategoryName(String categoryName);
        public abstract Builder setPriceId(LongId<PriceEntity> priceId);
        public abstract Builder setPriceValue(int priceValue);
        public abstract Builder setPriceScale(int priceScale);
        public abstract Builder setPriceSetDate(Date priceSetDate);
        public abstract Builder setEwcCode(@Nullable EWCCodeEntity ewcCode);
        public abstract Builder setUnitOfMeasure(@Nullable String unitOfMeasure);

        public abstract PricedGoodEntity build();
    }
}
