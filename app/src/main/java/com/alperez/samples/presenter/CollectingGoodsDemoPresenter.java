package com.alperez.samples.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alperez.samples.collectgoods.model.EWCCodeEntity;
import com.alperez.samples.collectgoods.model.PricedGoodEntity;
import com.alperez.samples.collectgoods.model.UserEntity;
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
import java.util.LinkedList;
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
    private AsyncTaskCompat<Void, Void, UserEntity> userTask;
    private final List<ImgLoadingTask> imgTasks = new LinkedList<>();

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

        userTask = new AsyncTaskCompat<Void, Void, UserEntity>() {
            private Context ctx;

            @Override
            protected void onPreExecute() {
                ctx = getView().getContext();
            }

            @Override
            protected UserEntity doInBackground(Void... voids) {
                try(InputStream is = ctx.getAssets().open("collect_goods/logged_in_user.json", AssetManager.ACCESS_STREAMING)) {
                    String textUser = readFromStream(is);
                    UserEntity user = ParserProvider.getMoshiParser().adapter(UserEntity.class).fromJson(textUser);
                    return user;
                } catch (IOException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(UserEntity result) {
                userTask = null;
                CollectingGoodsDemoActivityView v = getView();
                if (!isReleased() && (v != null)) {
                    v.onUserLoaded(result);
                }
            }

            @Override
            protected void onCancelled(UserEntity result) {
                userTask = null;
            }
        };
        userTask.safeExecute();

    }




    public boolean loadImageAsync(@Nullable String path) {
        if (TextUtils.isEmpty(path)) return false;
        ImgLoadingTask task = new ImgLoadingTask(getView().getContext(), path);
        task.safeExecute();
        return true;
    }


    @Override
    public synchronized void release() {
        super.release();
        if (goodsTask != null) {
            goodsTask.cancel(true);
            goodsTask = null;
        }

        if (userTask != null) {
            userTask.cancel(true);
            userTask = null;
        }

        if (!imgTasks.isEmpty()) {
            List<ImgLoadingTask> cache = new ArrayList<>(imgTasks.size());
            cache.addAll(imgTasks);
            imgTasks.clear();
            for (ImgLoadingTask task : cache) task.cancel(true);
            cache.clear();
        }
    }

    private List<PricedGoodEntity> loadAllGoodsFromAssets(Context ctx) {
        try (InputStream is_ewc = ctx.getAssets().open("collect_goods/ewc_codes.json", AssetManager.ACCESS_STREAMING); InputStream is_goods = ctx.getAssets().open("collect_goods/good_categories.json", AssetManager.ACCESS_STREAMING)) {

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



    /****************************  Loading images from Assets  ************************************/

    private class ImgLoadingTask extends AsyncTaskCompat<Void, Void, Bitmap> {
        private final Context context;
        private final String path;

        @Override
        protected void onPreExecute() {
            imgTasks.add(this);
        }

        public ImgLoadingTask(Context context, @NonNull String path) {
            this.context = context;
            assert (path != null);
            this.path = path;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            try (InputStream is = context.getAssets().open(path)) {
                return BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(@Nullable Bitmap img) {
            CollectingGoodsDemoActivityView v = getView();
            if ((img != null) && !isReleased() && (v != null)) {
                v.onImageLoaded(path, img);
            }
            onCancelled(img);
        }

        @Override
        protected void onCancelled(Bitmap bitmap) {
            imgTasks.remove(this);
        }
    }
}
