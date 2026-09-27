package com.mijuego.aplicacion.casodeuso;

import com.mijuego.aplicacion.puerto.entrada.PuertoMejorarTijera;
import com.mijuego.dominio.modelo.Partida;
import com.mijuego.dominio.modelo.Tijera;

public class MejorarTijeraCasoUso implements PuertoMejorarTijera {

    @Override
    public boolean ejecutar(Partida partida) {
        Tijera tijera = partida.obtenerTijera();
        int costo = tijera.obtenerCostoMejora();

        if (!partida.gastarMonedas(costo)) {
            return false;
        }

        tijera.mejorar();
        return true;
    }
}