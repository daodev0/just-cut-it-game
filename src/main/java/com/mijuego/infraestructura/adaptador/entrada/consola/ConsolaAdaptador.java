package com.mijuego.infraestructura.adaptador.entrada.consola;

import com.mijuego.aplicacion.casodeuso.CortarPapelCasoUso;
import com.mijuego.aplicacion.casodeuso.IniciarPartidaCasoUso;
import com.mijuego.aplicacion.casodeuso.MejorarTijeraCasoUso;
import com.mijuego.aplicacion.puerto.entrada.PuertoCortarPapel;
import com.mijuego.aplicacion.puerto.entrada.PuertoIniciarPartida;
import com.mijuego.aplicacion.puerto.entrada.PuertoMejorarTijera;
import com.mijuego.dominio.modelo.Partida;
import com.mijuego.dominio.servicio.ServicioCorte;

import java.util.Scanner;

public class ConsolaAdaptador {

    public static void main(String[] args) {
        // Ensamblado de la aplicación
        PuertoIniciarPartida iniciarPartida = new IniciarPartidaCasoUso();
        Partida partida = iniciarPartida.ejecutar();
        PuertoCortarPapel cortarPapel = new CortarPapelCasoUso(new ServicioCorte());
        PuertoMejorarTijera mejorarTijera = new MejorarTijeraCasoUso();

        Scanner scanner = new Scanner(System.in);
        boolean jugando = true;

        System.out.println("=== ¡BIENVENIDO A INCREMENTAL CLIPPER! ===");

        while (jugando) {
            System.out.println("\n----------------------------------------");
            System.out.println("Monedas acumuladas: $" + partida.obtenerMonedas());
            System.out.println("Tijera: " + partida.obtenerTijera().obtenerNombre() + " (Nivel " + partida.obtenerTijera().obtenerNivel() + ")");
            System.out.println("Poder de corte: " + partida.obtenerTijera().obtenerPoderCorte());
            System.out.println("Vida del papel: " + partida.obtenerPapel().obtenerVidaActual() + " / " + partida.obtenerPapel().obtenerVidaMaxima());
            System.out.println("----------------------------------------");
            System.out.println("1. Cortar papel");
            System.out.println("2. Mejorar tijera (Costo: $" + partida.obtenerTijera().obtenerCostoMejora() + ")");
            System.out.println("3. Salir");
            System.out.print("Elige una opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    int ganancia = cortarPapel.ejecutar(partida);
                    System.out.println("¡Zas! Cortaste el papel.");
                    if (ganancia > 0) {
                        System.out.println("¡Destruiste la hoja y ganaste $" + ganancia + " monedas!");
                    }
                    break;

                case "2":
                    int costo = partida.obtenerTijera().obtenerCostoMejora();
                    boolean exito = mejorarTijera.ejecutar(partida);
                    if (exito) {
                        System.out.println("¡Tijera mejorada a nivel " + partida.obtenerTijera().obtenerNivel() + "!");
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