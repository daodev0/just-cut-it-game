package com.mijuego.infraestructura.adaptador.entrada.escenas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mijuego.aplicacion.puerto.salida.PuertoAudio;
import com.mijuego.aplicacion.puerto.salida.PuertoConfiguracion;
import com.mijuego.infraestructura.adaptador.entrada.gui.EfectoEspiral;
import com.mijuego.infraestructura.adaptador.entrada.gui.JuegoGUI;

public class PantallaMenuPrincipal implements Screen {

    private final JuegoGUI juego;
    private final PuertoAudio audio;
    private final PuertoConfiguracion configuracion;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Vector3 touchPoint;

    // Fuentes y Layouts
    private BitmapFont fontTitulo;
    private BitmapFont fontTexto;
    private GlyphLayout glyphLayout;

    // Componente visual
    private EfectoEspiral efectoEspiral;

    // Texturas UI y Fondo
    private Texture imgFondo;
    private Texture imgPapelPicado;
    private Texture imgLogo;
    private Texture imgMesa;
    private Texture imgPapel;
    private Texture imgTijera;
    private Texture imgOverlayNegro;
    private Texture imgTransicionNegra;
    private Texture imgFondoModal;
    private Texture imgFondoBoton;
    private Texture imgLineaSeparadora;

    // Animación
    private float tiempoTransicion = 0f;
    private final float DURACION_TRANSICION = 0.9f;
    private float offsetY = 0;
    private final float VELOCIDAD_CAIDA = 60f;

    // Botones Principales
    private Rectangle rectBtnJugar;
    private Rectangle rectBtnConfig;

    // Modal de Configuración
    private boolean mostrandoConfiguracion = false;
    private Rectangle rectCajaConfig;
    private Rectangle rectBtnCerrarConfig;
    private Rectangle rectBtnAlternarCursor;
    private Rectangle rectBtnModoPantalla;

    // Controles de Audio
    private Rectangle rectSliderMusica;
    private Rectangle rectSliderSFX;
    private boolean arrastrandoSliderMusica;
    private boolean arrastrandoSliderSFX;

    // Controles de Sensibilidad
    private Rectangle rectBtnSensiMenos;
    private Rectangle rectBtnSensiMas;

    // Control de Hover
    private boolean hoverJugarAnterior = false;
    private boolean hoverConfigAnterior = false;
    private boolean hoverCerrarConfigAnterior = false;
    private boolean hoverAlternarCursorAnterior = false;
    private boolean hoverModoPantallaAnterior = false;
    private boolean hoverSliderMusicaAnterior = false;
    private boolean hoverSliderSFXAnterior = false;
    private boolean hoverSensiMenosAnterior = false;
    private boolean hoverSensiMasAnterior = false;

    public PantallaMenuPrincipal(JuegoGUI juego, PuertoAudio audio, PuertoConfiguracion configuracion) {
        this.juego = juego;
        this.audio = audio;
        this.configuracion = configuracion;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();

        viewport = new FitViewport(JuegoGUI.VIRTUAL_WIDTH, JuegoGUI.VIRTUAL_HEIGHT, camera);
        viewport.apply();
        camera.position.set(JuegoGUI.VIRTUAL_WIDTH / 2f, JuegoGUI.VIRTUAL_HEIGHT / 2f, 0);
        touchPoint = new Vector3();

        fontTitulo = new BitmapFont();
        float escalaFuentes = calcularEscalaFuentes();
        fontTitulo.getData().setScale(1.8f * escalaFuentes);

        fontTexto = new BitmapFont();
        fontTexto.getData().setScale(1.2f * escalaFuentes);

        glyphLayout = new GlyphLayout();
        efectoEspiral = new EfectoEspiral();

        imgFondo = new Texture(Gdx.files.internal("FondoMainMenu.png"));
        imgPapelPicado = new Texture(Gdx.files.internal("efecto-papel.png"));
        imgLogo = new Texture(Gdx.files.internal("logo.png"));
        imgMesa = new Texture(Gdx.files.internal("mesa.png"));
        imgPapel = new Texture(Gdx.files.internal("papel.png"));
        imgTijera = new Texture(Gdx.files.internal("tijera.png"));

        imgFondo.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        imgPapelPicado.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        imgLogo.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        imgMesa.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        imgPapel.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        imgTijera.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);

        pixmap.setColor(0f, 0f, 0f, 0.75f);
        pixmap.fill();
        imgOverlayNegro = new Texture(pixmap);

        pixmap.setColor(new Color(0.08f, 0.09f, 0.14f, 0.95f));
        pixmap.fill();
        imgFondoModal = new Texture(pixmap);

