package com.mijuego.infraestructura.adaptador.entrada.gui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mijuego.aplicacion.casodeuso.CortarPapelCasoUso;
import com.mijuego.aplicacion.casodeuso.IniciarPartidaCasoUso;
import com.mijuego.aplicacion.casodeuso.MejorarTijeraCasoUso;
import com.mijuego.aplicacion.puerto.entrada.PuertoCortarPapel;
import com.mijuego.aplicacion.puerto.entrada.PuertoIniciarPartida;
import com.mijuego.aplicacion.puerto.entrada.PuertoMejorarTijera;
import com.mijuego.aplicacion.puerto.salida.PuertoAudio;
import com.mijuego.aplicacion.puerto.salida.PuertoConfiguracion;
import com.mijuego.dominio.servicio.ServicioCorte;
import com.mijuego.infraestructura.adaptador.salida.audio.AdaptadorAudioLibGDX;
import com.mijuego.infraestructura.adaptador.salida.configuracion.AdaptadorConfiguracionLibGDX;
import com.mijuego.infraestructura.adaptador.entrada.escenas.Escena1Papel;
import com.mijuego.infraestructura.adaptador.entrada.escenas.PantallaSplash;

public class JuegoGUI extends Game {

    public static final int VIRTUAL_WIDTH = 1280;
    public static final int VIRTUAL_HEIGHT = 720;

    private SpriteBatch batchGlobal;
    private Viewport viewportGlobal;
    private OrthographicCamera cameraGlobal;
    private GestorCursor gestorCursor;
    private PuertoAudio audio;
    private PuertoConfiguracion configuracion;
    private PuertoCortarPapel cortarPapel;
    private PuertoMejorarTijera mejorarTijera;
    private PuertoIniciarPartida iniciarPartida;

    @Override
    public void create() {
        batchGlobal = new SpriteBatch();
        cameraGlobal = new OrthographicCamera();
        viewportGlobal = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, cameraGlobal);
        viewportGlobal.apply();

        audio = new AdaptadorAudioLibGDX();
        configuracion = new AdaptadorConfiguracionLibGDX();
        cortarPapel = new CortarPapelCasoUso(new ServicioCorte());
        mejorarTijera = new MejorarTijeraCasoUso();
        iniciarPartida = new IniciarPartidaCasoUso();

        // 🔊 Carga de Sonidos y Música
        audio.cargarSonido("wosh", "wosh1.mp3");
        audio.cargarSonido("select", "seleccionar.mp3");
        audio.cargarMusica("menu", "bucle1.wav");
        audio.cargarMusica("escena1", "escena1loop.mp3");
        audio.cargarMusica("escena1talking", "escena1talkingloop.mp3");

        // Inicializar cursor global
        gestorCursor = new GestorCursor(configuracion);

        // Muestra la pantalla Splash o directamente el Menú Principal
        setScreen(new PantallaSplash(this, audio));
    }

    @Override
    public void render() {
        super.render(); // Renderiza la pantalla activa

        // Renderizar el cursor global por encima de la UI
        viewportGlobal.apply();
        cameraGlobal.update();
        batchGlobal.setProjectionMatrix(cameraGlobal.combined);

        batchGlobal.begin();
        gestorCursor.render(batchGlobal, viewportGlobal);
        batchGlobal.end();
    }

    @Override
    public void resize(int width, int height) {
        viewportGlobal.update(width, height, true);
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (batchGlobal != null) batchGlobal.dispose();
        if (gestorCursor != null) gestorCursor.dispose();
        if (audio != null) audio.dispose();
    }

    public PuertoAudio getAudio() {
        return audio;
    }

    public PuertoConfiguracion getConfiguracion() {
        return configuracion;
    }

    public void mostrarPantallaJuego() {
        setScreen(new Escena1Papel(cortarPapel, mejorarTijera, iniciarPartida, gestorCursor, audio));
    }
}