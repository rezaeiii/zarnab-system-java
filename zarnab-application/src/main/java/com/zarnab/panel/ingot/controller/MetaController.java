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
        return List.of(new EnumDto("GOLD", "1", "شمش"),
                new EnumDto("COIN", "2", "سکه"),
                new EnumDto("SILVER", "3", "نقره"));
    }

    @GetMapping("/purities")
    public List<EnumDto> getPurities() {
        return Arrays.stream(Purity.values())
                .map(p -> new EnumDto(p.name(), p.getCode(), p.getFriendlyName()))
                .collect(Collectors.toList());
    }

}
