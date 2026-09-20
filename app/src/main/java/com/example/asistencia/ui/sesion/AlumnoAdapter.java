package com.example.asistencia.ui.sesion;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asistencia.data.model.Alumno;
import com.example.asistencia.databinding.ItemAlumnoBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para el RecyclerView que lista los alumnos registrados en tiempo real (RF-07).
 */
public class AlumnoAdapter extends RecyclerView.Adapter<AlumnoAdapter.AlumnoViewHolder> {

    private final List<Alumno> lista = new ArrayList<>();

    public void setLista(List<Alumno> nuevaLista) {
        this.lista.clear();
        if (nuevaLista != null) {
            this.lista.addAll(nuevaLista);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlumnoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAlumnoBinding binding = ItemAlumnoBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new AlumnoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AlumnoViewHolder holder, int position) {
        Alumno alumno = lista.get(position);
        holder.bind(alumno, position + 1);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class AlumnoViewHolder extends RecyclerView.ViewHolder {

        private final ItemAlumnoBinding binding;

        public AlumnoViewHolder(ItemAlumnoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Alumno alumno, int numeroOrden) {
            String nombre = alumno.getNombre() != null ? alumno.getNombre() : "Alumno sin nombre";
            binding.tvNombreAlumno.setText(nombre);

            String hora = alumno.getHoraRegistro() != null ? alumno.getHoraRegistro() : "";
            // Si la hora es un ISO string como "2026-09-20T17:40:00Z", extraer solo la hora HH:mm si es posible
            if (hora.contains("T") && hora.length() >= 16) {
                String soloHora = hora.substring(hora.indexOf("T") + 1, hora.indexOf("T") + 6);
                binding.tvHoraRegistro.setText("Registrado a las " + soloHora);
            } else if (!hora.isEmpty()) {
                binding.tvHoraRegistro.setText("Hora: " + hora);
            } else {
                binding.tvHoraRegistro.setText("Registrado recientemente");
            }

            binding.tvNumeroOrden.setText("#" + numeroOrden);
        }
    }
}
