package com.example.asistencia.ui.principal;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asistencia.databinding.ActivityPrincipalBinding;
import com.example.asistencia.ui.crear.CrearSesionActivity;

/**
 * P-PF01 — Pantalla Principal del Profesor.
 * Permite iniciar el flujo de creación de una nueva sesión de asistencia.
 */
public class PrincipalActivity extends AppCompatActivity {

    private ActivityPrincipalBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrincipalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configurarEventos();
    }

    private void configurarEventos() {
        binding.btnCrearSesion.setOnClickListener(v -> {
            Intent intent = new Intent(PrincipalActivity.this, CrearSesionActivity.class);
            startActivity(intent);
        });
    }
}
