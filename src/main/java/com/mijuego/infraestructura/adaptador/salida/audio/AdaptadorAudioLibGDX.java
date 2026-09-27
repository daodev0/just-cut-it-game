package com.mijuego.infraestructura.adaptador.salida.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import com.mijuego.aplicacion.puerto.salida.PuertoAudio;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AdaptadorAudioLibGDX implements PuertoAudio, Disposable {

    private final Map<String, Sound> sonidos;
    private final Map<String, Music> canciones;
    private final Map<String, ReproduccionMusical> reproduccionesActivas;

    // Volúmenes independientes (0.0f a 1.0f)
    private float volumenSFX = 0.4f;     // Nivel equilibrado para efectos
    private float volumenMusica = 0.5f;  // La música más baja para no tapar los SFX

    public AdaptadorAudioLibGDX() {
        sonidos = new HashMap<>();
        canciones = new HashMap<>();
        reproduccionesActivas = new HashMap<>();
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

    private void reproducirMusica(String id, float factorAjusteLocal, boolean simultanea,
                                  float duracionSegundos) {
        if (!simultanea) {
            detenerMusica();
        } else {
            ReproduccionMusical reproduccionExistente = reproduccionesActivas.get(id);
            if (reproduccionExistente != null && reproduccionExistente.musica.isPlaying()) {
                return;
            }
        }

        Music musica = canciones.get(id);
        if (musica != null) {
            ReproduccionMusical reproduccion = new ReproduccionMusical(
                    musica, factorAjusteLocal, duracionSegundos);
            reproduccionesActivas.put(id, reproduccion);
            musica.setLooping(true);
            musica.setVolume(reproduccion.volumenActual(volumenMusica));
            musica.play();
        }
    }

    @Override
    public void reproducirMusicaConFade(String id, float duracionSegundos, float factorAjusteLocal) {
        reproducirMusica(id, factorAjusteLocal, false, duracionSegundos);
    }

    @Override
    public void reproducirMusicaSimultaneaConFade(
            String id,
            float duracionSegundos,
            float factorAjusteLocal) {
        reproducirMusica(id, factorAjusteLocal, true, duracionSegundos);
    }

    @Override
    public void actualizar(float delta) {
        Iterator<Map.Entry<String, ReproduccionMusical>> iterator =
                reproduccionesActivas.entrySet().iterator();
        while (iterator.hasNext()) {
            ReproduccionMusical reproduccion = iterator.next().getValue();
            reproduccion.actualizar(delta);
            reproduccion.musica.setVolume(reproduccion.volumenActual(volumenMusica));
            if (!reproduccion.musica.isPlaying()) {
                iterator.remove();
            }
        }
    }

    private void detenerMusica() {
        for (ReproduccionMusical reproduccion : reproduccionesActivas.values()) {
            reproduccion.musica.stop();
        }
        reproduccionesActivas.clear();
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
        for (ReproduccionMusical reproduccion : reproduccionesActivas.values()) {
            reproduccion.musica.setVolume(reproduccion.volumenActual(volumenMusica));
        }
    }

    private static final class ReproduccionMusical {
        private final Music musica;
        private final float factorVolumen;
        private final float duracionFade;
        private float tiempoFade;

        private ReproduccionMusical(Music musica, float factorVolumen, float duracionFade) {
            this.musica = musica;
            this.factorVolumen = factorVolumen;
            this.duracionFade = Math.max(0f, duracionFade);
            tiempoFade = 0f;
        }

        private void actualizar(float delta) {
            tiempoFade = Math.min(duracionFade, tiempoFade + delta);
        }

        private float volumenActual(float volumenMusica) {
            float progreso = duracionFade == 0f ? 1f : tiempoFade / duracionFade;
            return Math.max(0f, Math.min(1f, volumenMusica * factorVolumen * progreso));
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