package com.zarnab.panel.ingot.dto.res;

import com.zarnab.panel.ingot.model.Ingot;
import com.zarnab.panel.ingot.model.IngotState;

public record BatchIngotResponse(
        Long ingotId,
        String serial,
        IngotState state,
        boolean transferred
        ) {

    public static BatchIngotResponse from(Ingot ingot, boolean transferred) {
        return new BatchIngotResponse(ingot.getId(), ingot.getSerial(), ingot.getState(), transferred);
    }

    public boolean isAssigned() {
        return state == IngotState.ASSIGNED;
    }
}
