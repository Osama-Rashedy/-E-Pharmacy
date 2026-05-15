package com.epharmacy.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Shared formatting utilities. */
public class FormatUtil {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a");
    private static final DateTimeFormatter D_FMT  = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String formatDateTime(LocalDateTime dt) {
        return dt != null ? dt.format(DT_FMT) : "-";
    }

    public static String formatDate(java.time.LocalDate d) {
        return d != null ? d.format(D_FMT) : "-";
    }

    public static String formatCurrency(double amount) {
        return String.format("$%.2f", amount);
    }
}
