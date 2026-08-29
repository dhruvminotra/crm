package com.veyora.crm.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/** Small helpers for the CSV columns used by restrictions/applicable days. */
public final class CsvUtil {

    private CsvUtil() {
    }

    public static String join(Collection<?> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    public static List<String> split(String csv) {
        if (csv == null || csv.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(csv.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }

    public static List<Long> splitLongs(String csv) {
        return split(csv).stream().map(Long::valueOf).toList();
    }
}
