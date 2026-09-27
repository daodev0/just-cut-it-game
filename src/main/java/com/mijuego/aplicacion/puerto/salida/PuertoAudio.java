package com.mijuego.aplicacion.puerto.salida;

public interface PuertoAudio {
    void cargarSonido(String id, String ruta);

    void cargarMusica(String id, String ruta);

    void sonar(String id);

    void reproducirMusicaConFade(String id, float duracionSegundos, float factorAjusteLocal);

    void actualizar(float delta);

    float getVolumenSFX();

    void setVolumenSFX(float volumen);

    float getVolumenMusica();

    void setVolumenMusica(float volumen);

    void dispose();
}
