package com.mijuego.aplicacion.casodeuso;

import com.mijuego.dominio.modelo.Papel;
import com.mijuego.dominio.modelo.Tijera;
import com.mijuego.dominio.servicio.ServicioCorte;

public class CortarPapelCasoUso {

    private final ServicioCorte servicioCorte;

    public CortarPapelCasoUso(ServicioCorte servicioCorte) {
        this.servicioCorte = servicioCorte;
    }

    // Retorna la cantidad de monedas ganadas en este corte
    public double ejecutar(Tijera tijera, Papel papel) {
        servicioCorte.ejecutarCorte(tijera, papel);

        if (papel.estaDestruido()) {
            papel.reiniciar();
            return 10.0; // Recompensa de 10 monedas por destruir una hoja
        }

        return 0.0; // Si no la destruyó, aún no gana monedas
    }
}