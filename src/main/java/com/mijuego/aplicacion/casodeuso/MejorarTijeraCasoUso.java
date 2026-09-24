package com.mijuego.aplicacion.casodeuso;

import com.mijuego.dominio.modelo.Tijera;

public class MejorarTijeraCasoUso {

    // Retorna true si se pudo mejorar, false si no alcanzaban las monedas
    public boolean ejecutar(Tijera tijera, double monedasActuales) {
        double costo = tijera.obtenerCostoMejora();

        if (monedasActuales >= costo) {
            tijera.mejorar();
            return true;
        }

        return false;
    }
}