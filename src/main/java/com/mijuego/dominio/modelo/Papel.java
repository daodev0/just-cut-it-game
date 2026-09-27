package com.mijuego.dominio.modelo;

public class Papel {
    private double vidaActual;
    private final double vidaMaxima;

    public Papel(double vidaMaxima) {
        this.vidaMaxima = vidaMaxima;
        this.vidaActual = vidaMaxima;
    }

    public void recibirDanio(double cantidad) {
        this.vidaActual = Math.max(0, this.vidaActual - cantidad);
    }

    public boolean estaDestruido() {
        return this.vidaActual <= 0;
    }

    public void reiniciar() {
        this.vidaActual = this.vidaMaxima;
    }

    public double obtenerVidaActual() {
        return vidaActual;
    }

    public double obtenerVidaMaxima() {
        return vidaMaxima;
    }
}