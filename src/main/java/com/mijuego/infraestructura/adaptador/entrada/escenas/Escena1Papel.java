package com.mijuego.infraestructura.adaptador.entrada.escenas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mijuego.aplicacion.puerto.entrada.PuertoCortarPapel;
import com.mijuego.aplicacion.puerto.entrada.PuertoIniciarPartida;
import com.mijuego.aplicacion.puerto.entrada.PuertoMejorarTijera;
import com.mijuego.aplicacion.puerto.salida.PuertoAudio;
import com.mijuego.infraestructura.adaptador.entrada.gui.GestorCursor;
import java.util.ArrayList;
import java.util.List;

public class Escena1Papel extends PantallaJuego {
    private static final String RUTA_CARPETA = "PapelCortado/";
    private static final int CANTIDAD_CORTES = 10;
    private static final float DURACION_FRAME = 0.075f;
    private static final float ANCHO_PAPEL = 168f;
    private static final float ALTO_PAPEL = 168f;
    private static final float TAMANO_TIJERAS = 88f;

    private final Rectangle zonaPapel = new Rectangle(802f, 325f, ANCHO_PAPEL, ALTO_PAPEL);
    private final Rectangle zonaTijeras = new Rectangle(1042f, 195f, TAMANO_TIJERAS, TAMANO_TIJERAS);
    private final List<Texture> cuadrosPapel = new ArrayList<>();

    private boolean tijerasSeleccionadas;
    private boolean animandoCorte;
    private int cuadroVisible;
    private int cuadroObjetivo;
    private float tiempoCuadro;

    public Escena1Papel(
            PuertoCortarPapel cortarPapel,
            PuertoMejorarTijera mejorarTijera,
            PuertoIniciarPartida iniciarPartida,
            GestorCursor gestorCursor,
            PuertoAudio audio) {
        super(cortarPapel, mejorarTijera, iniciarPartida, gestorCursor, audio);
    }

    @Override
    public void show() {
        super.show();
        audio.reproducirMusicaConFade("escena1", 1.5f, 1f);
        cuadrosPapel.add(cargarTextura(RUTA_CARPETA + "papel.png"));
        for (int indice = 1; indice <= CANTIDAD_CORTES; indice++) {
            cuadrosPapel.add(cargarTextura(RUTA_CARPETA + "papelCorte" + indice + ".png"));
        }
        for (Texture cuadro : cuadrosPapel) {
            cuadro.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
        aplicarFiltroRecursos();
    }

    private Texture cargarTextura(String ruta) {
        return new Texture(Gdx.files.internal(ruta));
    }

    @Override
    protected void actualizarEscena(float delta) {
        if (!animandoCorte) {
            return;
        }

        tiempoCuadro += delta;
        while (tiempoCuadro >= DURACION_FRAME && cuadroVisible < cuadroObjetivo) {
            tiempoCuadro -= DURACION_FRAME;
            cuadroVisible++;
        }

        if (cuadroVisible >= cuadroObjetivo) {
            animandoCorte = false;
            if (cuadroVisible == CANTIDAD_CORTES) {
                cuadroVisible = 0;
            }
        }
    }

    @Override
    protected void procesarEntradaEscena(float x, float y, boolean nuevoClick) {
        if (nuevoClick && !tijerasSeleccionadas && zonaTijeras.contains(x, y)) {
            tijerasSeleccionadas = true;
            agarrarTijeras(true);
        }

        if (nuevoClick && tijerasSeleccionadas && !animandoCorte && zonaPapel.contains(x, y)) {
            cuadroObjetivo = Math.min(cuadroVisible + 2, CANTIDAD_CORTES);
            tiempoCuadro = 0f;
            animandoCorte = true;
            if (registrarCorte() > 0f) {
                cuadroObjetivo = CANTIDAD_CORTES;
            }
        }

    }

    @Override
    protected void dibujarEscena(SpriteBatch batch) {
        dibujarFondo(batch);

        if (recursos.getMesa() != null) {
            batch.draw(recursos.getMesa(), 700f, 160f, 460f, 460f);
        }

        Texture papel = cuadrosPapel.get(cuadroVisible);
        batch.draw(papel, zonaPapel.x, zonaPapel.y, zonaPapel.width, zonaPapel.height);

        if (!tijerasSeleccionadas && recursos.getTijera() != null) {
            batch.draw(recursos.getTijera(), zonaTijeras.x, zonaTijeras.y, TAMANO_TIJERAS, TAMANO_TIJERAS);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        for (Texture cuadro : cuadrosPapel) {
            cuadro.dispose();
        }
        cuadrosPapel.clear();
    }
}
