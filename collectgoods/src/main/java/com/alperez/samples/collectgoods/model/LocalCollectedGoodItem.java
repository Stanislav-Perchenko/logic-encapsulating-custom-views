package com.alperez.samples.collectgoods.model;

import com.alperez.samples.collectgoods.util.LongId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by stanislav.perchenko on 11/27/2019, 3:48 PM.
 */
public final class LocalCollectedGoodItem {

    private final LongId<PricedGoodEntity> goodcategoryId;
    private int weightKg;

    public LocalCollectedGoodItem(LongId<PricedGoodEntity> goodcategoryId, int weightKg) {
        assert (goodcategoryId != null);
        assert (weightKg > 0);
        this.goodcategoryId = goodcategoryId;
        this.weightKg = weightKg;
    }

    public LongId<PricedGoodEntity> getGoodcategoryId() {
        return goodcategoryId;
    }

    public int getWeightKg() {
        return weightKg;
    }

    public JSONObject toJsonObjecr() {
        try {
            JSONObject json = new JSONObject();
            json.put("id", goodcategoryId.getValue());
            json.put("weight", weightKg);
            return json;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public String tojson() {
        return toJsonObjecr().toString();
    }

    public static LocalCollectedGoodItem fromJson(String json) throws JSONException {
        return fromJsonObject(new JSONObject(json));
    }

    public static LocalCollectedGoodItem fromJsonObject(JSONObject jo) throws JSONException {
        return new LocalCollectedGoodItem(new LongId<PricedGoodEntity>(jo.getLong("id")), jo.getInt("weight"));
    }

    public static List<LocalCollectedGoodItem> fromJsonArray(JSONArray jArr) throws JSONException {
        final int len = jArr.length();
        List<LocalCollectedGoodItem> items = new ArrayList<>(len);
        for (int i=0; i<len; i++) {
            items.add(fromJsonObject(jArr.getJSONObject(i)));
        }
        return items;
    }

    public static JSONArray toJsonArray(Collection<LocalCollectedGoodItem> items) {
        JSONArray jArr = new JSONArray();
        for (LocalCollectedGoodItem item : items) {
            jArr.put(item.toJsonObjecr());
        }
        return jArr;
    }

    public static String toJsonArrayString(Collection<LocalCollectedGoodItem> items) {
        return toJsonArray(items).toString();
    }

}

