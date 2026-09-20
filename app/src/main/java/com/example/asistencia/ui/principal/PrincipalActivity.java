package com.example.asistencia.ui.principal;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asistencia.databinding.ActivityPrincipalBinding;

/**
 * P-PF01 — Pantalla principal del Profesor.
 * Stub H1: sólo infla el layout para verificar que ViewBinding compila.
 * Implementación completa en H4.
 */
public class PrincipalActivity extends AppCompatActivity {

    private ActivityPrincipalBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrincipalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
