package com.zarnab.panel.ingot.dto.res;

import com.zarnab.panel.ingot.model.Ingot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngotWithChangeableDto {
    private Ingot ingot;
    private boolean changeable;
}
