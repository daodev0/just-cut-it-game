package com.mijuego.infraestructura.adaptador.entrada.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public class EfectoEspiral implements Disposable {

    private final Texture imgEspiralBase;
    private float anguloRotacion = 0f;

    // Equilibrio de velocidad y apariencia
    private static final float VELOCIDAD_GIRO = 70f;
    private static final float TAMANO_PANTALLA = 2500f;
    private static final float OPACIDAD = 0.15f;

    public EfectoEspiral() {
        this.imgEspiralBase = crearEspiralBalanceada(1024);
    }

    public void actualizar(float delta) {
        anguloRotacion += VELOCIDAD_GIRO * delta;
        if (anguloRotacion >= 360f) {
            anguloRotacion -= 360f;
        }
    }

    public void render(SpriteBatch batch, float centroX, float centroY) {
        float x = centroX - (TAMANO_PANTALLA / 2f);
        float y = centroY - (TAMANO_PANTALLA / 2f);

        batch.setColor(0f, 0f, 0f, OPACIDAD);

        batch.draw(
                imgEspiralBase,
                x, y,
                TAMANO_PANTALLA / 2f, TAMANO_PANTALLA / 2f,
                TAMANO_PANTALLA, TAMANO_PANTALLA,
                1f, 1f,
                anguloRotacion,
                0, 0, imgEspiralBase.getWidth(), imgEspiralBase.getHeight(),
                false, false
        );

        batch.setColor(Color.WHITE);
    }

    private Texture crearEspiralBalanceada(int size) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        float centro = size / 2f;

        float grosorLinea = 40f;  // Grosor estandar perfecto
        float pasoVuelta = 60f;   // Espaciado cómodo entre espiras

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                float dx = x - centro;
                float dy = y - centro;
                float dist = (float) Math.sqrt(dx * dx + dy * dy);

                if (dist <= centro) {
                    float angulo = (float) Math.atan2(dy, dx);
                    if (angulo < 0) angulo += (2 * Math.PI);

                    float distEsperada = (angulo / (float)(2 * Math.PI)) * pasoVuelta;
                    float diff = Math.abs(((dist - distEsperada) % pasoVuelta));
                    if (diff > pasoVuelta / 2f) {
                        diff = pasoVuelta - diff;
                    }

                    if (diff < grosorLinea / 2f) {
                        float factorBorde = 1f - (diff / (grosorLinea / 2f));
                        pixmap.setColor(new Color(1f, 1f, 1f, factorBorde));
                        pixmap.drawPixel(x, y);
                    }
                }
            }
        }

        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();
        return texture;
    }

    @Override
    public void dispose() {
        if (imgEspiralBase != null) {
            imgEspiralBase.dispose();
        }
    }
}