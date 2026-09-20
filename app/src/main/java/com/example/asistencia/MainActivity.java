package com.example.asistencia;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asistencia.databinding.ActivityMainBinding;
import com.example.asistencia.ui.principal.PrincipalActivity;

/**
 * Punto de entrada de la app.
 * Su única responsabilidad es arrancar y redirigir a PrincipalActivity.
 * No contiene lógica de negocio.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Redirige inmediatamente a la pantalla principal real
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }
}