package com.mijuego.dominio.servicio;

import com.mijuego.dominio.modelo.Papel;
import com.mijuego.dominio.modelo.Tijera;

public class ServicioCorte {

    // REGLA DE NEGOCIO: La tijera le hace daño al papel
    public void ejecutarCorte(Tijera tijera, Papel papel) {
        if (papel.estaDestruido()) {
            return; // Si el papel ya se rompió, no le hace más daño
        }

        double danio = tijera.obtenerPoderCorte();
        papel.recibirDanio(danio);
    }
}