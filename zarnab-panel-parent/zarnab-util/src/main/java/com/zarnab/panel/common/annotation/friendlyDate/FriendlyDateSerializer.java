package com.zarnab.panel.common.annotation.friendlyDate;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.zarnab.panel.common.translate.LanguageUtil;
import com.zarnab.panel.common.util.DateUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import static com.zarnab.panel.common.constants.ConfigConstants.*;


@Component
public class FriendlyDateSerializer extends JsonSerializer<TemporalAccessor> {

    @Override
    public void serialize(TemporalAccessor value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        boolean headerIsJalali = LanguageUtil.isJalali();

        Field field = getCurrentField(gen);
        FriendlyDate friendlyDate = field.getAnnotation(FriendlyDate.class);

        boolean useJalali = friendlyDate.type().equals(FriendlyDateType.Jalali) || headerIsJalali;
        boolean includeTime = friendlyDate.includeTime();
        String timeZone = friendlyDate.timeZone();
        String pattern = friendlyDate.includeTime()
                ? friendlyDate.pattern()
                : DateUtil.removeTimePattern(friendlyDate.pattern());
        String dateFieldName = gen.getOutputContext().getCurrentName();
        String friendlyFieldName = friendlyDate.fieldName().isEmpty()
                ? dateFieldName + FRIENDLY_DATE_KEY_POSTFIX : friendlyDate.fieldName();

        String formattedDate = useJalali
                ? convertToJalali(value, pattern, includeTime, timeZone)
                : convertToGregorian(value, pattern, includeTime, timeZone);

        gen.writeObject(value);
        gen.writeFieldName(friendlyFieldName);
        gen.writeString(formattedDate);
    }

    private String convertToGregorian(
            TemporalAccessor value,
            String pattern,
            boolean includeTime,
            String timeZone
    ) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime = toLocalDateTime(value, timeZone);

        return includeTime
                ? localDateTime.format(formatter)
                : localDateTime.toLocalDate().format(formatter);
    }

    private String convertToJalali(
            TemporalAccessor value,
            String pattern,
            boolean includeTime,
            String timeZone
    ) {
        LocalDateTime localDateTime = toLocalDateTime(value, timeZone);
        return includeTime
                ? DateUtil.toJalali(localDateTime, pattern)
                : DateUtil.toJalali(localDateTime.toLocalDate(), pattern);
    }

    private LocalDateTime toLocalDateTime(TemporalAccessor value, String timeZone) {
        if (value instanceof Instant) {
            return ((Instant) value).atZone(ZoneId.of(timeZone)).toLocalDateTime();
        } else if (value instanceof LocalDate) {
            return ((LocalDate) value).atStartOfDay();
        } else if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }
        throw new IllegalArgumentException("Unsupported date type: " + value.getClass());
    }

    private Field getCurrentField(JsonGenerator gen) {
        try {
            return gen.getOutputContext().getCurrentValue()
                    .getClass()
                    .getDeclaredField(gen.getOutputContext().getCurrentName());
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Field not found for serialization", e);
        }
    }
}