package com.example.asistencia.ui.sesion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.asistencia.data.model.Alumno;
import com.example.asistencia.data.model.Sesion;
import com.example.asistencia.data.repository.SesionRepository;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel para la pantalla de Sesión Activa del Docente (P-PF03).
 * Gestiona la escucha en tiempo real de alumnos asistentes (RF-07),
 * el contador en vivo (RF-08) y el cierre de la sesión (RF-09).
 */
public class SesionActivaViewModel extends ViewModel {

    private final SesionRepository repository;

    private String idSesionActual;
    private ValueEventListener listenerFirebase;

    private final MutableLiveData<Sesion> sesion = new MutableLiveData<>(null);
    private final MutableLiveData<List<Alumno>> alumnos = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Integer> cantidadAlumnos = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> cargando = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> sesionCerrada = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);

    public SesionActivaViewModel() {
        this.repository = new SesionRepository();
    }

    public SesionActivaViewModel(SesionRepository repository) {
        this.repository = repository;
    }

    public LiveData<Sesion> getSesion() {
        return sesion;
    }

    public LiveData<List<Alumno>> getAlumnos() {
        return alumnos;
    }

    public LiveData<Integer> getCantidadAlumnos() {
        return cantidadAlumnos;
    }

    public LiveData<Boolean> getCargando() {
        return cargando;
    }

    public LiveData<Boolean> getSesionCerrada() {
        return sesionCerrada;
    }

    public LiveData<String> getError() {
        return error;
    }

    /**
     * Inicia la observación en tiempo real de la sesión y sus asistentes (RF-07, RF-08).
     *
     * @param idSesion Identificador único de la sesión
     */
    public void iniciarObservacion(String idSesion) {
        if (idSesion == null || idSesion.trim().isEmpty()) {
            error.setValue("El ID de sesión no es válido.");
            cargando.setValue(false);
            return;
        }

        // Si ya había un observador para otra sesión, limpiarlo
        limpiarObservador();

        this.idSesionActual = idSesion;
        this.cargando.setValue(true);

        this.listenerFirebase = repository.observarSesion(idSesion, new SesionRepository.ObservarSesionListener() {
            @Override
            public void onSesionActualizada(Sesion sesionActualizada, List<Alumno> listaAlumnos) {
                cargando.setValue(false);
                sesion.setValue(sesionActualizada);
                alumnos.setValue(listaAlumnos != null ? listaAlumnos : new ArrayList<>());
                cantidadAlumnos.setValue(listaAlumnos != null ? listaAlumnos.size() : 0);
            }

            @Override
            public void onError(String mensajeError) {
                cargando.setValue(false);
                error.setValue(mensajeError);
            }
        });
    }

    /**
     * Cierra la sesión activa actualizando activa = false (RF-09).
     */
    public void cerrarSesion() {
        if (idSesionActual == null || idSesionActual.trim().isEmpty()) {
            error.setValue("No hay una sesión activa para cerrar.");
            return;
        }

        cargando.setValue(true);

        repository.cerrarSesion(idSesionActual, new SesionRepository.CerrarSesionCallback() {
            @Override
            public void onSuccess() {
                cargando.setValue(false);
                sesionCerrada.setValue(true);
            }

            @Override
            public void onError(String mensajeError) {
                cargando.setValue(false);
                error.setValue(mensajeError);
            }
        });
    }

    private void limpiarObservador() {
        if (idSesionActual != null && listenerFirebase != null) {
            repository.removerObservador(idSesionActual, listenerFirebase);
            listenerFirebase = null;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        limpiarObservador();
    }
}
