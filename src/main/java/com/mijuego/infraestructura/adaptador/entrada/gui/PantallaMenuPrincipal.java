package com.mijuego.infraestructura.adaptador.entrada.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mijuego.infraestructura.config.ConfiguracionJuego;

public class PantallaMenuPrincipal implements Screen {

    private final JuegoGUI juego;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Vector3 touchPoint;

    // Fuentes y layouts
    private BitmapFont font;
    private GlyphLayout glyphLayout;

    // Componente modular de la espiral
    private EfectoEspiral efectoEspiral;

    // Texturas UI y Fondo
    private Texture imgFondo;
    private Texture imgPapelPicado;
    private Texture imgLogo;
    private Texture imgMesa;
    private Texture imgPapel;
    private Texture imgTijera;
    private Texture imgOverlayNegro;
    private Texture imgFondoModal;
    private Texture imgFondoBoton;

    // Transición de entrada
    private float tiempoTransicion = 0f;
    private final float DURACION_TRANSICION = 0.9f;

    // Caída continua del fondo
    private float offsetY = 0;
    private final float VELOCIDAD_CAIDA = 60f;

    // Botones principales
    private Rectangle rectBtnJugar;
    private Rectangle rectBtnConfig;

    // Modal de Configuración
    private boolean mostrandoConfiguracion = false;
    private Rectangle rectCajaConfig;
    private Rectangle rectBtnCerrarConfig;
    private Rectangle rectBtnAlternarCursor;

