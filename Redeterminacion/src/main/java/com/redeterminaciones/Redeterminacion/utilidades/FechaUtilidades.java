package com.redeterminaciones.Redeterminacion.utilidades;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FechaUtilidades {

    public static LocalDate convertirStringALocalDate(String fechaStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            return LocalDate.parse(fechaStr, formatter);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static LocalDate convertirStringALocalDateFormato2(String fechaStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        try {
            YearMonth yearMonth = YearMonth.parse(fechaStr, formatter);
            return yearMonth.atDay(1);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