        pixmap.setColor(new Color(0.18f, 0.20f, 0.28f, 0.9f));
        pixmap.fill();
        imgFondoBoton = new Texture(pixmap);

        pixmap.setColor(new Color(0.3f, 0.35f, 0.45f, 0.6f));
        pixmap.fill();
        imgLineaSeparadora = new Texture(pixmap);

        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        imgTransicionNegra = new Texture(pixmap);

        pixmap.dispose();

        // DIMENSIONES Y POSICIONAMIENTO
        float anchoBoton = 200f;
        boolean esAndroid = Gdx.app.getType() == Application.ApplicationType.Android;
        float altoBoton = esAndroid ? 96f : 60f;
        float espacioEntreBotones = 40f;
        float centroX = JuegoGUI.VIRTUAL_WIDTH / 2f;

        rectBtnJugar = new Rectangle(centroX - anchoBoton - (espacioEntreBotones / 2f), 80, anchoBoton, altoBoton);
        rectBtnConfig = new Rectangle(centroX + (espacioEntreBotones / 2f), 80, anchoBoton, altoBoton);

        float modalAncho = 640f;
        float modalAlto = 580f;
        rectCajaConfig = new Rectangle((JuegoGUI.VIRTUAL_WIDTH - modalAncho) / 2f, (JuegoGUI.VIRTUAL_HEIGHT - modalAlto) / 2f, modalAncho, modalAlto);

        float tamanoCerrar = esAndroid ? 80f : 40f;
        float margenCerrar = esAndroid ? 90f : 50f;
        rectBtnCerrarConfig = new Rectangle(
                rectCajaConfig.x + rectCajaConfig.width - margenCerrar,
                rectCajaConfig.y + rectCajaConfig.height - margenCerrar,
                tamanoCerrar,
                tamanoCerrar);

        float controlY = rectCajaConfig.y + 420f;
        float controlWidth = 45f;

        // Fila 1: Música
        rectSliderMusica = new Rectangle(rectCajaConfig.x + 340f, controlY + 7f, 220f, 30f);

        // Fila 2: SFX
        controlY -= 60f;
        rectSliderSFX = new Rectangle(rectCajaConfig.x + 340f, controlY + 7f, 220f, 30f);

        // Fila 3: Sensibilidad
        controlY -= 60f;
        rectBtnSensiMenos = new Rectangle(rectCajaConfig.x + 380f, controlY, controlWidth, controlWidth);
        rectBtnSensiMas = new Rectangle(rectCajaConfig.x + 530f, controlY, controlWidth, controlWidth);

        // Fila 4: Modo Pantalla
        controlY -= 80f;
        rectBtnModoPantalla = new Rectangle(rectCajaConfig.x + 60f, controlY, modalAncho - 120f, 48f);

        // Fila 5: Estilo de Cursor
        controlY -= 60f;
        rectBtnAlternarCursor = new Rectangle(rectCajaConfig.x + 60f, controlY, modalAncho - 120f, 48f);

