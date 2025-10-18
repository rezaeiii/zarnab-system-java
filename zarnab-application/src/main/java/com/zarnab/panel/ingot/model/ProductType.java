package com.zarnab.panel.ingot.model;

import lombok.Getter;

@Getter
public enum ProductType {
    /**
     * 200 Gram Gold Bar
     * (شمش 200 گرمی)
     */
    GOLD_200G("C"),

    /**
     * 100 Gram Gold Bar
     * (شمش 100 گرمی)
     */
    GOLD_100G("E"),

    /**
     * 50 Gram Gold Bar
     * (شمش 50 گرمی)
     */
    GOLD_50G("G"),

    /**
     * 20 Gram Gold Bar
     * (شمش 20 گرمی)
     */
    GOLD_20G("I"),

    /**
     * 10 Gram Gold Bar
     * (شمش 10 گرمی)
     */
    GOLD_10G("K"),

    /**
     * 5 Gram Gold Bar
     * (شمش 5 گرمی)
     */
    GOLD_5G("M"),

    /**
     * 2.5 Gram Gold Bar
     * (شمش 2.5 گرمی)
     */
    GOLD_2_5G("O"),

    /**
     * 1 Gram Gold Bar
     * (شمش 1 گرمی)
     */
    GOLD_1G("Q"),

    /**
     * Full Gold Coin
     * (سکه تمام)
     */
    COIN_FULL("Z"),

    /**
     * Half Gold Coin
     * (نیم سکه)
     */
    COIN_HALF("Y"),

    /**
     * Quarter Gold Coin
     * (ربع سکه)
     */
    COIN_QUARTER("X"),

    /**
     * Grammy Gold Coin
     * (سکه گرمی)
     */
    COIN_GRAMMY("W");

    private final String code;

    ProductType(String code) {
        this.code = code;
    }

}
