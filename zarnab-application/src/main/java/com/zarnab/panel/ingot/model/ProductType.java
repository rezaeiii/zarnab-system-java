package com.zarnab.panel.ingot.model;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum ProductType {
    /** 200 Gram Gold Bar */
    GOLD_200G("C", "شمش طلا ۲۰۰ گرمی"),

    /** 100 Gram Gold Bar */
    GOLD_100G("E", "شمش طلا ۱۰۰ گرمی"),

    /** 50 Gram Gold Bar */
    GOLD_50G("G", "شمش طلا ۵۰ گرمی"),

    /** 20 Gram Gold Bar */
    GOLD_20G("I", "شمش طلا ۲۰ گرمی"),

    /** 10 Gram Gold Bar */
    GOLD_10G("K", "شمش طلا ۱۰ گرمی"),

    /** 5 Gram Gold Bar */
    GOLD_5G("M", "شمش طلا ۵ گرمی"),

    /** 2.5 Gram Gold Bar */
    GOLD_2_5G("O", "شمش طلا ۲.۵ گرمی"),

    /** 1 Gram Gold Bar */
    GOLD_1G("Q", "شمش طلا ۱ گرمی"),

    /** Full Gold Coin */
    COIN_FULL("Z", "سکه تمام بهار آزادی"),

    /** Half Gold Coin */
    COIN_HALF("Y", "نیم سکه بهار آزادی"),

    /** Quarter Gold Coin */
    COIN_QUARTER("X", "ربع سکه بهار آزادی"),

    /** Grammy Gold Coin */
    COIN_GRAMMY("W", "سکه گرمی"),

    /** 100 Gram Silver Bar */
    SILVER_100G("R", "شمش نقره ۱۰۰ گرمی"),

    /** 50 Gram Silver Bar */
    SILVER_50G("S", "شمش نقره ۵۰ گرمی"),

    /** 20 Gram Silver Bar */
    SILVER_20G("T", "شمش نقره ۲۰ گرمی"),

    /** 1 Ounce Silver Bar */
    SILVER_31G("U", "شمش نقره یک اونس");

    private final String code;
    private final String persianName;

    ProductType(String code, String persianName) {
        this.code = code;
        this.persianName = persianName;
    }
    private static final Map<String, ProductType> CODE_MAP = new HashMap<>();

    static {
        for (ProductType type : values()) {
            CODE_MAP.put(type.code, type);
        }
    }

    public static ProductType fromCode(String serial) {
        if (serial == null || serial.isEmpty()) {
            throw new IllegalArgumentException("Invalid serial");
        }
        String code = String.valueOf(serial.charAt(0));
        ProductType type = CODE_MAP.get(code.toUpperCase());
        if (type == null) {
            throw new IllegalArgumentException("Unknown ProductType code: " + code);
        }
        return type;
    }

}





