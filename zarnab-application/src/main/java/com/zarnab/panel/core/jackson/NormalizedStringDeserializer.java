package com.zarnab.panel.core.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.zarnab.panel.common.util.CharacterUtil;

import java.io.IOException;

public class NormalizedStringDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        return CharacterUtil.normalizeDigits(value);
    }
}
