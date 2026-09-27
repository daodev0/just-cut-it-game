package com.mijuego.infraestructura.adaptador.entrada.escenas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mijuego.aplicacion.puerto.salida.PuertoAudio;
import com.mijuego.infraestructura.adaptador.entrada.gui.JuegoGUI;

public class PantallaSplash implements Screen {

    private final JuegoGUI juego;
    private final PuertoAudio audio;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;

    private Texture imgLogoEstudio;
    private Texture imgOverlayNegro;

    // Tiempos
    private float tiempoTranscurrido = 0f;
    private final float DURACION_TOTAL = 5.0f;
    private final float RETARDO_INICIAL = 0.4f;
    private final float TIEMPO_CAIDA = 0.5f; // Tiempo que tarda en caer y aparecer
    private final float TIEMPO_FADE_OUT = 0.8f;

    // Control de sonido
    private boolean sonidoReproducido = false;

    private boolean iniciandoSalida = false;
    private float tiempoFadeOut = 0f;

    public PantallaSplash(JuegoGUI juego, PuertoAudio audio) {
        this.juego = juego;
        this.audio = audio;
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

        if (!sonidoReproducido && (tiempoTranscurrido > RETARDO_INICIAL || iniciandoSalida)) {
            audio.sonar("wosh");
            sonidoReproducido = true;
        }

        if (iniciandoSalida) {
            tiempoFadeOut += deltaAjustado;
        }

        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Dimensiones del logo
        float relacionAspecto = (float) imgLogoEstudio.getWidth() / imgLogoEstudio.getHeight();
        float altoDeseado = 110f;
        float anchoProporcional = altoDeseado * relacionAspecto;

        // Posición final (centrado en la pantalla)
        float posX = (JuegoGUI.VIRTUAL_WIDTH - anchoProporcional) / 2f;
        float posYFinal = (JuegoGUI.VIRTUAL_HEIGHT - altoDeseado) / 2f;

        // Arranca solo 50 o 60 píxeles por encima de su posición de destino en el centro
        float posYInicial = posYFinal + 20f;

        // Cálculo del progreso de caída (de 0.0 a 1.0)
        float progresoAnimacion = 0f;
        if (tiempoTranscurrido > RETARDO_INICIAL) {
            progresoAnimacion = (tiempoTranscurrido - RETARDO_INICIAL) / TIEMPO_CAIDA;
        }
        progresoAnimacion = MathUtils.clamp(progresoAnimacion, 0f, 1f);

        if (iniciandoSalida) {
            progresoAnimacion = 1f;
        }

        // Interpolación suave con desaceleración (Pow2Out / Decelerate)
        float progresoSuave = Interpolation.pow2Out.apply(progresoAnimacion);

        // Posición Y actual e Intensidad Alfa (Fade-In)
        float posYActual = MathUtils.lerp(posYInicial, posYFinal, progresoSuave);
        float alphaLogo = progresoAnimacion; // De 0.0 (invisible) a 1.0 (visible)

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Dibujar el logo deslizándose y cambiando su opacidad
        batch.setColor(1f, 1f, 1f, alphaLogo);
        batch.draw(imgLogoEstudio, posX, posYActual, anchoProporcional, altoDeseado);

        // Capa negra de transición de salida (Fade-Out hacia el menú)
        if (iniciandoSalida) {
            float alphaNegro = MathUtils.clamp(tiempoFadeOut / TIEMPO_FADE_OUT, 0f, 1f);
            batch.setColor(1f, 1f, 1f, alphaNegro);
            batch.draw(imgOverlayNegro, 0, 0, JuegoGUI.VIRTUAL_WIDTH, JuegoGUI.VIRTUAL_HEIGHT);
        }

        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();

        if (iniciandoSalida && tiempoFadeOut >= TIEMPO_FADE_OUT) {
            juego.setScreen(new PantallaMenuPrincipal(juego, juego.getAudio(), juego.getConfiguracion()));
        }
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