    public PantallaMenuPrincipal(JuegoGUI juego) {
        this.juego = juego;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();

        viewport = new FitViewport(JuegoGUI.VIRTUAL_WIDTH, JuegoGUI.VIRTUAL_HEIGHT, camera);
        viewport.apply();
        camera.position.set(JuegoGUI.VIRTUAL_WIDTH / 2f, JuegoGUI.VIRTUAL_HEIGHT / 2f, 0);
        touchPoint = new Vector3();

        font = new BitmapFont();
        font.getData().setScale(1.2f);
        glyphLayout = new GlyphLayout();

        // Inicializar el efecto modular
        efectoEspiral = new EfectoEspiral(512);

        // Cargar Texturas
        imgFondo = new Texture(Gdx.files.internal("FondoMainMenu.png"));
        imgPapelPicado = new Texture(Gdx.files.internal("efecto-papel.png"));
        imgLogo = new Texture(Gdx.files.internal("logo.png"));
        imgMesa = new Texture(Gdx.files.internal("mesa.png"));
        imgPapel = new Texture(Gdx.files.internal("papel.png"));
        imgTijera = new Texture(Gdx.files.internal("tijera.png"));

        // Filtro Nearest para pixel art
        imgFondo.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        imgPapelPicado.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        imgLogo.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        imgMesa.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        imgPapel.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        imgTijera.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // Texturas auxiliares
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        imgOverlayNegro = new Texture(pixmap);

        pixmap.setColor(new Color(0.1f, 0.1f, 0.15f, 0.95f));
        pixmap.fill();
        imgFondoModal = new Texture(pixmap);

        pixmap.setColor(new Color(0.2f, 0.2f, 0.25f, 0.85f));
        pixmap.fill();
        imgFondoBoton = new Texture(pixmap);
        pixmap.dispose();

        // Hitboxes
        rectBtnJugar = new Rectangle(440, 100, 180, 70);
        rectBtnConfig = new Rectangle(660, 100, 180, 70);

        rectCajaConfig = new Rectangle(390, 160, 500, 360);
        rectBtnCerrarConfig = new Rectangle(830, 470, 40, 40);
        rectBtnAlternarCursor = new Rectangle(rectCajaConfig.x + 50, rectCajaConfig.y + 150, 400, 50);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (tiempoTransicion < DURACION_TRANSICION) {
            tiempoTransicion += delta;
        }

        // Actualizar lógica del efecto
        efectoEspiral.actualizar(delta);

        offsetY -= VELOCIDAD_CAIDA * delta;
        if (offsetY <= -JuegoGUI.VIRTUAL_HEIGHT) {
            offsetY = 0;
        }

        touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(touchPoint);

        if (tiempoTransicion >= DURACION_TRANSICION && Gdx.input.justTouched()) {
            if (mostrandoConfiguracion) {
                if (rectBtnCerrarConfig.contains(touchPoint.x, touchPoint.y)) {
                    mostrandoConfiguracion = false;
                } else if (rectBtnAlternarCursor.contains(touchPoint.x, touchPoint.y)) {
                    ConfiguracionJuego.getInstancia().alternarCursor();
                }
            } else {
                if (rectBtnJugar.contains(touchPoint.x, touchPoint.y)) {
                    juego.setScreen(new PantallaJuego(juego));
                } else if (rectBtnConfig.contains(touchPoint.x, touchPoint.y)) {
                    mostrandoConfiguracion = true;
                }
            }
        }

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // 1. Fondo base (Muro/Pared de atrás)
        batch.draw(imgFondo, 0, 0, JuegoGUI.VIRTUAL_WIDTH, JuegoGUI.VIRTUAL_HEIGHT);

        // 2. ESPIRAL (Ahora se dibuja antes del papel picado y la mesa)
        efectoEspiral.render(batch, JuegoGUI.VIRTUAL_WIDTH / 2f, JuegoGUI.VIRTUAL_HEIGHT / 2f);

        // 3. Lluvia de papel picado (Cae por encima de la espiral)
        batch.draw(imgPapelPicado, 0, offsetY, JuegoGUI.VIRTUAL_WIDTH, JuegoGUI.VIRTUAL_HEIGHT);
        batch.draw(imgPapelPicado, 0, offsetY + JuegoGUI.VIRTUAL_HEIGHT, JuegoGUI.VIRTUAL_WIDTH, JuegoGUI.VIRTUAL_HEIGHT);

        // 4. Mesa centrada
        float anchoMesa = 320f, altoMesa = 320f;
        float mesaX = (JuegoGUI.VIRTUAL_WIDTH - anchoMesa) / 2f;
        float mesaY = 200f;
        batch.draw(imgMesa, mesaX, mesaY, anchoMesa, altoMesa);

        // 5. Papel en la mesa
        float anchoPapel = 130f, altoPapel = 150f;
        float papelX = mesaX + (anchoMesa - anchoPapel) / 2f;
        float papelY = mesaY + (altoMesa - altoPapel) / 2f + 10f;
        batch.draw(imgPapel, papelX, papelY, anchoPapel, altoPapel);

        // 6. Tijeras
        batch.draw(imgTijera, papelX + 80f, mesaY + 50f, 85f, 85f);

        // 7. Logo del Juego
        float relacionAspectoLogo = (float) imgLogo.getWidth() / imgLogo.getHeight();
        float altoLogo = 180f;
        float anchoLogo = altoLogo * relacionAspectoLogo;
        batch.draw(imgLogo, (JuegoGUI.VIRTUAL_WIDTH - anchoLogo) / 2f, 480, anchoLogo, altoLogo);

        // 8. Botones Principales
        if (!mostrandoConfiguracion) {
            dibujarBotonConTexto(rectBtnJugar, "JUGAR");
            dibujarBotonConTexto(rectBtnConfig, "CONFIG");
        }

        // 9. Modal de Configuración
        if (mostrandoConfiguracion) {
            batch.draw(imgFondoModal, rectCajaConfig.x, rectCajaConfig.y, rectCajaConfig.width, rectCajaConfig.height);

            font.setColor(Color.GOLD);
            font.draw(batch, "CONFIGURACION", rectCajaConfig.x + 150, rectCajaConfig.y + 320);

            boolean hoverCerrar = rectBtnCerrarConfig.contains(touchPoint.x, touchPoint.y);
            font.setColor(hoverCerrar ? Color.YELLOW : Color.RED);
            font.draw(batch, "[X]", rectBtnCerrarConfig.x + 5, rectBtnCerrarConfig.y + 30);

            boolean usaPunto = ConfiguracionJuego.getInstancia().isUsarCursorPuntoBlanco();
            dibujarBotonConTexto(rectBtnAlternarCursor, "RATON: " + (usaPunto ? "PUNTO BLANCO" : "SISTEMA"));
        }

        // 10. Transición de entrada
        if (tiempoTransicion < DURACION_TRANSICION) {
            float alphaOverlay = MathUtils.clamp(1f - (tiempoTransicion / DURACION_TRANSICION), 0f, 1f);
            batch.setColor(1f, 1f, 1f, alphaOverlay);
            batch.draw(imgOverlayNegro, 0, 0, JuegoGUI.VIRTUAL_WIDTH, JuegoGUI.VIRTUAL_HEIGHT);
            batch.setColor(1f, 1f, 1f, 1f);
        }

        batch.end();
    }

    private void dibujarBotonConTexto(Rectangle bounds, String texto) {
        boolean estaHover = bounds.contains(touchPoint.x, touchPoint.y);
        batch.setColor(estaHover ? new Color(0.4f, 0.4f, 0.5f, 0.95f) : new Color(0.2f, 0.2f, 0.25f, 0.8f));
        batch.draw(imgFondoBoton, bounds.x, bounds.y, bounds.width, bounds.height);
        batch.setColor(Color.WHITE);

        glyphLayout.setText(font, texto);
        font.setColor(estaHover ? Color.YELLOW : Color.WHITE);
        font.draw(batch, texto, bounds.x + (bounds.width - glyphLayout.width) / 2f, bounds.y + (bounds.height + glyphLayout.height) / 2f);
    }

    @Override public void resize(int width, int height) { viewport.update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        efectoEspiral.dispose(); // Liberar la espiral
        imgFondo.dispose();
        imgPapelPicado.dispose();
        imgLogo.dispose();
        imgMesa.dispose();
        imgPapel.dispose();
        imgTijera.dispose();
        if (imgOverlayNegro != null) imgOverlayNegro.dispose();
        if (imgFondoModal != null) imgFondoModal.dispose();
        if (imgFondoBoton != null) imgFondoBoton.dispose();
    }
}