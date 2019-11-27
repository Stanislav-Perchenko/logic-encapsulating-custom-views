package com.alperez.samples.collectgoods.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alperez.samples.collectgoods.R;
import com.alperez.samples.collectgoods.model.PricedGoodEntity;
import com.alperez.samples.collectgoods.util.LongId;
import com.alperez.samples.collectgoods.util.PublicUtils;

import java.util.Locale;

/**
 * Created by stanislav.perchenko on 11/16/2019, 4:51 PM.
 */
public class CollectedGoodItemView extends FrameLayout {
    private static final Locale APPLICATION_LOCALE = new Locale("en", "GB", "");

    public interface OnDeleteListener {
        void onDelete(CollectedGoodItemView v);
    }

    private PricedGoodEntity goodCategory;
    private Integer amount;

    private OnDeleteListener onDeleteListener;

    private TextView vTxtGoodName;
    private TextView vTxtAmount;
    private TextView vTxtEwcCode;
    private TextView vTxtPrice;

    public CollectedGoodItemView(@NonNull Context context, PricedGoodEntity goodCategory, Integer amount) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.view_collected_good_item, this, true);
        vTxtGoodName = findViewById(R.id.txt_good_name);
        vTxtAmount = findViewById(R.id.txt_amount_weight);
        vTxtEwcCode = findViewById(R.id.txt_ewc_code);
        vTxtPrice = findViewById(R.id.txt_price);

        setData(goodCategory, amount);

        findViewById(R.id.action_delete).setOnClickListener(v -> {
            if (onDeleteListener != null) onDeleteListener.onDelete(this);
        });
    }

    public void setOnDeleteListener(OnDeleteListener l) {
        this.onDeleteListener = l;
    }

    public void setData(PricedGoodEntity gCat, Integer amount) {
        assert (gCat != null);
        assert (amount != null);
        this.goodCategory = gCat;
        this.amount = amount;
        vTxtGoodName.setText(gCat.categoryName());
        vTxtEwcCode.setText(gCat.ewcCode().getCode());
        String txtPrice = PublicUtils.formatPrice(gCat.priceValue(), gCat.priceScale(), true, APPLICATION_LOCALE);
        vTxtPrice.setText((gCat.unitOfMeasure() == null) ? txtPrice : String.format("%s/%s", txtPrice, gCat.unitOfMeasure()));
        vTxtAmount.setText(getResources().getString(R.string.collected_amount, amount.intValue(), gCat.unitOfMeasure()));
    }

    public LongId<PricedGoodEntity> getGoodId() {
        return goodCategory.id();
    }

    public int getCollectedAmount() {
        return amount;
    }

    public int getTotalValue() {
        return amount * goodCategory.priceValue();
    }

    public int getTotalScale() {
        return goodCategory.priceScale();
    }

}

