package com.example.asistencia;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.asistencia.util.CodigoGenerator;

/**
 * Pruebas unitarias para la generación de código de sesión (RF-04).
 */
public class CodigoGeneratorTest {

    @Test
    public void testGenerarCodigoFormatoValido() {
        for (int i = 0; i < 100; i++) {
            String codigo = CodigoGenerator.generarCodigo();
            assertNotNull("El código no debe ser nulo", codigo);
            assertEquals("El código debe tener exactamente 4 dígitos", 4, codigo.length());
            assertTrue("El código debe ser puramente numérico", codigo.matches("\\d{4}"));

            int valorNumerico = Integer.parseInt(codigo);
            assertTrue("El código debe estar entre 1000 y 9999", valorNumerico >= 1000 && valorNumerico <= 9999);
        }
    }
}
