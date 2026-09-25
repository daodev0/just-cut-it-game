package com.mijuego.infraestructura.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class ConfiguracionJuego {

    private static ConfiguracionJuego instancia;
    private final Preferences prefs;

    private boolean usarCursorPuntoBlanco;

    private ConfiguracionJuego() {
        // Guarda las preferencias en disco automáticamente
        prefs = Gdx.app.getPreferences("MiJuegoPrefs");
        usarCursorPuntoBlanco = prefs.getBoolean("usarCursorPuntoBlanco", true);
    }

    public static ConfiguracionJuego getInstancia() {
        if (instancia == null) {
            instancia = new ConfiguracionJuego();
        }
        return instancia;
    }

    public boolean isUsarCursorPuntoBlanco() {
        return usarCursorPuntoBlanco;
    }

    public void setUsarCursorPuntoBlanco(boolean usarPuntoBlanco) {
        this.usarCursorPuntoBlanco = usarPuntoBlanco;
        prefs.putBoolean("usarCursorPuntoBlanco", usarPuntoBlanco);
        prefs.flush(); // Guardar cambios
    }

    public void alternarCursor() {
        setUsarCursorPuntoBlanco(!usarCursorPuntoBlanco);
    }
}