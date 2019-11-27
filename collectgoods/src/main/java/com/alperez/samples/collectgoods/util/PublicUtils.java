package com.alperez.samples.collectgoods.util;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by stanislav.perchenko on 11/15/2019, 10:32 AM.
 */
public final class PublicUtils {


    private PublicUtils() {
        throw new IllegalStateException("No instances allowed");
    }

    public static String formatPrice(long amount, int scale, boolean formatAsCurrency, Locale locale) {

        double value = amount;
        for (int i=0; i<scale; i++) value /= 10;

        if (formatAsCurrency) {
            return NumberFormat.getCurrencyInstance(locale).format(value);
        } else {
            return String.format("%."+scale+"f", value);
        }
    }

    public static double buildPriceValue(long amount, int scale) {
        double value = amount;
        for (int i=0; i<scale; i++) value /= 10;
        return value;
    }
}
