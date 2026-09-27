package com.mijuego.aplicacion.casodeuso;

import com.mijuego.aplicacion.puerto.entrada.PuertoIniciarPartida;
import com.mijuego.dominio.modelo.Papel;
import com.mijuego.dominio.modelo.Partida;
import com.mijuego.dominio.modelo.Tijera;

public class IniciarPartidaCasoUso implements PuertoIniciarPartida {
    @Override
    public Partida ejecutar() {
        return new Partida(new Tijera("Tijera Oxidada", 2.0), new Papel(10.0));
    }
}
