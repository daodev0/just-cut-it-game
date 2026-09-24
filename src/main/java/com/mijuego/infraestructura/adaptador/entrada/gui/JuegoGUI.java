package com.mijuego.infraestructura.adaptador.entrada.gui;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mijuego.aplicacion.casodeuso.CortarPapelCasoUso;
import com.mijuego.aplicacion.casodeuso.MejorarTijeraCasoUso;
import com.mijuego.dominio.modelo.Papel;
import com.mijuego.dominio.modelo.Tijera;
import com.mijuego.dominio.servicio.ServicioCorte;

import java.util.Locale;

public class JuegoGUI extends ApplicationAdapter {

    public static final int VIRTUAL_WIDTH = 1280;
    public static final int VIRTUAL_HEIGHT = 720;

    private SpriteBatch batch;
    private BitmapFont font;

    private OrthographicCamera camera;
    private Viewport viewport;
    private Vector3 touchPoint;

    // Texturas
    private Texture imgFondo; // Fondo general del taller/habitación
    private Texture imgMesa;  // Mesa redonda grande
    private Texture imgTijera;// Tijera grande
    private Texture imgBtnCortar;
    private Texture imgBtnMejorar;

    // Hitboxes
    private Rectangle rectBtnCortar;
    private Rectangle rectBtnMejorar;

    // Dominio
    private double monedas;
    private Tijera miTijera;
    private Papel papelObjetivo;
    private CortarPapelCasoUso cortarPapel;
    private MejorarTijeraCasoUso mejorarTijera;

    @Override
    public void create() {
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        viewport.apply();
        camera.position.set(VIRTUAL_WIDTH / 2f, VIRTUAL_HEIGHT / 2f, 0);
        touchPoint = new Vector3();

        font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.getData().setScale(1.5f);

        // 1. Cargar Fondo General (opcional)
        if (Gdx.files.internal("fondo.png").exists()) {
            imgFondo = new Texture(Gdx.files.internal("fondo.png"));
            imgFondo.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }

        // 2. Cargar Mesa
        if (Gdx.files.internal("mesa.png").exists()) {
            imgMesa = new Texture(Gdx.files.internal("mesa.png"));
            imgMesa.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }

        // 3. Cargar Tijera
        if (Gdx.files.internal("tijera.png").exists()) {
            imgTijera = new Texture(Gdx.files.internal("tijera.png"));
            imgTijera.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        } else {
            Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
            pixmap.setColor(1f, 0f, 1f, 1f);
            pixmap.fill();
            imgTijera = new Texture(pixmap);
            pixmap.dispose();
        }

        // 4. Crear texturas para botones
        Pixmap pixmapCortar = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapCortar.setColor(0.2f, 0.5f, 0.8f, 1f);
        pixmapCortar.fill();
        imgBtnCortar = new Texture(pixmapCortar);
        pixmapCortar.dispose();

        Pixmap pixmapMejorar = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapMejorar.setColor(0.2f, 0.7f, 0.3f, 1f);
        pixmapMejorar.fill();
        imgBtnMejorar = new Texture(pixmapMejorar);
        pixmapMejorar.dispose();

        // Alineados en la zona izquierda para no estorbar con la mesa
        rectBtnCortar = new Rectangle(50, 100, 200, 70);
        rectBtnMejorar = new Rectangle(280, 100, 200, 70);

        monedas = 0.0;
        miTijera = new Tijera("Tijera Oxidada", 2.0);
        papelObjetivo = new Papel(10.0);

        ServicioCorte servicioCorte = new ServicioCorte();
        cortarPapel = new CortarPapelCasoUso(servicioCorte);
        mejorarTijera = new MejorarTijeraCasoUso();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.12f, 1f); // Color oscuro de fondo por defecto
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

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

        batch.begin();

        // CAPA 1: FONDO COMPLETO DE LA PANTALLA
        if (imgFondo != null) {
            batch.draw(imgFondo, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        }

        // CAPA 2: MESA GRANDE EN LA ZONA DERECHA (TAMAÑO 550x550)
        if (imgMesa != null) {
            batch.draw(imgMesa, 620, 85, 550, 550);
        }

        // CAPA 3: TIJERA GRANDE CENTRADA SOBRE LA MESA (TAMAÑO 256x256)
        // Posicionada en (620 + 147, 85 + 147) para quedar en el centro exacto de la mesa
        batch.draw(imgTijera, 767, 232, 256, 256);

        // CAPA 4: INTERFAZ Y TEXTOS (ZONA IZQUIERDA)
        font.draw(batch, "=== INCREMENTAL CLIPPER ===", 50, 650);
        font.draw(batch, String.format(Locale.US, "Monedas: $%.2f", monedas), 50, 570);
        font.draw(batch, "Tijera: " + miTijera.obtenerNombre(), 50, 510);
        font.draw(batch, "Nivel: " + miTijera.obtenerNivel(), 50, 470);
        font.draw(batch, String.format(Locale.US, "Poder de corte: %.1f", miTijera.obtenerPoderCorte()), 50, 430);
        font.draw(batch, String.format(Locale.US, "Vida Papel: %.1f / %.1f", papelObjetivo.obtenerVidaActual(), papelObjetivo.obtenerVidaMaxima()), 50, 370);

        // CAPA 5: BOTONES
        batch.draw(imgBtnCortar, rectBtnCortar.x, rectBtnCortar.y, rectBtnCortar.width, rectBtnCortar.height);
        batch.draw(imgBtnMejorar, rectBtnMejorar.x, rectBtnMejorar.y, rectBtnMejorar.width, rectBtnMejorar.height);

        font.draw(batch, "CORTAR", rectBtnCortar.x + 45, rectBtnCortar.y + 45);
        font.draw(batch, "MEJORAR", rectBtnMejorar.x + 20, rectBtnMejorar.y + 50);
        font.draw(batch, String.format(Locale.US, "$%.2f", miTijera.obtenerCostoMejora()), rectBtnMejorar.x + 110, rectBtnMejorar.y + 25);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        imgBtnCortar.dispose();
        imgBtnMejorar.dispose();
        if (imgTijera != null) imgTijera.dispose();
        if (imgMesa != null) imgMesa.dispose();
        if (imgFondo != null) imgFondo.dispose();
    }
}