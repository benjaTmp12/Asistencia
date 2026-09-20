package com.example.asistencia.util;

import java.util.Locale;
import java.util.Random;

/**
 * Utilidad para la generación de código de sesión de 4 dígitos (RF-04).
 */
public final class CodigoGenerator {

    private static final Random RANDOM = new Random();

    private CodigoGenerator() {
        // Evita instanciación
    }

    /**
     * Genera un código numérico aleatorio de 4 dígitos entre 1000 y 9999.
     *
     * @return String de 4 dígitos (ej: "8392")
     */
    public static String generarCodigo() {
        int codigo = 1000 + RANDOM.nextInt(9000);
        return String.format(Locale.US, "%04d", codigo);
    }
}
