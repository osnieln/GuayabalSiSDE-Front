package cu.edu.unah.util;

import jakarta.validation.constraints.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateFormatter {
    private static final String DATE_PATTERN  = "yyyyMMdd";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN).withZone(ZoneOffset.UTC);

    public static java.sql.Date format(@NotNull String date){
        LocalDate localDate = LocalDate.parse(date, FORMATTER);
        return java.sql.Date.valueOf(localDate);
    }

    public static String format(@NotNull java.sql.Date date){
        LocalDate localDate = date.toLocalDate();
        return localDate.format(FORMATTER);
    }

    public static java.util.Date formatUtil(@NotNull String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        sdf.setLenient(false);
        return sdf.parse(date);
    }

    public static String formatUtil(@NotNull java.util.Date date){
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        return sdf.format(date);
    }
}
