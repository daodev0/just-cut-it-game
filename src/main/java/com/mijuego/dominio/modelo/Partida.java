package com.mijuego.dominio.modelo;

import java.util.Objects;

public class Partida {
    private final Tijera tijera;
    private final Papel papel;
    private int monedas;

    public Partida(Tijera tijera, Papel papel) {
        this.tijera = Objects.requireNonNull(tijera, "La tijera es obligatoria");
        this.papel = Objects.requireNonNull(papel, "El papel es obligatorio");
    }

    public Tijera obtenerTijera() {
        return tijera;
    }

    public Papel obtenerPapel() {
        return papel;
    }

    public int obtenerMonedas() {
        return monedas;
    }

    public void agregarMonedas(int cantidad) {
        validarCantidad(cantidad);
        monedas += cantidad;
    }

    public boolean gastarMonedas(int cantidad) {
        validarCantidad(cantidad);
        if (monedas < cantidad) {
            return false;
        }
        monedas -= cantidad;
        return true;
    }

    private void validarCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de monedas debe ser positiva");
        }
    }
}
