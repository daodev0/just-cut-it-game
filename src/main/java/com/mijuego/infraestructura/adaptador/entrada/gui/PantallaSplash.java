package com.mijuego.infraestructura.adaptador.entrada.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PantallaSplash implements Screen {

    private final JuegoGUI juego;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;

    private Texture imgLogoEstudio;
    private Texture imgOverlayNegro;

    // Tiempos
    private float tiempoTranscurrido = 0f;
    private final float DURACION_TOTAL = 5.0f;
    private final float RETARDO_INICIAL = 0.6f;
    private final float TIEMPO_BARRIDO = 1.2f; // Tiempo que tarda en "pintarse"
    private final float TIEMPO_FADE_OUT = 0.8f;

    // Rectángulos para la máscara de recorte
    private Rectangle clipBounds;
    private Rectangle scissors;

    private boolean iniciandoSalida = false;
    private float tiempoFadeOut = 0f;

    public PantallaSplash(JuegoGUI juego) {
        this.juego = juego;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();

        viewport = new FitViewport(JuegoGUI.VIRTUAL_WIDTH, JuegoGUI.VIRTUAL_HEIGHT, camera);
        viewport.apply();
        camera.position.set(JuegoGUI.VIRTUAL_WIDTH / 2f, JuegoGUI.VIRTUAL_HEIGHT / 2f, 0);

        imgLogoEstudio = new Texture(Gdx.files.internal("imgLogoEstudio.png"));
        imgLogoEstudio.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        imgOverlayNegro = new Texture(pixmap);
        pixmap.dispose();

        clipBounds = new Rectangle();
        scissors = new Rectangle();
    }

    @Override
    public void render(float delta) {
        float deltaAjustado = Math.min(delta, 1 / 30f);
        tiempoTranscurrido += deltaAjustado;

        if (Gdx.input.justTouched() && !iniciandoSalida) {
            iniciandoSalida = true;
        }

        if (tiempoTranscurrido >= (DURACION_TOTAL - TIEMPO_FADE_OUT)) {
            iniciandoSalida = true;
        }

        if (iniciandoSalida) {
            tiempoFadeOut += deltaAjustado;
            if (tiempoFadeOut >= TIEMPO_FADE_OUT) {
                juego.setScreen(new PantallaMenuPrincipal(juego));
                return;
            }
        }

        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Posición fija y tamaño del Logo
        float relacionAspecto = (float) imgLogoEstudio.getWidth() / imgLogoEstudio.getHeight();
        float altoDeseado = 110f;
        float anchoProporcional = altoDeseado * relacionAspecto;
        float posX = (JuegoGUI.VIRTUAL_WIDTH - anchoProporcional) / 2f;
        float posY = (JuegoGUI.VIRTUAL_HEIGHT - altoDeseado) / 2f;

        // Progreso del barrido (de 0.0 a 1.0)
        float progresoBarrido = 0f;
        if (tiempoTranscurrido > RETARDO_INICIAL) {
            progresoBarrido = (tiempoTranscurrido - RETARDO_INICIAL) / TIEMPO_BARRIDO;
        }
        progresoBarrido = MathUtils.clamp(progresoBarrido, 0f, 1f);

        // Si estamos omitiendo con clic, mostramos el logo completo de inmediato
        if (iniciandoSalida) {
            progresoBarrido = 1f;
        }

        // --- MÁSCARA DE RECORRIDO (BARRIDO DE ARRIBA HACIA ABAJO) ---
        float altoRevelado = altoDeseado * progresoBarrido;
        // La máscara empieza desde la parte superior del logo y crece hacia abajo
        clipBounds.set(posX, posY + (altoDeseado - altoRevelado), anchoProporcional, altoRevelado);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Convertir las coordenadas del mundo a píxeles de pantalla para OpenGL
        ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), clipBounds, scissors);

        batch.begin();

        // Aplicar máscara de recorte si hay algo que revelar
        boolean recortando = ScissorStack.pushScissors(scissors);

        if (recortando && altoRevelado > 0f) {
            batch.setColor(1f, 1f, 1f, 1f);
            batch.draw(imgLogoEstudio, posX, posY, anchoProporcional, altoDeseado);
            batch.flush(); // Enviar los comandos a la GPU antes de quitar la máscara
            ScissorStack.popScissors();
        }

        // Capa negra final de salida
        if (iniciandoSalida) {
            float alphaNegro = MathUtils.clamp(tiempoFadeOut / TIEMPO_FADE_OUT, 0f, 1f);
            batch.setColor(1f, 1f, 1f, alphaNegro);
            batch.draw(imgOverlayNegro, 0, 0, JuegoGUI.VIRTUAL_WIDTH, JuegoGUI.VIRTUAL_HEIGHT);
        }

        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        imgLogoEstudio.dispose();
        if (imgOverlayNegro != null) imgOverlayNegro.dispose();
    }
}