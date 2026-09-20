package com.example.asistencia.data.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Modelo de datos Alumno según el contrato de datos RTDB:
 * {
 *   "nombre": string,
 *   "horaRegistro": string ISO 8601
 * }
 */
@IgnoreExtraProperties
public class Alumno {

    private String idAlumno;
    private String nombre;
    private String horaRegistro;

    /**
     * Constructor por defecto requerido por Firebase para deserialización.
     */
    public Alumno() {
    }

    public Alumno(String nombre, String horaRegistro) {
        this.nombre = nombre;
        this.horaRegistro = horaRegistro;
    }

    public Alumno(String idAlumno, String nombre, String horaRegistro) {
        this.idAlumno = idAlumno;
        this.nombre = nombre;
        this.horaRegistro = horaRegistro;
    }

    @Exclude
    public String getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(String idAlumno) {
        this.idAlumno = idAlumno;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getHoraRegistro() {
        return horaRegistro;
    }

    public void setHoraRegistro(String horaRegistro) {
        this.horaRegistro = horaRegistro;
    }
}
