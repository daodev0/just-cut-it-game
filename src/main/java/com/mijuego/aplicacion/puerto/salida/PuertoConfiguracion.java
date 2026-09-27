package com.mijuego.aplicacion.puerto.salida;

public interface PuertoConfiguracion {
    boolean isUsarCursorPuntoBlanco();

    void alternarCursor();

    float getSensibilidadCursor();

    void setSensibilidadCursor(float sensibilidad);

    boolean isPantallaCompleta();

    void alternarModoPantalla();
}
