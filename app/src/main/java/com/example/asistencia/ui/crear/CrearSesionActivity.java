package com.example.asistencia.ui.crear;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.asistencia.databinding.ActivityCrearSesionBinding;
import com.example.asistencia.ui.sesion.SesionActivaActivity;

/**
 * P-PF02 — Pantalla de Creación de Sesión del Docente.
 * Maneja el ingreso del curso (RF-03), la validación y la llamada al ViewModel.
 */
public class CrearSesionActivity extends AppCompatActivity {

    private ActivityCrearSesionBinding binding;
    private CrearSesionViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCrearSesionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(CrearSesionViewModel.class);

        configurarVistas();
        observarViewModel();
    }

    private void configurarVistas() {
        binding.btnVolver.setOnClickListener(v -> finish());

        binding.btnIniciarSesion.setOnClickListener(v -> {
            String curso = binding.etCurso.getText() != null
                    ? binding.etCurso.getText().toString()
                    : "";
            viewModel.crearSesion(curso);
        });
    }

    private void observarViewModel() {
        // Estado de carga
        viewModel.getCargando().observe(this, cargando -> {
            boolean estaCargando = Boolean.TRUE.equals(cargando);
            binding.progressBarCrear.setVisibility(estaCargando ? View.VISIBLE : View.GONE);
            binding.btnIniciarSesion.setEnabled(!estaCargando);
            binding.etCurso.setEnabled(!estaCargando);
        });

        // Error de validación local (RF-03: 3 a 60 caracteres)
        viewModel.getErrorValidacionCurso().observe(this, error -> {
            binding.tilCurso.setError(error);
        });

        // Error retornado por Firebase
        viewModel.getErrorFirebase().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                binding.tvErrorFirebase.setText(error);
                binding.tvErrorFirebase.setVisibility(View.VISIBLE);
            } else {
                binding.tvErrorFirebase.setVisibility(View.GONE);
            }
        });

        // Éxito en la creación -> Navegar a Sesión Activa (P-PF03)
        viewModel.getSesionCreada().observe(this, sesion -> {
            if (sesion != null) {
                Intent intent = new Intent(CrearSesionActivity.this, SesionActivaActivity.class);
                intent.putExtra(SesionActivaActivity.EXTRA_ID_SESION, sesion.getIdSesion());
                intent.putExtra(SesionActivaActivity.EXTRA_CURSO, sesion.getCurso());
                intent.putExtra(SesionActivaActivity.EXTRA_CODIGO, sesion.getCodigo());
                startActivity(intent);
                finish(); // No volver a la pantalla de crear sesión con 'Back'
            }
        });
    }
}
