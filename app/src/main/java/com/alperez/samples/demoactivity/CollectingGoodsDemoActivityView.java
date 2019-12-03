package com.alperez.samples.demoactivity;

import android.content.Context;
import android.graphics.Bitmap;

import com.alperez.samples.collectgoods.model.PricedGoodEntity;
import com.alperez.samples.collectgoods.model.UserEntity;

import java.util.List;

/**
 * Created by stanislav.perchenko on 11/27/2019, 4:13 PM.
 */
public interface CollectingGoodsDemoActivityView {

    Context getContext();

    void onGoodsLoaded(List<PricedGoodEntity> goodCategs);

    void onUserLoaded(UserEntity user);

    void onImageLoaded(String path, Bitmap img);

}
