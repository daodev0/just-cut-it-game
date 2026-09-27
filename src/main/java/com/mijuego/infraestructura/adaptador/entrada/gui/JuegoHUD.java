package com.mijuego.infraestructura.adaptador.entrada.gui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.mijuego.dominio.modelo.Partida;

import java.util.Locale;

public class JuegoHUD {

    private static final Color COLOR_BOTON_DESACTIVADO = new Color(0.58f, 0.61f, 0.67f, 1f);
    private final GestorRecursos recursos;
    private final float escalaFuentes;

    public JuegoHUD(GestorRecursos recursos) {
        this.recursos = recursos;
        escalaFuentes = calcularEscalaFuentes();
    }

    public void dibujar(
            SpriteBatch batch,
            Partida partida,
            Rectangle btnMejorar,
            int recompensaReciente,
            float tiempoRecompensa) {
        var tijera = partida.obtenerTijera();
        var papel = partida.obtenerPapel();
        var font = recursos.getFont();
        float panelX = 16f;
        float panelY = 354f;
        float panelWidth = 330f;
        float panelHeight = 190f;
        float porcentajeVida = MathUtils.clamp((float) (papel.obtenerVidaActual() / papel.obtenerVidaMaxima()), 0f, 1f);
        boolean puedeMejorar = partida.obtenerMonedas() >= tijera.obtenerCostoMejora();

        batch.setColor(0.025f, 0.035f, 0.085f, 0.98f);
        batch.draw(recursos.getPanel(), 0f, 0f, 366f, 720f);
        batch.setColor(0.25f, 0.55f, 0.95f, 1f);
        batch.draw(recursos.getPanel(), 362f, 0f, 4f, 720f);
        batch.setColor(Color.WHITE);

        // Encabezado y saldo destacado
        dibujarPanel(batch, 16f, 640f, 330f, 58f);
        font.setColor(Color.WHITE);
        establecerEscala(font, 1.7f);
        font.draw(batch, "JUST CUT IT!", 34f, 677f);
        establecerEscala(font, 0.95f);
        font.setColor(0.55f, 0.75f, 1f, 1f);
        font.draw(batch, "ESCENA 1  /  PAPEL", 36f, 653f);

        dibujarPanel(batch, 16f, 552f, 330f, 72f);
        batch.draw(recursos.getMoneda(), 32f, 571f, 34f, 34f);
        font.setColor(1f, 0.84f, 0.25f, 1f);
        establecerEscala(font, 0.9f);
        font.draw(batch, "MONEDAS", 78f, 608f);
        font.setColor(Color.WHITE);
        establecerEscala(font, 1.35f);
        font.draw(batch, Integer.toString(partida.obtenerMonedas()), 78f, 578f);

        if (tiempoRecompensa > 0f && recompensaReciente > 0f) {
            float alpha = MathUtils.clamp(tiempoRecompensa / 1.4f, 0f, 1f);
            font.setColor(0.55f, 1f, 0.55f, alpha);
            establecerEscala(font, 1.05f);
            font.draw(batch, "+" + recompensaReciente, 248f, 585f);
        }

        // Estado de tijera y papel
        dibujarPanel(batch, panelX, panelY, panelWidth, panelHeight);
        font.setColor(0.55f, 0.75f, 1f, 1f);
        establecerEscala(font, 1f);
        font.draw(batch, "TU EQUIPO", panelX + 20f, panelY + panelHeight - 28f);
        font.setColor(Color.WHITE);
        establecerEscala(font, 1.1f);
        font.draw(batch, tijera.obtenerNombre(), panelX + 18f, panelY + panelHeight - 60f);

        font.setColor(0.77f, 0.81f, 0.9f, 1f);
        establecerEscala(font, 0.82f);
        font.draw(batch, "NIVEL " + tijera.obtenerNivel(), panelX + 18f, panelY + panelHeight - 88f);
        font.draw(batch, String.format(Locale.US, "PODER  %.1f", tijera.obtenerPoderCorte()),
                panelX + 138f, panelY + panelHeight - 88f);

        font.setColor(Color.WHITE);
        establecerEscala(font, 0.78f);
        font.draw(batch, "RESISTENCIA DEL PAPEL", panelX + 18f, panelY + 74f);
        dibujarBarra(batch, panelX + 18f, panelY + 49f, panelWidth - 36f, 12f, porcentajeVida);
        font.setColor(0.77f, 0.81f, 0.9f, 1f);
        font.draw(batch, String.format(Locale.US, "%.0f / %.0f", papel.obtenerVidaActual(), papel.obtenerVidaMaxima()),
                panelX + 18f, panelY + 27f);

        // Acciones
        dibujarPanel(batch, 16f, 270f, 330f, 62f);
        font.setColor(1f, 0.84f, 0.25f, 1f);
        establecerEscala(font, 0.72f);
        font.draw(batch, "CLIC EN LAS TIJERAS Y LUEGO", 32f, 306f);
        font.setColor(0.77f, 0.81f, 0.9f, 1f);
        establecerEscala(font, 0.72f);
        font.draw(batch, "clic en el papel para cortar", 32f, 284f);

        dibujarBoton(batch, btnMejorar, recursos.getBotonMejorar(),
                puedeMejorar ? Color.WHITE : COLOR_BOTON_DESACTIVADO,
                "MEJORAR TIJERA",
                "Siguiente nivel - " + tijera.obtenerCostoMejora() + " monedas");
        font.setColor(Color.WHITE);
    }

    private void dibujarPanel(SpriteBatch batch, float x, float y, float width, float height) {
        batch.setColor(0.035f, 0.055f, 0.12f, 0.94f);
        batch.draw(recursos.getPanel(), x, y, width, height);
        batch.setColor(0.3f, 0.65f, 1f, 0.9f);
        batch.draw(recursos.getPanel(), x, y + height - 3f, width, 3f);
        batch.setColor(Color.WHITE);
    }

    private void dibujarBarra(SpriteBatch batch, float x, float y, float width, float height, float progreso) {
        batch.setColor(0.13f, 0.17f, 0.27f, 1f);
        batch.draw(recursos.getPanel(), x, y, width, height);
        batch.setColor(0.2f, 0.85f, 0.55f, 1f);
        batch.draw(recursos.getBotonMejorar(), x, y, width * progreso, height);
        batch.setColor(Color.WHITE);
    }

    private void dibujarBoton(
            SpriteBatch batch,
            Rectangle bounds,
            com.badlogic.gdx.graphics.Texture textura,
            Color color,
            String titulo,
            String subtitulo) {
        batch.setColor(color);
        batch.draw(textura, bounds.x, bounds.y, bounds.width, bounds.height);
        batch.setColor(Color.WHITE);

        var font = recursos.getFont();
        font.setColor(Color.WHITE);
        establecerEscala(font, 1.05f);
        font.draw(batch, titulo, bounds.x + 18f, bounds.y + bounds.height - 30f);
        font.setColor(0.87f, 0.91f, 1f, 1f);
        establecerEscala(font, 0.68f);
        font.draw(batch, subtitulo, bounds.x + 18f, bounds.y + 22f);
    }

    private float calcularEscalaFuentes() {
        if (Gdx.app.getType() != Application.ApplicationType.Android) {
            return 1f;
        }
        float escalaViewport = Gdx.graphics.getHeight() / (float) JuegoGUI.VIRTUAL_HEIGHT;
        return Gdx.graphics.getDensity() / Math.max(escalaViewport, 0.01f);
    }

    private void establecerEscala(com.badlogic.gdx.graphics.g2d.BitmapFont font, float escala) {
        font.getData().setScale(escala * escalaFuentes);
    }
}