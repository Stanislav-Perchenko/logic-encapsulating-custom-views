package com.alperez.samples.collectgoods.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alperez.samples.collectgoods.model.LocalCollectedGoodItem;
import com.alperez.samples.collectgoods.model.PricedGoodEntity;
import com.alperez.samples.collectgoods.model.VisitEntity;
import com.alperez.samples.collectgoods.util.LongId;

import org.json.JSONException;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by stanislav.perchenko on 11/13/2019, 2:18 PM.
 */
public final class SingleVisitEditorStorage {

    private static SingleVisitEditorStorage INSTANCE;

    public static SingleVisitEditorStorage getInstance(Context c) {
        if (INSTANCE == null) {
            synchronized (SingleVisitEditorStorage.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SingleVisitEditorStorage(c.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    private final SharedPreferences sPrefs;

    private static final String PREF_VISIT_ID = "visit_id";
    private static final String PREF_COLLECTED_GOODS = "coll_goods";
    private static final String PREF_CUSTOMER_NAME = "customer_name";
    private static final String PREF_CUSTOMER_SIGN = "customer_signature";


    private LongId<VisitEntity> visitId;
    private final Map<Long, LocalCollectedGoodItem> collectedGoods = new LinkedHashMap<>();
    private String customerName;
    private String customerSignature;


    public SingleVisitEditorStorage(Context context) {
        this.sPrefs = context.getSharedPreferences(SingleVisitEditorStorage.class.getSimpleName(), Context.MODE_PRIVATE);
        long vis_id = sPrefs.getLong(PREF_VISIT_ID, 0);
        if (vis_id > 0) {
            try {
                visitId = new LongId<VisitEntity>(vis_id);
                Set<String> jsonSet = sPrefs.getStringSet(PREF_COLLECTED_GOODS, new HashSet<>());
                for (String json : jsonSet) {
                    LocalCollectedGoodItem item = LocalCollectedGoodItem.fromJson(json);
                    collectedGoods.put(item.getGoodcategoryId().getValue(), item);
                }
                customerName = sPrefs.getString(PREF_CUSTOMER_NAME, null);
                customerSignature = sPrefs.getString(PREF_CUSTOMER_SIGN, null);
            } catch (JSONException e) {
                visitId = null;
                collectedGoods.clear();
            }
        }
    }

    private void save() {
        if (visitId != null) {
            Set<String> jsonSet = new HashSet<>(collectedGoods.size());
            for (LocalCollectedGoodItem item : collectedGoods.values()) {
                jsonSet.add(item.tojson());
            }
            SharedPreferences.Editor edt = sPrefs.edit()
                    .putLong(PREF_VISIT_ID, visitId.getValue())
                    .putStringSet(PREF_COLLECTED_GOODS, jsonSet);
            if (customerName != null) edt.putString(PREF_CUSTOMER_NAME, customerName);
            if (customerSignature != null) edt.putString(PREF_CUSTOMER_SIGN, customerSignature);
            edt.commit();
        } else {
            sPrefs.edit().clear().commit();
        }
    }

    @Nullable
    public LongId<VisitEntity> inEditVisitId() {
        return visitId;
    }

    public void setInEditVisit(LongId<VisitEntity> visitId) {
        if (this.visitId == null) {
            this.visitId = visitId;
        } else if ((this.visitId != null) && !this.visitId.equals(visitId)) {
            throw new IllegalStateException("Another Visit is being edited");
        }
    }

    /**
     * Clear all collected goods and remove current in-edit visit
     */
    public void clear() {
        if (inEditVisitId() != null) {
            visitId = null;
            collectedGoods.clear();
            customerName = null;
            customerSignature = null;
            save();
        }
    }

    public void addCollectedItem(@NonNull LongId<VisitEntity> visitId, LocalCollectedGoodItem collectedItem) {
        if (inEditVisitId() == null) {
            throw new IllegalStateException("No in-edit visit is set");
        } else if (!visitId.equals(inEditVisitId())) {
            throw new IllegalArgumentException("Wrong visit");
        }

        Long id = collectedItem.getGoodcategoryId().getValue();
        this.collectedGoods.remove(id);
        this.collectedGoods.put(id, collectedItem);
        save();
    }

    public void removeCollectedItem(@NonNull LongId<VisitEntity> visitId, LongId<PricedGoodEntity> goodcategoryId) {
        if (visitId.equals(inEditVisitId()) && (this.collectedGoods.remove(goodcategoryId.getValue()) != null)) {
            save();
        }
    }

    public int getCollectedGoodsCount() {
        return collectedGoods.size();
    }

    public Set<LocalCollectedGoodItem> getAllCollectedGoods(@NonNull LongId<VisitEntity> visitId) {
        if (visitId.equals(inEditVisitId())) {
            Set<LocalCollectedGoodItem> result = new HashSet<>(collectedGoods.size());
            for (LocalCollectedGoodItem item : collectedGoods.values()) result.add(item);
            return result;
        } else {
            return new HashSet<>(0);
        }
    }

    public boolean isHasCustomerData() {
        return !(TextUtils.isEmpty(customerName) || TextUtils.isEmpty(customerSignature));
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerSignature() {
        return customerSignature;
    }

    public void setCustomerNameAndSignature(@NonNull LongId<VisitEntity> visitId, @NonNull String customerName, @Nullable String customerSignature) {
        if (inEditVisitId() == null) {
            throw new IllegalStateException("No in-edit visit is set");
        } else if (!visitId.equals(inEditVisitId())) {
            throw new IllegalArgumentException("Wrong visit");
        } else if (!customerName.equalsIgnoreCase(this.customerName) || !TextUtils.equals(this.customerSignature, customerSignature)) {
            this.customerName = customerName;
            this.customerSignature = customerSignature;
            save();
        }
    }

    public void clearCustomerSignature(@NonNull LongId<VisitEntity> visitId) {
        if (inEditVisitId() == null) {
            throw new IllegalStateException("No in-edit visit is set");
        } else if (!visitId.equals(inEditVisitId())) {
            throw new IllegalArgumentException("Wrong visit");
        } else if (customerSignature != null) {
            customerSignature = null;
            save();
        }
    }


    /*@Nullable
    public LocalVisitResult buildVisitResult() {
        try {
            List<LocalCollectedGoodItem> goods = new LinkedList<>();
            for (Iterator<Map.Entry<Long, LocalCollectedGoodItem>> itr = collectedGoods.entrySet().iterator(); itr.hasNext(); ) {
                goods.add(itr.next().getValue());
            }

            final String myName = SessionHolder.getSession().getUser().getFullName();

            return LocalVisitResult.builder()
                    .setVisitId(visitId)
                    .setCollectedGoods(goods.isEmpty() ? null : Collections.unmodifiableList(goods))
                    .setDriverName(myName)
                    .setCustomerName(customerName)
                    .setCustomerSignature(customerSignature)
                    .setOptDriverNote(optDriverNote)
                    .build();
        } catch (Exception e) {
            //If not all required data has been collected
            return null;
        }
    }*/
}
