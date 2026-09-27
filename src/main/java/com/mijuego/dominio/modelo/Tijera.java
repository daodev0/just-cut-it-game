package com.mijuego.dominio.modelo;

public class Tijera {
    // ATRIBUTOS (El estado de la tijera)
    private final String nombre;
    private final double poderBase;
    private int nivel;

    // CONSTRUCTOR
    public Tijera(String nombre, double poderBase) {
        this.nombre = nombre;
        this.poderBase = poderBase;
        this.nivel = 1; // Regla de negocio: Empieza en Nivel 1
    }

    // REGLA DE NEGOCIO: El poder aumenta con el nivel
    public double obtenerPoderCorte() {
        return this.poderBase * this.nivel;
    }

    // REGLA DE NEGOCIO: El costo de la mejora aumenta un 50% por nivel
    public int obtenerCostoMejora() {
        return (int) Math.ceil(10.0 * Math.pow(1.5, this.nivel));
    }

    // ACCIÓN: Subir de nivel
    public void mejorar() {
        this.nivel++;
    }

    // GETTERS
    public String obtenerNombre() {
        return nombre;
    }

    public int obtenerNivel() {
        return nivel;
    }
}