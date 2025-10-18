package com.zarnab.panel.ingot.controller;

import com.zarnab.panel.ingot.dto.res.EnumDto;
import com.zarnab.panel.ingot.model.ProductType;
import com.zarnab.panel.ingot.model.Purity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ingots/meta")
public class MetaController {

    @GetMapping("/product-types")
    public List<EnumDto> getProductTypes() {
        return Arrays.stream(ProductType.values())
                .map(pt -> new EnumDto(pt.name(), pt.getCode(), getProductTypePersianTitle(pt)))
                .collect(Collectors.toList());
    }

    @GetMapping("/purities")
    public List<EnumDto> getPurities() {
        return Arrays.stream(Purity.values())
                .map(p -> new EnumDto(p.name(), p.getCode(), getPurityPersianTitle(p)))
                .collect(Collectors.toList());
    }

    private String getProductTypePersianTitle(ProductType pt) {
        return switch (pt) {
            case GOLD_200G -> "شمش 200 گرمی";
            case GOLD_100G -> "شمش 100 گرمی";
            case GOLD_50G -> "شمش 50 گرمی";
            case GOLD_20G -> "شمش 20 گرمی";
            case GOLD_10G -> "شمش 10 گرمی";
            case GOLD_5G -> "شمش 5 گرمی";
            case GOLD_2_5G -> "شمش 2.5 گرمی";
            case GOLD_1G -> "شمش 1 گرمی";
            case COIN_FULL -> "سکه تمام";
            case COIN_HALF -> "نیم سکه";
            case COIN_QUARTER -> "ربع سکه";
            case COIN_GRAMMY -> "سکه گرمی";
            default -> "";
        };
    }

    private String getPurityPersianTitle(Purity p) {
        switch (p) {
            case P995: return "عیار 995";
            case P750: return "عیار 750";
            default: return "";
        }
    }
}
