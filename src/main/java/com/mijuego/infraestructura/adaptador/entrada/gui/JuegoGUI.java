package com.mijuego.infraestructura.adaptador.entrada.gui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class JuegoGUI extends Game {

    public static final int VIRTUAL_WIDTH = 1280;
    public static final int VIRTUAL_HEIGHT = 720;

    private SpriteBatch batchGlobal;
    private Viewport viewportGlobal;
    private OrthographicCamera cameraGlobal;
    private GestorCursor gestorCursor;

    @Override
    public void create() {
        batchGlobal = new SpriteBatch();
        cameraGlobal = new OrthographicCamera();
        viewportGlobal = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, cameraGlobal);
        viewportGlobal.apply();

        // Inicializamos el cursor globalmente una sola vez
        gestorCursor = new GestorCursor();

        // Muestra la pantalla Splash al iniciar
        setScreen(new PantallaSplash(this));
    }

    @Override
    public void render() {
        // 1. Dibuja la pantalla actual (Splash, Menú Principal, Juego, etc.)
        super.render();

        // 2. Dibuja el cursor encima de CUALQUIER pantalla
        cameraGlobal.update();
        batchGlobal.setProjectionMatrix(cameraGlobal.combined);
        viewportGlobal.apply();

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
    }
}