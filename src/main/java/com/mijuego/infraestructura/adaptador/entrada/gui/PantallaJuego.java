package com.mijuego.infraestructura.adaptador.entrada.gui;

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
import com.mijuego.aplicacion.casodeuso.CortarPapelCasoUso;
import com.mijuego.aplicacion.casodeuso.MejorarTijeraCasoUso;
import com.mijuego.dominio.modelo.Papel;
import com.mijuego.dominio.modelo.Tijera;
import com.mijuego.dominio.servicio.ServicioCorte;

public class PantallaJuego implements Screen {

    private final JuegoGUI juego;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Vector3 touchPoint;

    private GestorRecursos recursos;
    private JuegoHUD hud;

    private Rectangle rectBtnCortar;
    private Rectangle rectBtnMejorar;

    // Dominio
    private double monedas;
    private Tijera miTijera;
    private Papel papelObjetivo;
    private CortarPapelCasoUso cortarPapel;
    private MejorarTijeraCasoUso mejorarTijera;

    public PantallaJuego(JuegoGUI juego) {
        this.juego = juego;
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
        recursos.cargarRecursos();

        // Aseguramos que los recursos usen el filtro Nearest para evitar bordes borrosos
        aplicarFiltroRecursos();

        hud = new JuegoHUD(recursos);

        rectBtnCortar = new Rectangle(50, 100, 200, 70);
        rectBtnMejorar = new Rectangle(280, 100, 200, 70);

        monedas = 0.0;
        miTijera = new Tijera("Tijera Oxidada", 2.0);
        papelObjetivo = new Papel(10.0);
        cortarPapel = new CortarPapelCasoUso(new ServicioCorte());
        mejorarTijera = new MejorarTijeraCasoUso();
    }

    private void aplicarFiltroRecursos() {
        if (recursos.imgFondo != null) recursos.imgFondo.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        if (recursos.imgMesa != null) recursos.imgMesa.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        if (recursos.imgTijera != null) recursos.imgTijera.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.12f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Entrada
        if (Gdx.input.justTouched()) {
            touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(touchPoint);

            if (rectBtnCortar.contains(touchPoint.x, touchPoint.y)) {
                double ganancia = cortarPapel.ejecutar(miTijera, papelObjetivo);
                if (ganancia > 0) monedas += ganancia;
            } else if (rectBtnMejorar.contains(touchPoint.x, touchPoint.y)) {
                double costo = miTijera.obtenerCostoMejora();
                if (mejorarTijera.ejecutar(miTijera, monedas)) monedas -= costo;
            }
        }

        // Renderizado
        batch.begin();
        if (recursos.imgFondo != null) batch.draw(recursos.imgFondo, 0, 0, JuegoGUI.VIRTUAL_WIDTH, JuegoGUI.VIRTUAL_HEIGHT);
        if (recursos.imgMesa != null) batch.draw(recursos.imgMesa, 620, 85, 550, 550);
        if (recursos.imgTijera != null) batch.draw(recursos.imgTijera, 767, 232, 256, 256);

        hud.dibujar(batch, monedas, miTijera, papelObjetivo, rectBtnCortar, rectBtnMejorar);
        batch.end();
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
        batch.dispose();
        recursos.dispose();
    }
}