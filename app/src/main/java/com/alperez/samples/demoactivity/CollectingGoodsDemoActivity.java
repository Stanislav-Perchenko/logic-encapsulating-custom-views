package com.alperez.samples.demoactivity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alperez.library.widget.SignatureEditorView;
import com.alperez.library.widget.SignatureView;
import com.alperez.library.widget.SlidingViewFlipper;
import com.alperez.samples.R;
import com.alperez.samples.collectgoods.model.LocalCollectedGoodItem;
import com.alperez.samples.collectgoods.model.PricedGoodEntity;
import com.alperez.samples.collectgoods.model.VisitEntity;
import com.alperez.samples.collectgoods.storage.SingleVisitEditorStorage;
import com.alperez.samples.collectgoods.util.LongId;
import com.alperez.samples.collectgoods.widget.CollectedGoodItemView;
import com.alperez.samples.collectgoods.widget.SelectGoodItemView;
import com.alperez.samples.presenter.CollectingGoodsDemoPresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by stanislav.perchenko on 11/27/2019, 11:52 AM.
 */
public class CollectingGoodsDemoActivity extends BaseDemoActivity implements CollectingGoodsDemoActivityView {
    public static final String ARG_VISIT_ID = "visit_id";

    private SlidingViewFlipper vFlipper;

    //--- Page 1 ---
    private LinearLayout vCollectedGoodsContainer;

    //--- Signature section ---
    private TextView vTxtCustomerName;
    private View vActionFillInCustomer;
    private ViewGroup vSignContainer;
    private SignatureView vSignView;
    private View vHintTapToSign;
    private View vActionClearSignature;
    private View vActionChangeSignature;

    //--- Page 2 ---
    private SelectGoodItemView vGoodsSelector;

    //--- Page 3 ---
    private EditText vEdtCustomerName;
    private SignatureEditorView vEdtSignature;
    private View vActionClearSignEditor;
    private View vActionApplySignature;
    private View vHintSignHere;
    private TextView dbgTxtNSignTracks;

    private final List<PricedGoodEntity> mGoodCategs = new ArrayList<>();

    private CollectingGoodsDemoPresenter mPresenter;

