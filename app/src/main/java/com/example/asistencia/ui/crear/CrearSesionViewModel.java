package com.example.asistencia.ui.crear;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.asistencia.data.model.Sesion;
import com.example.asistencia.data.repository.SesionRepository;

/**
 * ViewModel para la pantalla de Crear Sesión (P-PF02).
 * Gestiona la validación del curso (RF-03) y el estado de la operación de creación.
 */
public class CrearSesionViewModel extends ViewModel {

    private final SesionRepository repository;

    private final MutableLiveData<Boolean> cargando = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorValidacionCurso = new MutableLiveData<>(null);
    private final MutableLiveData<String> errorFirebase = new MutableLiveData<>(null);
    private final MutableLiveData<Sesion> sesionCreada = new MutableLiveData<>(null);

    public CrearSesionViewModel() {
        this.repository = new SesionRepository();
    }

    public CrearSesionViewModel(SesionRepository repository) {
        this.repository = repository;
    }

    public LiveData<Boolean> getCargando() {
        return cargando;
    }

    public LiveData<String> getErrorValidacionCurso() {
        return errorValidacionCurso;
    }

    public LiveData<String> getErrorFirebase() {
        return errorFirebase;
    }

    public LiveData<Sesion> getSesionCreada() {
        return sesionCreada;
    }

    /**
     * Valida y crea una nueva sesión docente (RF-03, RF-04, RF-05).
     *
     * @param curso Texto ingresado por el profesor
     */
    public void crearSesion(String curso) {
        // Limpiar errores previos
        errorValidacionCurso.setValue(null);
        errorFirebase.setValue(null);

        // Validación estricta según RF-03 (3 a 60 caracteres)
        if (curso == null || curso.trim().length() < 3 || curso.trim().length() > 60) {
            errorValidacionCurso.setValue("El nombre del curso debe tener entre 3 y 60 caracteres.");
            return;
        }

        cargando.setValue(true);

        repository.crearSesion(curso.trim(), new SesionRepository.CrearSesionCallback() {
            @Override
            public void onSuccess(Sesion sesion) {
                cargando.setValue(false);
                sesionCreada.setValue(sesion);
            }

            @Override
            public void onError(String mensajeError) {
                cargando.setValue(false);
                errorFirebase.setValue(mensajeError);
            }
        });
    }
}
