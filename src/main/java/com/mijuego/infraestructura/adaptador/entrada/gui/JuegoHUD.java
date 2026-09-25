package com.mijuego.infraestructura.adaptador.entrada.gui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mijuego.dominio.modelo.Papel;
import com.mijuego.dominio.modelo.Tijera;

import java.util.Locale;

public class JuegoHUD {

    private final GestorRecursos recursos;

    public JuegoHUD(GestorRecursos recursos) {
        this.recursos = recursos;
    }

    public void dibujar(SpriteBatch batch, double monedas, Tijera tijera, Papel papel, Rectangle btnCortar, Rectangle btnMejorar) {
        recursos.font.draw(batch, "=== JUST CUT IT ===", 50, 650);
        recursos.font.draw(batch, String.format(Locale.US, "Monedas: $%.2f", monedas), 50, 570);
        recursos.font.draw(batch, "Tijera: " + tijera.obtenerNombre(), 50, 510);
        recursos.font.draw(batch, "Nivel: " + tijera.obtenerNivel(), 50, 470);
        recursos.font.draw(batch, String.format(Locale.US, "Poder de corte: %.1f", tijera.obtenerPoderCorte()), 50, 430);
        recursos.font.draw(batch, String.format(Locale.US, "Vida Papel: %.1f / %.1f", papel.obtenerVidaActual(), papel.obtenerVidaMaxima()), 50, 370);

        batch.draw(recursos.imgBtnCortar, btnCortar.x, btnCortar.y, btnCortar.width, btnCortar.height);
        batch.draw(recursos.imgBtnMejorar, btnMejorar.x, btnMejorar.y, btnMejorar.width, btnMejorar.height);

        recursos.font.draw(batch, "CORTAR", btnCortar.x + 45, btnCortar.y + 45);
        recursos.font.draw(batch, "MEJORAR", btnMejorar.x + 20, btnMejorar.y + 50);
        recursos.font.draw(batch, String.format(Locale.US, "$%.2f", tijera.obtenerCostoMejora()), btnMejorar.x + 110, btnMejorar.y + 25);
    }
}