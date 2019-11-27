package com.alperez.samples.demoactivity;

import com.alperez.samples.collectgoods.model.PricedGoodEntity;

import java.util.List;

/**
 * Created by stanislav.perchenko on 11/27/2019, 4:13 PM.
 */
public interface CollectingGoodsDemoActivityView {

    void onGoodsLoaded(List<PricedGoodEntity> goodCategs);

}
