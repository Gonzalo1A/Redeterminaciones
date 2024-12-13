
package com.redet.redeterminacion.utilidades;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FormatearDecimal {

    public static Double dosDecimales(Double valor) {
        BigDecimal bd = new BigDecimal(Double.toString(valor));
        bd = bd.setScale(2, RoundingMode.DOWN);
        return bd.doubleValue();
    }

    public static Double cuatroDecimales(Double valor) {
        BigDecimal bd = new BigDecimal(Double.toString(valor));
        bd = bd.setScale(4, RoundingMode.DOWN);  
        return bd.doubleValue();
    }
    
    public static Double porcentajes(Double valor) {
        BigDecimal bd = new BigDecimal(Double.toString(valor));
        bd = bd.setScale(2, RoundingMode.UP);  
        return bd.doubleValue();
    }
}
