package com.mijuego.infraestructura.adaptador.salida.configuracion;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Preferences;
import com.mijuego.aplicacion.puerto.salida.PuertoConfiguracion;

public class AdaptadorConfiguracionLibGDX implements PuertoConfiguracion {

    private final Preferences prefs;

    private boolean usarCursorPuntoBlanco;
    private float sensibilidadCursor;
    private boolean pantallaCompleta; // 👈 Nueva variable

    public AdaptadorConfiguracionLibGDX() {
        prefs = Gdx.app.getPreferences("MiJuegoConfig");
        usarCursorPuntoBlanco = prefs.getBoolean("usarCursorPuntoBlanco", true);
        sensibilidadCursor = prefs.getFloat("sensibilidadCursor", 1.0f);
        pantallaCompleta = prefs.getBoolean("pantallaCompleta", false);
    }

    @Override
    public boolean isUsarCursorPuntoBlanco() {
        return usarCursorPuntoBlanco;
    }

    @Override
    public void alternarCursor() {
        usarCursorPuntoBlanco = !usarCursorPuntoBlanco;
        prefs.putBoolean("usarCursorPuntoBlanco", usarCursorPuntoBlanco);
        prefs.flush();
    }

    @Override
    public float getSensibilidadCursor() {
        return sensibilidadCursor;
    }

    @Override
    public void setSensibilidadCursor(float sensibilidad) {
        this.sensibilidadCursor = Math.max(0.2f, Math.min(3.0f, sensibilidad));
        prefs.putFloat("sensibilidadCursor", this.sensibilidadCursor);
        prefs.flush();
    }

    // 🎯 Getter y Métodos para Modo de Pantalla
    @Override
    public boolean isPantallaCompleta() {
        return pantallaCompleta;
    }

    @Override
    public void alternarModoPantalla() {
        setPantallaCompleta(!pantallaCompleta);
    }

    private void setPantallaCompleta(boolean activar) {
        this.pantallaCompleta = activar;
        prefs.putBoolean("pantallaCompleta", pantallaCompleta);
        prefs.flush();
        aplicarModoPantalla();
    }

    private void aplicarModoPantalla() {
        if (pantallaCompleta) {
            DisplayMode displayMode = Gdx.graphics.getDisplayMode();
            Gdx.graphics.setFullscreenMode(displayMode);
        } else {
            // Regresa a ventana de 1280x720 (puedes ajustar esta resolución por defecto)
            Gdx.graphics.setWindowedMode(1280, 720);
        }
    }
}