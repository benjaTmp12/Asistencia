package com.example.asistencia.data.repository;

import androidx.annotation.NonNull;

import com.example.asistencia.data.model.Alumno;
import com.example.asistencia.data.model.Sesion;
import com.example.asistencia.util.CodigoGenerator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de la capa DATA responsable de las operaciones con Firebase Realtime Database
 * para la aplicación del Profesor.
 *
 * Sigue el patrón Repository aislando a los ViewModels de la API de Firebase.
 */
public class SesionRepository {

    private static final String NODO_SESIONES = "sesiones";
    private static final String DEFAULT_DATABASE_URL = "https://asistencia-37735-default-rtdb.firebaseio.com";

    private final DatabaseReference sesionesRef;

    // Interfaces de Callback para comunicación asíncrona
    public interface CrearSesionCallback {
        void onSuccess(Sesion sesion);
        void onError(String mensajeError);
    }

    public interface CerrarSesionCallback {
        void onSuccess();
        void onError(String mensajeError);
    }

    public interface ObservarSesionListener {
        void onSesionActualizada(Sesion sesion, List<Alumno> alumnos);
        void onError(String mensajeError);
    }

    public SesionRepository() {
        FirebaseDatabase db;
        try {
            db = FirebaseDatabase.getInstance();
        } catch (Exception e) {
            db = FirebaseDatabase.getInstance(DEFAULT_DATABASE_URL);
        }
        this.sesionesRef = db.getReference(NODO_SESIONES);
    }

    public SesionRepository(DatabaseReference sesionesRef) {
        this.sesionesRef = sesionesRef;
    }

    /**
     * Crea una nueva sesión en Firebase RTDB (RF-03, RF-04, RF-05).
     *
     * @param curso Nombre del curso (entre 3 y 60 caracteres)
     * @param callback Retorno de éxito o error
     */
    public void crearSesion(String curso, CrearSesionCallback callback) {
        if (curso == null || curso.trim().length() < 3 || curso.trim().length() > 60) {
            if (callback != null) {
                callback.onError("El nombre del curso debe tener entre 3 y 60 caracteres.");
            }
            return;
        }

        DatabaseReference nuevaSesionRef = sesionesRef.push();
        String idSesion = nuevaSesionRef.getKey();
        String codigo = CodigoGenerator.generarCodigo();
        String creadoEn = DateTimeFormatter.ISO_INSTANT.format(Instant.now());

        Sesion nuevaSesion = new Sesion(idSesion, curso.trim(), codigo, creadoEn, true);

        nuevaSesionRef.setValue(nuevaSesion)
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) {
                        callback.onSuccess(nuevaSesion);
                    }
                })
                .addOnFailureListener(e -> {
                    if (callback != null) {
                        callback.onError(e.getMessage() != null ? e.getMessage() : "Error al crear la sesión en Firebase.");
                    }
                });
    }

    /**
     * Cierra una sesión activa marcando activa = false (RF-09).
     *
     * @param idSesion Identificador único de la sesión
     * @param callback Retorno de éxito o error
     */
    public void cerrarSesion(String idSesion, CerrarSesionCallback callback) {
        if (idSesion == null || idSesion.trim().isEmpty()) {
            if (callback != null) {
                callback.onError("Identificador de sesión inválido.");
            }
            return;
        }

        sesionesRef.child(idSesion).child("activa").setValue(false)
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    if (callback != null) {
                        callback.onError(e.getMessage() != null ? e.getMessage() : "Error al cerrar la sesión.");
                    }
                });
    }

    /**
     * Observa en tiempo real una sesión y su listado de alumnos (RF-07, RF-08).
     *
     * @param idSesion Identificador de la sesión
     * @param listener Callback invocado ante cada cambio en tiempo real
     * @return El ValueEventListener registrado (para poder removerlo en onCleared)
     */
    public ValueEventListener observarSesion(String idSesion, ObservarSesionListener listener) {
        if (idSesion == null || idSesion.trim().isEmpty()) {
            if (listener != null) {
                listener.onError("Identificador de sesión inválido.");
            }
            return null;
        }

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    if (listener != null) {
                        listener.onError("La sesión no existe.");
                    }
                    return;
                }

                String curso = snapshot.child("curso").getValue(String.class);
                String codigo = snapshot.child("codigo").getValue(String.class);
                Boolean activa = snapshot.child("activa").getValue(Boolean.class);
                String creadoEn = snapshot.child("creadoEn").getValue(String.class);
                String idKey = snapshot.getKey();

                Sesion sesion = new Sesion(idKey, curso != null ? curso : "", codigo != null ? codigo : "", creadoEn, activa == null || activa);

                List<Alumno> listaAlumnos = new ArrayList<>();
                DataSnapshot alumnosSnapshot = snapshot.child("alumnos");
                if (alumnosSnapshot.exists()) {
                    for (DataSnapshot alumnoSnap : alumnosSnapshot.getChildren()) {
                        String idAlumno = alumnoSnap.getKey();
                        String nombre = alumnoSnap.child("nombre").getValue(String.class);
                        String horaRegistro = alumnoSnap.child("horaRegistro").getValue(String.class);
                        if (nombre != null && !nombre.isEmpty()) {
                            listaAlumnos.add(new Alumno(idAlumno, nombre, horaRegistro != null ? horaRegistro : ""));
                        }
                    }
                }

                android.util.Log.d("SesionRepository", "Sesión actualizada: " + codigo + ", asistentes=" + listaAlumnos.size());

                if (listener != null) {
                    listener.onSesionActualizada(sesion, listaAlumnos);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (listener != null) {
                    listener.onError(error.getMessage());
                }
            }
        };

        sesionesRef.child(idSesion).addValueEventListener(eventListener);
        return eventListener;
    }

    /**
     * Remueve el observador en tiempo real para evitar fugas de memoria.
     *
     * @param idSesion Identificador de la sesión
     * @param listener Listener a remover
     */
    public void removerObservador(String idSesion, ValueEventListener listener) {
        if (idSesion != null && listener != null) {
            sesionesRef.child(idSesion).removeEventListener(listener);
        }
    }
}
