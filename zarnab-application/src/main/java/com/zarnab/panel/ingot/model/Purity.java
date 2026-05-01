package com.zarnab.panel.ingot.model;

import lombok.Getter;

@Getter
public enum Purity {
    /**
     * 995 Karat
     * (عیار 995)
     */
    P995("A", "عیار 995"),

    /**
     * 750 Karat
     * (عیار 750)
     */
    P750("B", "عیار 750"),

    /**
     * 999 Karat
     * (عیار 999)
     */
    P999("C", "عیار 999");

    private final String code;
    private final String friendlyName;

    Purity(String code, String friendlyName) {
        this.code = code;
        this.friendlyName = friendlyName;
    }

}



