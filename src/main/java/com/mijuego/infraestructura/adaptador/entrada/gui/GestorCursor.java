package com.mijuego.infraestructura.adaptador.entrada.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mijuego.aplicacion.puerto.salida.PuertoConfiguracion;

public class GestorCursor implements Disposable {

    private final ShapeRenderer shapeRenderer;
    private final Vector3 posMouse;
    private final PuertoConfiguracion configuracion;
    private final Texture texturaTijeras;
    private boolean tijerasAgarradas;

    // Tamaños para el grid de píxeles
    private static final float TAMANO_PIXEL = 3.0f; // Escala de cada "píxel" del cursor

    /*
       Matriz del cursor Pixel Art (7x7 píxeles):
       0 = Transparente
       1 = Contorno Negro
       2 = Centro Blanco
    */
    private static final int[][] PATRON_CURSOR = {
            {0, 0, 1, 1, 1, 0, 0},
            {0, 1, 1, 2, 1, 1, 0},
            {1, 1, 2, 2, 2, 1, 1},
            {1, 2, 2, 2, 2, 2, 1},
            {1, 1, 2, 2, 2, 1, 1},
            {0, 1, 1, 2, 1, 1, 0},
            {0, 0, 1, 1, 1, 0, 0}
    };

    public GestorCursor(PuertoConfiguracion configuracion) {
        this.configuracion = configuracion;
        shapeRenderer = new ShapeRenderer();
        posMouse = new Vector3();
        texturaTijeras = new Texture(Gdx.files.internal("tijera.png"));
        texturaTijeras.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    public void render(SpriteBatch batch, Viewport viewport) {
        boolean esAndroid = Gdx.app.getType() == Application.ApplicationType.Android;
        boolean usarPuntoBlanco = configuracion.isUsarCursorPuntoBlanco();

        if (!esAndroid) {
            Gdx.graphics.setSystemCursor(usarPuntoBlanco || tijerasAgarradas
                    ? com.badlogic.gdx.graphics.Cursor.SystemCursor.None
                    : com.badlogic.gdx.graphics.Cursor.SystemCursor.Arrow);
        }

        if (tijerasAgarradas) {
            if (esAndroid && !Gdx.input.isTouched()) {
                return;
            }
            posMouse.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(posMouse);
            batch.draw(texturaTijeras, posMouse.x - 36f, posMouse.y - 36f, 96f, 96f);
            return;
        }

        if (esAndroid || !usarPuntoBlanco) {
            return;
        }

        // Obtener posición del ratón
        posMouse.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(posMouse);

        boolean batchEstabaDibujando = batch.isDrawing();
        if (batchEstabaDibujando) {
            batch.end();
        }

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Alineación pixel-perfect centrada en el puntero
        int filas = PATRON_CURSOR.length;
        int columnas = PATRON_CURSOR[0].length;

        float inicioX = posMouse.x - (columnas * TAMANO_PIXEL / 2f);
        float inicioY = posMouse.y - (filas * TAMANO_PIXEL / 2f);

        // Dibujar primero el contorno (1) y luego el centro (2)
        for (int paso = 1; paso <= 2; paso++) {
            if (paso == 1) shapeRenderer.setColor(Color.BLACK);
            if (paso == 2) shapeRenderer.setColor(Color.WHITE);

            for (int f = 0; f < filas; f++) {
                for (int c = 0; c < columnas; c++) {
                    if (PATRON_CURSOR[f][c] == paso) {
                        // Envertimos 'f' para que la matriz coincida con las coordenadas de pantalla Y-arriba
                        float px = inicioX + (c * TAMANO_PIXEL);
                        float py = inicioY + ((filas - 1 - f) * TAMANO_PIXEL);

                        shapeRenderer.rect(px, py, TAMANO_PIXEL, TAMANO_PIXEL);
                    }
                }
            }
        }

        shapeRenderer.end();

        if (batchEstabaDibujando) {
            batch.begin();
        }
    }

    public void setTijerasAgarradas(boolean agarradas) {
        tijerasAgarradas = agarradas;
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        texturaTijeras.dispose();
    }
}