        audio.reproducirMusicaConFade("menu", 2.0f, 0.8f);
    }

    @Override
    public void render(float delta) {
        audio.actualizar(delta);

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (tiempoTransicion < DURACION_TRANSICION) {
            tiempoTransicion += delta;
        }

        efectoEspiral.actualizar(delta);

        offsetY -= VELOCIDAD_CAIDA * delta;
        if (offsetY <= -JuegoGUI.VIRTUAL_HEIGHT) {
            offsetY = 0;
        }

        // Obtener la coordenada unprojected respetando el Viewport virtual
        touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(touchPoint);

        if (!Gdx.input.isTouched()) {
            arrastrandoSliderMusica = false;
            arrastrandoSliderSFX = false;
        } else if (mostrandoConfiguracion && tiempoTransicion >= DURACION_TRANSICION) {
            if (Gdx.input.justTouched()) {
                arrastrandoSliderMusica = contieneSlider(rectSliderMusica, touchPoint.x, touchPoint.y);
                arrastrandoSliderSFX = contieneSlider(rectSliderSFX, touchPoint.x, touchPoint.y);
            }

            if (arrastrandoSliderMusica) {
                audio.setVolumenMusica(valorSlider(rectSliderMusica, touchPoint.x));
            } else if (arrastrandoSliderSFX) {
                audio.setVolumenSFX(valorSlider(rectSliderSFX, touchPoint.x));
            }
        }

        // HOVER AUDIO FEEDBACK
        if (tiempoTransicion >= DURACION_TRANSICION) {
            if (mostrandoConfiguracion) {
                boolean hoverCerrar = rectBtnCerrarConfig.contains(touchPoint.x, touchPoint.y);
                boolean hoverModoPant = rectBtnModoPantalla.contains(touchPoint.x, touchPoint.y);
                boolean hoverAlternar = rectBtnAlternarCursor.contains(touchPoint.x, touchPoint.y);
                boolean hoverSliderMusica = contieneSlider(rectSliderMusica, touchPoint.x, touchPoint.y);
                boolean hoverSliderSFX = contieneSlider(rectSliderSFX, touchPoint.x, touchPoint.y);
                boolean hoverSensiM = rectBtnSensiMenos.contains(touchPoint.x, touchPoint.y);
                boolean hoverSensiP = rectBtnSensiMas.contains(touchPoint.x, touchPoint.y);

                if ((hoverCerrar && !hoverCerrarConfigAnterior) ||
                        (hoverModoPant && !hoverModoPantallaAnterior) ||
                        (hoverAlternar && !hoverAlternarCursorAnterior) ||
                        (hoverSliderMusica && !hoverSliderMusicaAnterior) ||
                        (hoverSliderSFX && !hoverSliderSFXAnterior) ||
                        (hoverSensiM && !hoverSensiMenosAnterior) ||
                        (hoverSensiP && !hoverSensiMasAnterior)) {
                    audio.sonar("select");
                }

                hoverCerrarConfigAnterior = hoverCerrar;
                hoverModoPantallaAnterior = hoverModoPant;
                hoverAlternarCursorAnterior = hoverAlternar;
                hoverSliderMusicaAnterior = hoverSliderMusica;
                hoverSliderSFXAnterior = hoverSliderSFX;
                hoverSensiMenosAnterior = hoverSensiM;
                hoverSensiMasAnterior = hoverSensiP;
            } else {
                boolean hoverJugarActual = rectBtnJugar.contains(touchPoint.x, touchPoint.y);
                boolean hoverConfigActual = rectBtnConfig.contains(touchPoint.x, touchPoint.y);

                if ((hoverJugarActual && !hoverJugarAnterior) || (hoverConfigActual && !hoverConfigAnterior)) {
                    audio.sonar("select");
                }

                hoverJugarAnterior = hoverJugarActual;
                hoverConfigAnterior = hoverConfigActual;
            }
        }

        // CLICS / INTERACCIONES
        if (tiempoTransicion >= DURACION_TRANSICION && Gdx.input.justTouched()) {
            if (mostrandoConfiguracion) {
                if (rectBtnCerrarConfig.contains(touchPoint.x, touchPoint.y)) {
                    mostrandoConfiguracion = false;
                } else if (rectBtnModoPantalla.contains(touchPoint.x, touchPoint.y)) {
                    if (Gdx.app.getType() != Application.ApplicationType.Android) {
                        configuracion.alternarModoPantalla();
                    }
                } else if (rectBtnAlternarCursor.contains(touchPoint.x, touchPoint.y)) {
                    if (Gdx.app.getType() != Application.ApplicationType.Android) {
                        configuracion.alternarCursor();
                    }
                } else if (rectBtnSensiMenos.contains(touchPoint.x, touchPoint.y)) {
                    configuracion.setSensibilidadCursor(configuracion.getSensibilidadCursor() - 0.1f);
                    audio.sonar("select");
                } else if (rectBtnSensiMas.contains(touchPoint.x, touchPoint.y)) {
                    configuracion.setSensibilidadCursor(configuracion.getSensibilidadCursor() + 0.1f);
                    audio.sonar("select");
                }
            } else {
                if (rectBtnJugar.contains(touchPoint.x, touchPoint.y)) {
                    juego.mostrarPantallaJuego();
                } else if (rectBtnConfig.contains(touchPoint.x, touchPoint.y)) {
                    mostrandoConfiguracion = true;
                }
            }
        }

        viewport.apply();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Elementos de fondo
        batch.draw(imgFondo, 0, 0, JuegoGUI.VIRTUAL_WIDTH, JuegoGUI.VIRTUAL_HEIGHT);
        efectoEspiral.render(batch, JuegoGUI.VIRTUAL_WIDTH / 2f, JuegoGUI.VIRTUAL_HEIGHT / 2f);
        batch.draw(imgPapelPicado, 0, offsetY, JuegoGUI.VIRTUAL_WIDTH, JuegoGUI.VIRTUAL_HEIGHT);
        batch.draw(imgPapelPicado, 0, offsetY + JuegoGUI.VIRTUAL_HEIGHT, JuegoGUI.VIRTUAL_WIDTH, JuegoGUI.VIRTUAL_HEIGHT);

        float anchoMesa = 300f, altoMesa = 300f;
        float mesaX = (JuegoGUI.VIRTUAL_WIDTH - anchoMesa) / 2f;
        float mesaY = 170f;
        batch.draw(imgMesa, mesaX, mesaY, anchoMesa, altoMesa);

        float anchoPapel = 120f, altoPapel = 140f;
        float papelX = mesaX + (anchoMesa - anchoPapel) / 2f;
        float papelY = mesaY + (altoMesa - altoPapel) / 2f + 10f;
        batch.draw(imgPapel, papelX, papelY, anchoPapel, altoPapel);
        batch.draw(imgTijera, papelX + 75f, mesaY + 45f, 80f, 80f);

        float relacionAspectoLogo = (float) imgLogo.getWidth() / imgLogo.getHeight();
        float altoLogo = 170f;
        float anchoLogo = altoLogo * relacionAspectoLogo;
        batch.draw(imgLogo, (JuegoGUI.VIRTUAL_WIDTH - anchoLogo) / 2f, 490, anchoLogo, altoLogo);

        if (!mostrandoConfiguracion) {
            dibujarBotonConTexto(rectBtnJugar, "JUGAR", Color.GREEN);
            dibujarBotonConTexto(rectBtnConfig, "CONFIG", Color.ORANGE);
        } else {
            batch.draw(imgOverlayNegro, 0, 0, JuegoGUI.VIRTUAL_WIDTH, JuegoGUI.VIRTUAL_HEIGHT);
            batch.draw(imgFondoModal, rectCajaConfig.x, rectCajaConfig.y, rectCajaConfig.width, rectCajaConfig.height);

            // Título
            glyphLayout.setText(fontTitulo, "CONFIGURACIÓN");
            fontTitulo.setColor(Color.GOLD);
            fontTitulo.draw(batch, "CONFIGURACIÓN", rectCajaConfig.x + (rectCajaConfig.width - glyphLayout.width) / 2f, rectCajaConfig.y + rectCajaConfig.height - 30);

            batch.draw(imgLineaSeparadora, rectCajaConfig.x + 30, rectCajaConfig.y + rectCajaConfig.height - 60, rectCajaConfig.width - 60, 2);

            // Botón [X] Cerrar
            boolean hoverCerrar = rectBtnCerrarConfig.contains(touchPoint.x, touchPoint.y);
            fontTexto.setColor(hoverCerrar ? Color.RED : Color.LIGHT_GRAY);
            fontTexto.draw(batch, "X", rectBtnCerrarConfig.x + 12, rectBtnCerrarConfig.y + 28);

            // --- FILA 1: MÚSICA ---
            fontTexto.setColor(Color.WHITE);
            fontTexto.draw(batch, "Música de fondo", rectCajaConfig.x + 60, rectSliderMusica.y + 23);
            dibujarSlider(rectSliderMusica, audio.getVolumenMusica());

            // --- FILA 2: EFECTOS (SFX) ---
            fontTexto.setColor(Color.WHITE);
            fontTexto.draw(batch, "Efectos de sonido", rectCajaConfig.x + 60, rectSliderSFX.y + 23);
            dibujarSlider(rectSliderSFX, audio.getVolumenSFX());

            boolean esAndroid = Gdx.app.getType() == Application.ApplicationType.Android;

            // --- FILA 3: SENSIBILIDAD ---
            float sensiVal = configuracion.getSensibilidadCursor();
            String textoSensi = String.format("%.1fx", sensiVal);

            if (!esAndroid) {
                fontTexto.setColor(Color.WHITE);
                fontTexto.draw(batch, "Sensibilidad de cursor", rectCajaConfig.x + 60, rectBtnSensiMenos.y + 30);

                dibujarBotonConTexto(rectBtnSensiMenos, "-", Color.CYAN);
                fontTexto.setColor(Color.CYAN);
                glyphLayout.setText(fontTexto, textoSensi);
                fontTexto.draw(batch, textoSensi, rectCajaConfig.x + 430f + (55f - glyphLayout.width) / 2f, rectBtnSensiMenos.y + 30);
                dibujarBotonConTexto(rectBtnSensiMas, "+", Color.CYAN);
            }

            // Línea separadora inferior
            batch.draw(imgLineaSeparadora, rectCajaConfig.x + 40, rectBtnModoPantalla.y + 60, rectCajaConfig.width - 80, 2);

            // --- FILA 4: MODO DE PANTALLA ---
            boolean esFull = configuracion.isPantallaCompleta();
            String textoPantalla = esAndroid
                    ? "ORIENTACION: HORIZONTAL"
                    : "PANTALLA: " + (esFull ? "COMPLETA" : "VENTANA");
            dibujarBotonConTexto(rectBtnModoPantalla, textoPantalla, Color.CHARTREUSE);

            // --- FILA 5: ESTILO CURSOR ---
            if (esAndroid) {
                dibujarBotonConTexto(rectBtnAlternarCursor, "CONTROLES: TOCAR", Color.YELLOW);
            } else {
                boolean usaPunto = configuracion.isUsarCursorPuntoBlanco();
                dibujarBotonConTexto(rectBtnAlternarCursor, "ESTILO CURSOR: " + (usaPunto ? "PUNTO PIXEL" : "SISTEMA"), Color.YELLOW);
            }
        }

        if (tiempoTransicion < DURACION_TRANSICION) {
            float progreso = MathUtils.clamp(tiempoTransicion / DURACION_TRANSICION, 0f, 1f);
            float alphaOverlay = 1f - Interpolation.smooth.apply(progreso);
            batch.setColor(1f, 1f, 1f, alphaOverlay);
            batch.draw(imgTransicionNegra, 0, 0, JuegoGUI.VIRTUAL_WIDTH, JuegoGUI.VIRTUAL_HEIGHT);
            batch.setColor(1f, 1f, 1f, 1f);
        }

        batch.end();
    }

    private void dibujarBotonConTexto(Rectangle bounds, String texto, Color colorAcentoHover) {
        boolean estaHover = bounds.contains(touchPoint.x, touchPoint.y);

        if (estaHover) {
            batch.setColor(0.28f, 0.32f, 0.45f, 0.95f);
        } else {
            batch.setColor(0.15f, 0.17f, 0.24f, 0.85f);
        }

        batch.draw(imgFondoBoton, bounds.x, bounds.y, bounds.width, bounds.height);
        batch.setColor(Color.WHITE);

        glyphLayout.setText(fontTexto, texto);
        fontTexto.setColor(estaHover ? colorAcentoHover : Color.WHITE);
        fontTexto.draw(batch, texto, bounds.x + (bounds.width - glyphLayout.width) / 2f, bounds.y + (bounds.height + glyphLayout.height) / 2f);
    }

    private void dibujarSlider(Rectangle bounds, float valor) {
        float centroY = bounds.y + bounds.height / 2f;
        float anchoRelleno = bounds.width * valor;
        float posicionControl = bounds.x + anchoRelleno;

        batch.setColor(0.15f, 0.17f, 0.24f, 1f);
        batch.draw(imgFondoBoton, bounds.x, centroY - 4f, bounds.width, 8f);

        batch.setColor(Color.CYAN);
        batch.draw(imgLineaSeparadora, bounds.x, centroY - 4f, anchoRelleno, 8f);

        batch.setColor(Color.WHITE);
        batch.draw(imgFondoBoton, posicionControl - 9f, centroY - 10f, 18f, 20f);
        batch.setColor(Color.WHITE);
    }

    private boolean contieneSlider(Rectangle bounds, float x, float y) {
        float areaToque = Gdx.app.getType() == Application.ApplicationType.Android ? 48f : 18f;
        return x >= bounds.x - areaToque && x <= bounds.x + bounds.width + areaToque
                && Math.abs(y - (bounds.y + bounds.height / 2f)) <= areaToque;
    }

    private float valorSlider(Rectangle bounds, float x) {
        return MathUtils.clamp((x - bounds.x) / bounds.width, 0f, 1f);
    }

    private float calcularEscalaFuentes() {
        if (Gdx.app.getType() != Application.ApplicationType.Android) {
            return 1f;
        }
        float escalaViewport = Gdx.graphics.getHeight() / (float) JuegoGUI.VIRTUAL_HEIGHT;
        return Gdx.graphics.getDensity() / Math.max(escalaViewport, 0.01f);
    }

    @Override public void resize(int width, int height) { viewport.update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        fontTitulo.dispose();
        fontTexto.dispose();
        efectoEspiral.dispose();
        imgFondo.dispose();
        imgPapelPicado.dispose();
        imgLogo.dispose();
        imgMesa.dispose();
        imgPapel.dispose();
        imgTijera.dispose();
        if (imgOverlayNegro != null) imgOverlayNegro.dispose();
        if (imgTransicionNegra != null) imgTransicionNegra.dispose();
        if (imgFondoModal != null) imgFondoModal.dispose();
        if (imgFondoBoton != null) imgFondoBoton.dispose();
        if (imgLineaSeparadora != null) imgLineaSeparadora.dispose();
    }
}