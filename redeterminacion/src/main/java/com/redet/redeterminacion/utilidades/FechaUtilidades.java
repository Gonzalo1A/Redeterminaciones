
package com.redet.redeterminacion.utilidades;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

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

    public static int cantidadMeses(LocalDate inicioObra, LocalDate actual) {
        int difM = 0;
        if (actual.withDayOfMonth(actual.lengthOfMonth()).isAfter(actual)) {
            int difA = actual.getYear() - inicioObra.getYear();
            difM = difA * 12 + actual.getMonthValue() - inicioObra.getMonthValue();
        } else {
            actual.minusMonths(1);
            int difA = actual.getYear() - inicioObra.getYear();
            difM = difA * 12 + actual.getMonthValue() - inicioObra.getMonthValue();
        }
        return difM;
    }

    public static List<LocalDate> generarListaFechas(LocalDate fechaInicial, int numeroFechas) {
        List<LocalDate> listaFechas = new ArrayList<>();
        for (int i = 0; i < numeroFechas; i++) {
            listaFechas.add(fechaInicial.plusMonths(i));
        }
        return listaFechas;
    }
}
