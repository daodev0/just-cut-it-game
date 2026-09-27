package com.mijuego.aplicacion.casodeuso;

import com.mijuego.aplicacion.puerto.entrada.PuertoCortarPapel;
import com.mijuego.dominio.modelo.Papel;
import com.mijuego.dominio.modelo.Partida;
import com.mijuego.dominio.modelo.Tijera;
import com.mijuego.dominio.servicio.ServicioCorte;
import java.util.Objects;

public class CortarPapelCasoUso implements PuertoCortarPapel {

    private final ServicioCorte servicioCorte;
    private static final int RECOMPENSA_HOJA = 10;

    public CortarPapelCasoUso(ServicioCorte servicioCorte) {
        this.servicioCorte = Objects.requireNonNull(servicioCorte, "El servicio de corte es obligatorio");
    }

    // Retorna la cantidad de monedas ganadas en este corte
    @Override
    public int ejecutar(Partida partida) {
        Tijera tijera = partida.obtenerTijera();
        Papel papel = partida.obtenerPapel();
        servicioCorte.ejecutarCorte(tijera, papel);

        if (papel.estaDestruido()) {
            papel.reiniciar();
            partida.agregarMonedas(RECOMPENSA_HOJA);
            return RECOMPENSA_HOJA;
        }

        return 0; // Si no la destruyó, aún no gana monedas
    }
}