package com.example.asistencia.data.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Modelo de datos Sesion según el contrato de datos RTDB:
 * {
 *   "activa": boolean,
 *   "codigo": string,
 *   "curso": string,
 *   "creadoEn": string ISO 8601,
 *   "alumnos": { ... }
 * }
 */
@IgnoreExtraProperties
public class Sesion {

    private String idSesion;
    private boolean activa;
    private String codigo;
    private String curso;
    private String creadoEn;
    private Map<String, Alumno> alumnos = new HashMap<>();

    /**
     * Constructor por defecto requerido por Firebase para deserialización.
     */
    public Sesion() {
    }

    public Sesion(String curso, String codigo, String creadoEn) {
        this.curso = curso;
        this.codigo = codigo;
        this.activa = true;
        this.creadoEn = creadoEn;
        this.alumnos = new HashMap<>();
    }

    public Sesion(String idSesion, String curso, String codigo, String creadoEn, boolean activa) {
        this.idSesion = idSesion;
        this.curso = curso;
        this.codigo = codigo;
        this.creadoEn = creadoEn;
        this.activa = activa;
        this.alumnos = new HashMap<>();
    }

    @Exclude
    public String getIdSesion() {
        return idSesion;
    }

    public void setIdSesion(String idSesion) {
        this.idSesion = idSesion;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(String creadoEn) {
        this.creadoEn = creadoEn;
    }

    @Exclude
    public Map<String, Alumno> getAlumnos() {
        return alumnos;
    }

    @Exclude
    public void setAlumnos(Map<String, Alumno> alumnos) {
        this.alumnos = alumnos != null ? alumnos : new HashMap<>();
    }
}
