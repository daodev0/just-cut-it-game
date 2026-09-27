package com.mijuego.infraestructura.adaptador.salida.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import com.mijuego.aplicacion.puerto.salida.PuertoAudio;
import java.util.HashMap;
import java.util.Map;

public class AdaptadorAudioLibGDX implements PuertoAudio, Disposable {

    private final Map<String, Sound> sonidos;
    private final Map<String, Music> canciones;

    private Music musicaActual;
    private String idMusicaActual;

    // Volúmenes independientes (0.0f a 1.0f)
    private float volumenSFX = 0.4f;     // Nivel equilibrado para efectos
    private float volumenMusica = 0.5f;  // La música más baja para no tapar los SFX

    // Fade-In control
    private boolean haciendoFadeIn = false;
    private float duracionFade = 0f;
    private float tiempoFade = 0f;
    private float factorMusicaActual = 1.0f;

    public AdaptadorAudioLibGDX() {
        sonidos = new HashMap<>();
        canciones = new HashMap<>();
    }

    // --- CARGA DE ASSETS ---

    @Override
    public void cargarSonido(String id, String ruta) {
        if (!sonidos.containsKey(id)) {
            try {
                sonidos.put(id, Gdx.audio.newSound(Gdx.files.internal(ruta)));
            } catch (Exception e) {
                Gdx.app.error("AdaptadorAudioLibGDX", "Error al cargar sonido: " + ruta, e);
            }
        }
    }

    @Override
    public void cargarMusica(String id, String ruta) {
        if (!canciones.containsKey(id)) {
            try {
                canciones.put(id, Gdx.audio.newMusic(Gdx.files.internal(ruta)));
            } catch (Exception e) {
                Gdx.app.error("AdaptadorAudioLibGDX", "Error al cargar música: " + ruta, e);
            }
        }
    }

    // --- EFECTOS DE SONIDO (SFX) ---

    @Override
    public void sonar(String id) {
        sonar(id, 1.0f);
    }

    private void sonar(String id, float factorAjusteLocal) {
        Sound sonido = sonidos.get(id);
        if (sonido != null) {
            float volFinal = volumenSFX * factorAjusteLocal;
            volFinal = Math.max(0.0f, Math.min(1.0f, volFinal));
            sonido.play(volFinal);
        }
    }

    // --- MÚSICA ---

    private void reproducirMusica(String id, float factorAjusteLocal) {
        if (idMusicaActual != null && idMusicaActual.equals(id) && musicaActual != null && musicaActual.isPlaying()) {
            return;
        }

        detenerMusica();

        musicaActual = canciones.get(id);
        if (musicaActual != null) {
            idMusicaActual = id;
            factorMusicaActual = factorAjusteLocal;
            musicaActual.setLooping(true);

            actualizarVolumenMusicaActual();
            musicaActual.play();
        }
    }

    @Override
    public void reproducirMusicaConFade(String id, float duracionSegundos, float factorAjusteLocal) {
        reproducirMusica(id, factorAjusteLocal);
        if (musicaActual != null) {
            if (duracionSegundos <= 0f) {
                haciendoFadeIn = false;
                musicaActual.setVolume(Math.max(0.0f, Math.min(1.0f, volumenMusica * factorMusicaActual)));
                return;
            }
            duracionFade = duracionSegundos;
            tiempoFade = 0f;
            haciendoFadeIn = true;
            musicaActual.setVolume(0f);
        }
    }

    @Override
    public void actualizar(float delta) {
        if (haciendoFadeIn && musicaActual != null) {
            tiempoFade += delta;
            float progreso = tiempoFade / duracionFade;

            if (progreso >= 1.0f) {
                progreso = 1.0f;
                haciendoFadeIn = false;
            }

            float volObjetivo = volumenMusica * factorMusicaActual;
            musicaActual.setVolume(volObjetivo * progreso);
        }
    }

    private void detenerMusica() {
        if (musicaActual != null) {
            musicaActual.stop();
            musicaActual = null;
            idMusicaActual = null;
            haciendoFadeIn = false;
        }
    }

    // --- CONTROLES DE VOLUMEN Y GETTERS ---

    @Override
    public float getVolumenSFX() {
        return volumenSFX;
    }

    @Override
    public void setVolumenSFX(float volumen) {
        this.volumenSFX = Math.max(0.0f, Math.min(1.0f, volumen));
    }

    @Override
    public float getVolumenMusica() {
        return volumenMusica;
    }

    @Override
    public void setVolumenMusica(float volumen) {
        this.volumenMusica = Math.max(0.0f, Math.min(1.0f, volumen));
        actualizarVolumenMusicaActual();
    }

    private void actualizarVolumenMusicaActual() {
        if (musicaActual != null && !haciendoFadeIn) {
            float volFinal = volumenMusica * factorMusicaActual;
            musicaActual.setVolume(Math.max(0.0f, Math.min(1.0f, volFinal)));
        }
    }

    @Override
    public void dispose() {
        detenerMusica();
        for (Sound s : sonidos.values()) s.dispose();
        for (Music m : canciones.values()) m.dispose();
        sonidos.clear();
        canciones.clear();
    }
}