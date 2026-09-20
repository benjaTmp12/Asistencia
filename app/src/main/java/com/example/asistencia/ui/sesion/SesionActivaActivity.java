package com.example.asistencia.ui.sesion;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.asistencia.R;
import com.example.asistencia.databinding.ActivitySesionActivaBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * P-PF03 — Pantalla de Sesión Activa del Docente.
 * Muestra el código de 4 dígitos (RF-06), el listado en tiempo real de alumnos (RF-07),
 * el contador en vivo (RF-08) y permite finalizar la sesión (RF-09).
 */
public class SesionActivaActivity extends AppCompatActivity {

    public static final String EXTRA_ID_SESION = "extra_id_sesion";
    public static final String EXTRA_CURSO = "extra_curso";
    public static final String EXTRA_CODIGO = "extra_codigo";

    private ActivitySesionActivaBinding binding;
    private SesionActivaViewModel viewModel;
    private AlumnoAdapter adapter;

    private String idSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySesionActivaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        idSesion = getIntent().getStringExtra(EXTRA_ID_SESION);
        String cursoInicial = getIntent().getStringExtra(EXTRA_CURSO);
        String codigoInicial = getIntent().getStringExtra(EXTRA_CODIGO);

        viewModel = new ViewModelProvider(this).get(SesionActivaViewModel.class);

        configurarVistas(cursoInicial, codigoInicial);
        configurarRecyclerView();
        configurarEventos();
        observarViewModel();

        // Iniciar la escucha en tiempo real con Firebase (RF-07, RF-08)
        if (idSesion != null) {
            viewModel.iniciarObservacion(idSesion);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (idSesion != null) {
            viewModel.iniciarObservacion(idSesion);
        }
    }

    private void configurarVistas(String cursoInicial, String codigoInicial) {
        if (cursoInicial != null) {
            binding.tvCursoHeader.setText(cursoInicial);
        }
        if (codigoInicial != null) {
            // Formatear el código con espacios para mayor legibilidad (ej. "8 3 9 2")
            binding.tvCodigoGigante.setText(formatearCodigo(codigoInicial));
        }

        // Manejar el botón Back del sistema para solicitar confirmación de cierre
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                mostrarDialogoConfirmacionCierre();
            }
        });
    }

    private void configurarRecyclerView() {
        adapter = new AlumnoAdapter();
        binding.rvAlumnos.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAlumnos.setAdapter(adapter);
    }

    private void configurarEventos() {
        binding.btnFinalizarSesion.setOnClickListener(v -> mostrarDialogoConfirmacionCierre());
    }

    private void observarViewModel() {
        // Observar datos actualizados de la sesión
        viewModel.getSesion().observe(this, sesion -> {
            if (sesion != null) {
                if (sesion.getCurso() != null) {
                    binding.tvCursoHeader.setText(sesion.getCurso());
                }
                if (sesion.getCodigo() != null) {
                    binding.tvCodigoGigante.setText(formatearCodigo(sesion.getCodigo()));
                }
            }
        });

        // Observar listado en tiempo real de alumnos (RF-07)
        viewModel.getAlumnos().observe(this, listaAlumnos -> {
            adapter.setLista(listaAlumnos);
            boolean estaVacia = listaAlumnos == null || listaAlumnos.isEmpty();
            binding.layoutEstadoVacio.setVisibility(estaVacia ? View.VISIBLE : View.GONE);
            binding.rvAlumnos.setVisibility(estaVacia ? View.GONE : View.VISIBLE);
        });

        // Observar contador de asistentes en vivo (RF-08)
        viewModel.getCantidadAlumnos().observe(this, cantidad -> {
            int total = cantidad != null ? cantidad : 0;
            String textoContador = total == 1 ? "1 alumno" : total + " alumnos";
            binding.tvContadorAlumnos.setText(textoContador);
        });

        // Estado de carga / cierre
        viewModel.getCargando().observe(this, cargando -> {
            boolean estaCargando = Boolean.TRUE.equals(cargando);
            binding.progressBarSesion.setVisibility(estaCargando ? View.VISIBLE : View.GONE);
            binding.btnFinalizarSesion.setEnabled(!estaCargando);
        });

        // Sesión cerrada exitosamente (RF-09) -> Volver a PrincipalActivity
        viewModel.getSesionCerrada().observe(this, cerrada -> {
            if (Boolean.TRUE.equals(cerrada)) {
                Toast.makeText(this, R.string.sesion_finalizada_exito, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Errores
        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarDialogoConfirmacionCierre() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_cerrar_titulo)
                .setMessage(R.string.dialog_cerrar_mensaje)
                .setPositiveButton(R.string.dialog_btn_confirmar, (dialog, which) -> {
                    viewModel.cerrarSesion(); // RF-09
                })
                .setNegativeButton(R.string.dialog_btn_cancelar, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private String formatearCodigo(String codigo) {
        if (codigo == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < codigo.length(); i++) {
            sb.append(codigo.charAt(i));
            if (i < codigo.length() - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
}