    /**
     * This parameter is passed to the activity as an argument
     */
    private final LongId<VisitEntity> argVisitId = LongId.Companion.valueOf(101);

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_collecting_goods_demo;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //TODO uncomment this for real use
        //argVisitId = getIntent().getParcelableExtra(ARG_VISIT_ID);
        if (argVisitId == null) {
            throw new IllegalStateException(String.format("The '%s' argument is not provided", ARG_VISIT_ID));
        } else if ((SingleVisitEditorStorage.getInstance(this).inEditVisitId() != null) && !argVisitId.equals(SingleVisitEditorStorage.getInstance(this).inEditVisitId())) {
            throw new IllegalStateException("Another Visit is in edit mode");
        } else if (SingleVisitEditorStorage.getInstance(this).inEditVisitId() == null) {
            SingleVisitEditorStorage.getInstance(this).setInEditVisit(argVisitId);
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        vFlipper = findViewById(R.id.flipper);
        vCollectedGoodsContainer = findViewById(R.id.collected_goods_container);
        vGoodsSelector = findViewById(R.id.good_item_selector);

        vFlipper.setOnChildChangedListener(childIndex -> invalidateOptionsMenu());
        vGoodsSelector.setOnSelectionChangeListener(this::invalidateOptionsMenu);

        findViewById(R.id.action_collect_more).setOnClickListener(v -> {
            vGoodsSelector.clearSelection();
            vFlipper.showNext(1);
        });

        findViewById(R.id.action_clear_all).setOnClickListener(this::clearAllData);

        //TODO Load driver photo and name
        /*UserEntity driver = SessionHolder.currentSession().getUser();
        AvousApplication.getPicasso().load(driver.getPhotoUrl()).into((ImageView) findViewById(R.id.ic_driver_photo));
        ((TextView) findViewById(R.id.txt_driver_name)).setText(driver.getFullName());*/

        setupSignatureEditorPage();


        setupCustomerUiSection();
        populateCustomerFromStorage();

        mPresenter = new CollectingGoodsDemoPresenter(this);
        mPresenter.initializeView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_visit, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean collectDoneVisible = (vFlipper.getDisplayedChild() == 1) && vGoodsSelector.isItemSelected();
        menu.findItem(R.id.menu_item_collect_good_item).setVisible(collectDoneVisible);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_item_collect_good_item:
                LocalCollectedGoodItem selection = vGoodsSelector.getSelectedItem();
                for (PricedGoodEntity good : mGoodCategs) {
                    if (good.id().equals(selection.getGoodcategoryId())) {
                        SingleVisitEditorStorage.getInstance(this).addCollectedItem(argVisitId, selection);
                        addNewCollectedGoodToUi(good, selection.getWeightKg());
                    }
                }
                vFlipper.showPrevious(0);
                return true;
            /*case R.id.menu_item_fail_visit:
                onTryFailVisit();
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (vFlipper.getDisplayedChild() > 0) {
            vFlipper.showPrevious(0);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.release();
    }

    private void populateCurrentlySelectedGoodsForVisit(List<PricedGoodEntity> goodCategs) {
        vCollectedGoodsContainer.removeAllViews();
        Set<LocalCollectedGoodItem> goods = SingleVisitEditorStorage.getInstance(this).getAllCollectedGoods(argVisitId);
        for (LocalCollectedGoodItem collectedItem : goods) {
            PricedGoodEntity good = null;
            for (PricedGoodEntity g_item : goodCategs) {
                if (g_item.id().equals(collectedItem.getGoodcategoryId())) {
                    good = g_item;
                    break;
                }
            }

            if (good != null) {
                CollectedGoodItemView childView = new CollectedGoodItemView(this, good, collectedItem.getWeightKg());
                childView.setOnDeleteListener(this::onDeleteCollectedGood);
                vCollectedGoodsContainer.addView(childView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        }
    }

    private void addNewCollectedGoodToUi(PricedGoodEntity good, int weightAmount) {
        CollectedGoodItemView childView = null;
        for (int i=0; i < vCollectedGoodsContainer.getChildCount(); i++) {
            CollectedGoodItemView child = (CollectedGoodItemView) vCollectedGoodsContainer.getChildAt(i);
            if (child.getGoodId().equals(good.id())) {
                childView = child;
                vCollectedGoodsContainer.removeViewAt(i);
                break;
            }
        }
        if (childView == null) {
            childView = new CollectedGoodItemView(this, good, weightAmount);
            childView.setOnDeleteListener(this::onDeleteCollectedGood);
        } else {
            childView.setData(good, weightAmount);
        }
        vCollectedGoodsContainer.addView(childView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void onDeleteCollectedGood(final CollectedGoodItemView goodView) {
        new AlertDialog.Builder(this).setMessage("Remove item?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    SingleVisitEditorStorage.getInstance(this).removeCollectedItem(argVisitId, goodView.getGoodId());
                    vCollectedGoodsContainer.removeView(goodView);
                }).setNegativeButton(android.R.string.no, null).show();
    }

    private void clearAllData(View v) {
        //TODO Implement this !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    }


    /***************************  Page 3 - signature editor  **************************************/
    private void setupSignatureEditorPage() {
        vEdtCustomerName = findViewById(R.id.edt_customer_name);
        vEdtSignature = findViewById(R.id.signature_editor);
        vActionClearSignEditor = findViewById(R.id.action_clear_signature_editor);
        vActionApplySignature = findViewById(R.id.action_apply_signature);
        vHintSignHere = findViewById(R.id.txt_hint_sign_here);
        dbgTxtNSignTracks = findViewById(R.id.txt_num_sign_tracks);
        dbgTxtNSignTracks.setVisibility(View.GONE);

        vEdtSignature.clear();
        vActionApplySignature.setVisibility(View.GONE);
        vActionClearSignEditor.setVisibility(View.GONE);
        vActionClearSignEditor.setOnClickListener(v -> {
            vEdtSignature.clear();
            vEdtCustomerName.setText("");
            vActionApplySignature.setVisibility(View.GONE);
            vActionClearSignEditor.setVisibility(View.GONE);
            vHintSignHere.setVisibility(View.VISIBLE);
        });

        vEdtSignature.setOnSignatureChangeListener(this::updateSignEditorActionsVisibility);
        vEdtSignature.setOnTouchStateChangeListener(vEdt -> {
            vHintSignHere.setVisibility((vEdt.isInTouch() || vEdt.isHasSignature()) ? View.GONE : View.VISIBLE);
            vActionApplySignature.setVisibility(((vEdtCustomerName.getText().length() > 0) && vEdt.isHasSignature() && !vEdt.isInTouch()) ? View.VISIBLE : View.GONE);
        });
        vEdtCustomerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not implemented
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not implemented
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateSignEditorActionsVisibility(vEdtSignature.getSignTracksCount());
            }
        });

        vActionApplySignature.setOnClickListener(v -> {
            String customerName = vEdtCustomerName.getText().toString();
            if (!TextUtils.isEmpty(customerName)) onSetCustomerSignature(customerName, vEdtSignature.getSignature());
        });
    }

    private void updateSignEditorActionsVisibility(int nSignTracks) {
        boolean hasSign = (nSignTracks > 0);

        dbgTxtNSignTracks.setVisibility(hasSign ? View.VISIBLE : View.GONE);
        dbgTxtNSignTracks.setText(""+nSignTracks);
        vActionClearSignEditor.setVisibility(hasSign ? View.VISIBLE : View.GONE);
        vHintSignHere.setVisibility(hasSign ? View.GONE : View.VISIBLE);
        vActionApplySignature.setVisibility(((vEdtCustomerName.getText().length() > 0) && hasSign && !vEdtSignature.isInTouch()) ? View.VISIBLE : View.GONE);
    }

    /***************************  page 1 - Customer/signature section  ****************************/
    private void setupCustomerUiSection() {
        vTxtCustomerName = findViewById(R.id.txt_customer_name);
        vActionFillInCustomer = findViewById(R.id.action_fill_in_customer);
        vSignContainer = findViewById(R.id.signature_container);
        vSignView = findViewById(R.id.signature_view);
        vHintTapToSign = findViewById(R.id.hint_tap_to_sign);
        vActionClearSignature = findViewById(R.id.action_clear_signature);
        vActionChangeSignature = findViewById(R.id.action_change_signature);

        vActionFillInCustomer.setOnClickListener(this::onStartEditCustomer);
        vSignContainer.setOnClickListener(this::onStartEditCustomer);
        vActionChangeSignature.setOnClickListener(this::onStartEditCustomer);

        vActionClearSignature.setOnClickListener(v -> {
            SingleVisitEditorStorage.getInstance(this).clearCustomerSignature(argVisitId);
            populateCustomerFromStorage();
        });

    }

    private void onStartEditCustomer(View v) {
        final String name = SingleVisitEditorStorage.getInstance(this).getCustomerName();
        if (name != null) vEdtCustomerName.setText(name);
        vEdtSignature.clear();
        vFlipper.showNext(2);
    }

    private void onSetCustomerSignature(@NonNull String customerName, @Nullable String signature) {
        SingleVisitEditorStorage.getInstance(this).setCustomerNameAndSignature(argVisitId, customerName, signature);
        populateCustomerFromStorage();
        onBackPressed();
    }

    private void populateCustomerFromStorage() {
        final String name = SingleVisitEditorStorage.getInstance(this).getCustomerName();
        final String sign =  SingleVisitEditorStorage.getInstance(this).getCustomerSignature();

        if (name == null) {
            vTxtCustomerName.setVisibility(View.GONE);
            vActionFillInCustomer.setVisibility(View.VISIBLE);
            vSignView.setSignature(null);
            vHintTapToSign.setVisibility(View.VISIBLE);
            vActionClearSignature.setVisibility(View.GONE);
            vActionChangeSignature.setVisibility(View.GONE);
        } else {
            vTxtCustomerName.setText(name);
            vTxtCustomerName.setVisibility(View.VISIBLE);
            vActionFillInCustomer.setVisibility(View.GONE);
            vSignView.setSignature(sign);
            vActionChangeSignature.setVisibility(View.VISIBLE);
            if (sign == null) {
                vHintTapToSign.setVisibility(View.VISIBLE);
                vActionClearSignature.setVisibility(View.GONE);
            } else {
                vHintTapToSign.setVisibility(View.GONE);
                vActionClearSignature.setVisibility(View.VISIBLE);
            }
        }
    }

    /***************************  View interface implementation  **********************************/
    boolean goodsLoadedFirstTime = true;
    @Override
    public void onGoodsLoaded(List<PricedGoodEntity> goodCategs) {
        mGoodCategs.clear();
        mGoodCategs.addAll(goodCategs);
        vGoodsSelector.setAllGoods(goodCategs);
        if (goodsLoadedFirstTime) {
            goodsLoadedFirstTime = false;
            populateCurrentlySelectedGoodsForVisit(goodCategs);
        }
    }

    @Override
    public Context getContext() {
        return this;
    }
}
