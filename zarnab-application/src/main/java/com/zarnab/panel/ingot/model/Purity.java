package com.zarnab.panel.ingot.model;

import lombok.Getter;

@Getter
public enum Purity {
    /**
     * 995 Karat
     * (عیار 995)
     */
    P995("A"),

    /**
     * 750 Karat
     * (عیار 750)
     */
    P750("B");

    private final String code;

    Purity(String code) {
        this.code = code;
    }

}
