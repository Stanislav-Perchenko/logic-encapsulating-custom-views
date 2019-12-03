package com.alperez.samples.collectgoods.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alperez.samples.collectgoods.GlobalProperties;
import com.alperez.samples.collectgoods.model.LocalCollectedGoodItem;
import com.alperez.samples.collectgoods.model.PricedGoodEntity;
import com.alperez.samples.collectgoods.util.LongId;
import com.alperez.samples.collectgoods.util.PublicUtils;
import com.alperez.samples.collectgoods.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Created by stanislav.perchenko on 11/14/2019, 6:38 PM.
 */
public class SelectGoodItemView extends ScrollView {

    public interface OnSelectionChangeListener {
        void onSelectionChanged();
    }

    private SeekBar vSeekX1;
    private SeekBar vSeekX10;
    private SeekBar vSeekX100;
    private TextView vTxtWeight;

    private LinearLayout vCheckersContainer;


    private OnSelectionChangeListener onSelectionChangeListener;

    private final List<PricedGoodEntity> mGoods = new ArrayList<>(16);
    private boolean isGoodsDirty;

    /**
     * This index in set in the checkers' common click listener.
     * It is cleared in the clear()
     * It is used in the onSelectionChangedInternal() to update checkers UI
     */
    private int mSelectedCategoryIndex = -1;
    private int mSelectedWeight;
    private LongId<PricedGoodEntity> mSelectedCategoryId;

    private LocalCollectedGoodItem cachedResultSelectionModel;


    public SelectGoodItemView(Context context) {
        super(context);
        init(context);
    }

    public SelectGoodItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SelectGoodItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SelectGoodItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context c) {
        LayoutInflater.from(c).inflate(R.layout.view_select_good_item, this, true);
        vSeekX1 = findViewById(R.id.seek_bar_x1);
        vSeekX10 = findViewById(R.id.seek_bar_x10);
        vSeekX100 = findViewById(R.id.seek_bar_x100);
        vTxtWeight = findViewById(R.id.txt_selected_weight);
        vCheckersContainer = findViewById(R.id.goods_check_container);
        vSeekX1.setOnSeekBarChangeListener(seekChangeListener);
        vSeekX10.setOnSeekBarChangeListener(seekChangeListener);
        vSeekX100.setOnSeekBarChangeListener(seekChangeListener);
    }

    private final SeekBar.OnSeekBarChangeListener seekChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            onSelectionChangedInternal();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // Not used
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // Not used
        }
    };

    public void setOnSelectionChangeListener(OnSelectionChangeListener l) {
        this.onSelectionChangeListener = l;
    }


    public void setAllGoods(Collection<PricedGoodEntity> goods) {
        mGoods.clear();
        mGoods.addAll(goods);
        isGoodsDirty = true;
        if (isAttachedToWindow) populateGoods();
    }

    private boolean isAttachedToWindow;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttachedToWindow = true;
        if (isGoodsDirty) populateGoods();
    }

    @Override
    protected void onDetachedFromWindow() {
        isAttachedToWindow = false;
        super.onDetachedFromWindow();
    }

    private void populateGoods() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        vCheckersContainer.removeAllViews();
        int index = 0;
        for (PricedGoodEntity goodCat : mGoods) {
            View child = buildAndAddCheckableCategoryView(inflater, goodCat, index ++);
            child.findViewById(R.id.click_container).setOnClickListener(checkerClickListener);
        }
        clearSelection();
    }

    private final View.OnClickListener checkerClickListener = v -> {
        Object tag = v.getTag();
        if (tag instanceof Integer) {
            mSelectedCategoryIndex = ((Integer) tag).intValue();
            onSelectionChangedInternal();
        }
    };

    private View buildAndAddCheckableCategoryView(LayoutInflater inflater, PricedGoodEntity gCat, int index) {
        inflater.inflate(R.layout.layout_good_item_checker, vCheckersContainer, true);
        View child = vCheckersContainer.getChildAt(vCheckersContainer.getChildCount() - 1);
        Integer tag = index;
        child.setTag(tag);
        child.findViewById(R.id.frame_bg_checkable).setTag(tag);
        child.findViewById(R.id.click_container).setTag(tag);

        ((TextView) child.findViewById(R.id.txt_good_name)).setText(gCat.categoryName());
        ((TextView) child.findViewById(R.id.txt_ewc_code)).setText(gCat.ewcCode().getCode());
        String txtPrice = PublicUtils.formatPrice(gCat.priceValue(), gCat.priceScale(), true, GlobalProperties.APPLICATION_LOCALE);
        ((TextView) child.findViewById(R.id.txt_price)).setText((gCat.unitOfMeasure() == null) ? txtPrice : String.format("%s/%s", txtPrice, gCat.unitOfMeasure()));

        return child;
    }

    private void setCategoryViewChecked(View v, boolean isChecked) {
        int level = isChecked ? 1 : 0;
        v.findViewById(R.id.frame_bg_checkable).getBackground().setLevel(level);
        v.findViewById(R.id.check_mark).getBackground().setLevel(level);
    }


    public void clearSelection() {
        scrollTo(0, 0);
        vSeekX1.setOnSeekBarChangeListener(null);
        vSeekX1.setProgress(0);
        vSeekX1.setOnSeekBarChangeListener(seekChangeListener);

        vSeekX10.setOnSeekBarChangeListener(null);
        vSeekX10.setProgress(0);
        vSeekX10.setOnSeekBarChangeListener(seekChangeListener);

        vSeekX100.setOnSeekBarChangeListener(null);
        vSeekX100.setProgress(0);
        vSeekX100.setOnSeekBarChangeListener(seekChangeListener);

        mSelectedCategoryIndex = -1;

        onSelectionChangedInternal();
    }



    private void onSelectionChangedInternal() {
        //Extract selected weight and populate the UI with it.
        mSelectedWeight = 100*vSeekX100.getProgress() + 10*vSeekX10.getProgress() + vSeekX1.getProgress();
        vTxtWeight.setText(""+mSelectedWeight);

        // Extract selected good category ID
        mSelectedCategoryId = (mSelectedCategoryIndex < 0) ? null : mGoods.get(mSelectedCategoryIndex).id();

        // Updated checked UI with currently-selected category
        for (int i=0; i<vCheckersContainer.getChildCount(); i++) {
            View child = vCheckersContainer.getChildAt(i);
            setCategoryViewChecked(child, i == mSelectedCategoryIndex);
        }

        // Reset cached final data model
        cachedResultSelectionModel = null;

        // Notify external listener
        if (onSelectionChangeListener !=null) onSelectionChangeListener.onSelectionChanged();
    }

    public boolean isItemSelected() {
        return (mSelectedWeight > 0) && (mSelectedCategoryId != null);
    }

    @Nullable
    public LocalCollectedGoodItem getSelectedItem() {
        if (cachedResultSelectionModel == null && isItemSelected()) {
            cachedResultSelectionModel = new LocalCollectedGoodItem(mSelectedCategoryId, mSelectedWeight);
        }
        return cachedResultSelectionModel;
    }
}
