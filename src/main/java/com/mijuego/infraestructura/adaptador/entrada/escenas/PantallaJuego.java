package com.mijuego.infraestructura.adaptador.entrada.escenas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mijuego.aplicacion.puerto.entrada.PuertoCortarPapel;
import com.mijuego.aplicacion.puerto.entrada.PuertoIniciarPartida;
import com.mijuego.aplicacion.puerto.entrada.PuertoMejorarTijera;
import com.mijuego.aplicacion.puerto.salida.PuertoAudio;
import com.mijuego.dominio.modelo.Partida;
import com.mijuego.infraestructura.adaptador.entrada.gui.GestorCursor;
import com.mijuego.infraestructura.adaptador.entrada.gui.GestorRecursos;
import com.mijuego.infraestructura.adaptador.entrada.gui.JuegoGUI;
import com.mijuego.infraestructura.adaptador.entrada.gui.JuegoHUD;

public abstract class PantallaJuego implements Screen {

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Vector3 touchPoint;

    protected GestorRecursos recursos;
    private JuegoHUD hud;

    private final Rectangle rectBtnMejorar = new Rectangle(16f, 132f, 330f, 108f);

    // Dominio
    protected Partida partida;
    private int recompensaReciente;
    private float tiempoRecompensa;
    private final GestorCursor gestorCursor;
    private final PuertoCortarPapel cortarPapel;
    private final PuertoMejorarTijera mejorarTijera;
    private final PuertoIniciarPartida iniciarPartida;
    protected final PuertoAudio audio;

    public PantallaJuego(
            PuertoCortarPapel cortarPapel,
            PuertoMejorarTijera mejorarTijera,
            PuertoIniciarPartida iniciarPartida,
            GestorCursor gestorCursor,
            PuertoAudio audio) {
        this.cortarPapel = cortarPapel;
        this.mejorarTijera = mejorarTijera;
        this.iniciarPartida = iniciarPartida;
        this.gestorCursor = gestorCursor;
        this.audio = audio;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();

        // FitViewport para mantener la relación de aspecto 16:9 sin estirar la pantalla
        viewport = new FitViewport(JuegoGUI.VIRTUAL_WIDTH, JuegoGUI.VIRTUAL_HEIGHT, camera);
        viewport.apply();
        camera.position.set(JuegoGUI.VIRTUAL_WIDTH / 2f, JuegoGUI.VIRTUAL_HEIGHT / 2f, 0);
        touchPoint = new Vector3();

        recursos = new GestorRecursos();

        // Aseguramos que los recursos usen el filtro Nearest para evitar bordes borrosos
        aplicarFiltroRecursos();

        hud = new JuegoHUD(recursos);

        partida = iniciarPartida.ejecutar();
    }

    protected void aplicarFiltroRecursos() {
        if (recursos.getFondo() != null) recursos.getFondo().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        if (recursos.getMesa() != null) recursos.getMesa().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        if (recursos.getTijera() != null) recursos.getTijera().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    @Override
    public void render(float delta) {
        audio.actualizar(delta);
        actualizarEscena(delta);
        Gdx.gl.glClearColor(0.035f, 0.045f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        tiempoRecompensa = Math.max(0f, tiempoRecompensa - delta);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(touchPoint);

        if (Gdx.input.justTouched() && rectBtnMejorar.contains(touchPoint.x, touchPoint.y)) {
            mejorarTijera.ejecutar(partida);
        }
        procesarEntradaEscena(touchPoint.x, touchPoint.y, Gdx.input.justTouched());

        // Renderizado
        batch.begin();
        dibujarEscena(batch);

        hud.dibujar(batch, partida, rectBtnMejorar, recompensaReciente, tiempoRecompensa);
        batch.end();
    }

    protected abstract void actualizarEscena(float delta);

    protected abstract void procesarEntradaEscena(float x, float y, boolean nuevoClick);

    protected abstract void dibujarEscena(SpriteBatch batch);

    protected int registrarCorte() {
        recompensaReciente = cortarPapel.ejecutar(partida);
        if (recompensaReciente > 0f) {
            tiempoRecompensa = 1.4f;
        }
        return recompensaReciente;
    }

    protected void agarrarTijeras(boolean agarradas) {
        gestorCursor.setTijerasAgarradas(agarradas);
    }

    protected void dibujarFondo(SpriteBatch batch) {
        if (recursos.getFondo() != null) {
            batch.draw(recursos.getFondo(), 0, 0, JuegoGUI.VIRTUAL_WIDTH, JuegoGUI.VIRTUAL_HEIGHT);
        }
    }

    @Override
    public void resize(int width, int height) {
        // El parámetro true mantiene la cámara centrada correctamente
        viewport.update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        agarrarTijeras(false);
        batch.dispose();
        recursos.dispose();
    }
}