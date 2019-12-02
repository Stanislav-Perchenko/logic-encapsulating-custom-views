package com.alperez.samples.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;

import com.alperez.samples.collectgoods.model.EWCCodeEntity;
import com.alperez.samples.collectgoods.model.PricedGoodEntity;
import com.alperez.samples.collectgoods.parser.ParserProvider;
import com.alperez.samples.collectgoods.util.LongId;
import com.alperez.samples.demoactivity.CollectingGoodsDemoActivityView;
import com.alperez.utils.AsyncTaskCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by stanislav.perchenko on 12/2/2019, 7:44 PM.
 */
public class CollectingGoodsDemoPresenter extends BasePresenter<CollectingGoodsDemoActivityView> {

    public CollectingGoodsDemoPresenter(CollectingGoodsDemoActivityView view) {
        super(view);
    }


    private AsyncTaskCompat<Void, Void, List<PricedGoodEntity>> goodsTask;

    @SuppressLint("StaticFieldLeak")
    @Override
    public void initializeView() {
        goodsTask = new AsyncTaskCompat<Void, Void, List<PricedGoodEntity>>() {
            private Context ctx;

            @Override
            protected void onPreExecute() {
                ctx = getView().getContext();
            }

            @Override
            protected List<PricedGoodEntity> doInBackground(Void... voids) {
                return loadAllGoodsFromAssets(ctx);
            }

            @Override
            protected void onPostExecute(List<PricedGoodEntity> result) {
                goodsTask = null;
                CollectingGoodsDemoActivityView v = getView();
                if (!isReleased() && (v != null)) {
                    v.onGoodsLoaded(result);
                }
            }

            @Override
            protected void onCancelled(List<PricedGoodEntity> result) {
                goodsTask = null;
            }
        };
        goodsTask.safeExecute();
    }


    @Override
    public synchronized void release() {
        super.release();
        if (goodsTask != null) {
            goodsTask.cancel(true);
            goodsTask = null;
        }
    }

    private List<PricedGoodEntity> loadAllGoodsFromAssets(Context ctx) {
        try (InputStream is_ewc = ctx.getAssets().open("ewc_codes.json", AssetManager.ACCESS_STREAMING); InputStream is_goods = ctx.getAssets().open("good_categories.json", AssetManager.ACCESS_STREAMING)) {

            String textEwc = readFromStream(is_ewc);
            EWCCodeEntity[] codes = ParserProvider.getMoshiParser().adapter(EWCCodeEntity[].class).fromJson(textEwc);
            Map<LongId<EWCCodeEntity>, EWCCodeEntity> mapCodes = new HashMap<>();
            for (EWCCodeEntity code : codes) {
                mapCodes.put(code.id(), code);
            }


            final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            JSONArray jGoods = new JSONArray(readFromStream(is_goods));
            List<PricedGoodEntity> goods = new ArrayList<>(jGoods.length());
            for (int i=0; i<jGoods.length(); i++) {
                JSONObject jg = jGoods.getJSONObject(i);
                PricedGoodEntity good = PricedGoodEntity.builder()
                        .setId(LongId.Companion.valueOf(jg.getLong("id")))
                        .setCategoryName(jg.getString("name"))
                        .setPriceId(LongId.Companion.valueOf(jg.getLong("price_id")))
                        .setPriceValue(jg.getInt("price_value"))
                        .setPriceScale(jg.getInt("price_scale"))
                        .setPriceSetDate(dateFormat.parse(jg.getString("price_date")))
                        .setEwcCode(mapCodes.get(LongId.Companion.valueOf(jg.getLong("ewc_id"))))
                        .setUnitOfMeasure(jg.optString("unit"))
                        .build();
                goods.add(good);
            }
            return goods;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private String readFromStream(InputStream is) throws IOException {
        byte buff[] = new byte[128];
        int count;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((count = is.read(buff)) > 0) {
            bos.write(buff, 0, count);
        }

        return new String(bos.toByteArray(), "UTF-8");
    }
}
