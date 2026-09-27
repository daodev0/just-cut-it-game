package com.mijuego.infraestructura.adaptador.entrada.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class GestorRecursos {

    private final Texture imgFondo;
    private final Texture imgMesa;
    private final Texture imgTijera;
    private final Texture imgPanel;
    private final Texture imgMoneda;
    private final Texture imgBtnCortar;
    private final Texture imgBtnMejorar;
    private final BitmapFont font;

    public GestorRecursos() {
        font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.getData().setScale(1.5f);

        imgFondo = cargarTexturaSiExiste("fondo2.png");
        imgMesa = cargarTexturaSiExiste("mesa.png");
        if (Gdx.files.internal("tijera.png").exists()) {
            imgTijera = new Texture(Gdx.files.internal("tijera.png"));
        } else {
            Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
            pixmap.setColor(1f, 0f, 1f, 1f);
            pixmap.fill();
            imgTijera = new Texture(pixmap);
            pixmap.dispose();
        }

        imgPanel = crearTexturaColor(0.035f, 0.055f, 0.12f, 0.92f);
        imgMoneda = crearTexturaMoneda();
        imgBtnCortar = crearTexturaColor(0.2f, 0.5f, 0.8f);
        imgBtnMejorar = crearTexturaColor(0.2f, 0.7f, 0.3f);
    }

    private Texture cargarTexturaSiExiste(String ruta) {
        return Gdx.files.internal(ruta).exists() ? new Texture(Gdx.files.internal(ruta)) : null;
    }

    private Texture crearTexturaColor(float r, float g, float b) {
        return crearTexturaColor(r, g, b, 1f);
    }

    private Texture crearTexturaColor(float r, float g, float b, float a) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(r, g, b, a);
        pixmap.fill();
        Texture tex = new Texture(pixmap);
        pixmap.dispose();
        return tex;
    }

    private Texture crearTexturaMoneda() {
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.55f, 0.32f, 0.04f, 1f);
        pixmap.fillCircle(16, 16, 15);
        pixmap.setColor(1f, 0.72f, 0.12f, 1f);
        pixmap.fillCircle(16, 16, 12);
        pixmap.setColor(1f, 0.88f, 0.38f, 1f);
        pixmap.drawCircle(16, 16, 9);
        Texture textura = new Texture(pixmap);
        textura.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pixmap.dispose();
        return textura;
    }

    public Texture getFondo() {
        return imgFondo;
    }

    public Texture getMesa() {
        return imgMesa;
    }

    public Texture getTijera() {
        return imgTijera;
    }

    public Texture getPanel() {
        return imgPanel;
    }

    public Texture getMoneda() {
        return imgMoneda;
    }

    public Texture getBotonCortar() {
        return imgBtnCortar;
    }

    public Texture getBotonMejorar() {
        return imgBtnMejorar;
    }

    public BitmapFont getFont() {
        return font;
    }

    public void dispose() {
        font.dispose();
        if (imgBtnCortar != null) imgBtnCortar.dispose();
        if (imgBtnMejorar != null) imgBtnMejorar.dispose();
        if (imgTijera != null) imgTijera.dispose();
        if (imgMesa != null) imgMesa.dispose();
        if (imgFondo != null) imgFondo.dispose();
        imgPanel.dispose();
        imgMoneda.dispose();
    }
}