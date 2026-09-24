package com.mijuego.infraestructura.adaptador.entrada.consola;

import com.mijuego.aplicacion.casodeuso.CortarPapelCasoUso;
import com.mijuego.aplicacion.casodeuso.MejorarTijeraCasoUso;
import com.mijuego.dominio.modelo.Papel;
import com.mijuego.dominio.modelo.Tijera;
import com.mijuego.dominio.servicio.ServicioCorte;

import java.util.Scanner;

public class ConsolaAdaptador {

    public static void main(String[] args) {
        // Estado del jugador
        double monedas = 0.0;

        // Entidades y Casos de Uso
        Tijera miTijera = new Tijera("Tijera Oxidada", 2.0);
        Papel papelObjetivo = new Papel(10.0);

        ServicioCorte servicioCorte = new ServicioCorte();
        CortarPapelCasoUso cortarPapel = new CortarPapelCasoUso(servicioCorte);
        MejorarTijeraCasoUso mejorarTijera = new MejorarTijeraCasoUso();

        Scanner scanner = new Scanner(System.in);
        boolean jugando = true;

        System.out.println("=== ¡BIENVENIDO A INCREMENTAL CLIPPER! ===");

        while (jugando) {
            System.out.println("\n----------------------------------------");
            System.out.println("Monedas acumuladas: $" + monedas);
            System.out.println("Tijera: " + miTijera.obtenerNombre() + " (Nivel " + miTijera.obtenerNivel() + ")");
            System.out.println("Poder de corte: " + miTijera.obtenerPoderCorte());
            System.out.println("Vida del papel: " + papelObjetivo.obtenerVidaActual() + " / " + papelObjetivo.obtenerVidaMaxima());
            System.out.println("----------------------------------------");
            System.out.println("1. Cortar papel");
            System.out.println("2. Mejorar tijera (Costo: $" + miTijera.obtenerCostoMejora() + ")");
            System.out.println("3. Salir");
            System.out.print("Elige una opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    double ganancia = cortarPapel.ejecutar(miTijera, papelObjetivo);
                    System.out.println("¡Zas! Cortaste el papel.");
                    if (ganancia > 0) {
                        monedas += ganancia;
                        System.out.println("¡Destruiste la hoja y ganaste $" + ganancia + " monedas!");
                    }
                    break;

                case "2":
                    double costo = miTijera.obtenerCostoMejora();
                    boolean exito = mejorarTijera.ejecutar(miTijera, monedas);
                    if (exito) {
                        monedas -= costo;
                        System.out.println("¡Tijera mejorada a nivel " + miTijera.obtenerNivel() + "!");
                    } else {
                        System.out.println("¡No tienes suficientes monedas! Necesitas $" + costo);
                    }
                    break;

                case "3":
                    jugando = false;
                    System.out.println("¡Gracias por jugar!");
                    break;

                default:
                    System.out.println("Opción no válida.");
            }
        }

        scanner.close();
    }
}