package com.mijuego.infraestructura.adaptador.entrada.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class GestorRecursos {

    public Texture imgFondo;
    public Texture imgMesa;
    public Texture imgTijera;
    public Texture imgBtnCortar;
    public Texture imgBtnMejorar;
    public BitmapFont font;

    public void cargarRecursos() {
        font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.getData().setScale(1.5f);

        if (Gdx.files.internal("fondo2.png").exists()) {
            imgFondo = new Texture(Gdx.files.internal("fondo2.png"));
        }
        if (Gdx.files.internal("mesa.png").exists()) {
            imgMesa = new Texture(Gdx.files.internal("mesa.png"));
        }
        if (Gdx.files.internal("tijera.png").exists()) {
            imgTijera = new Texture(Gdx.files.internal("tijera.png"));
        } else {
            Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
            pixmap.setColor(1f, 0f, 1f, 1f);
            pixmap.fill();
            imgTijera = new Texture(pixmap);
            pixmap.dispose();
        }

        imgBtnCortar = crearTexturaColor(0.2f, 0.5f, 0.8f);
        imgBtnMejorar = crearTexturaColor(0.2f, 0.7f, 0.3f);
    }

    private Texture crearTexturaColor(float r, float g, float b) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(r, g, b, 1f);
        pixmap.fill();
        Texture tex = new Texture(pixmap);
        pixmap.dispose();
        return tex;
    }

    public void dispose() {
        font.dispose();
        if (imgBtnCortar != null) imgBtnCortar.dispose();
        if (imgBtnMejorar != null) imgBtnMejorar.dispose();
        if (imgTijera != null) imgTijera.dispose();
        if (imgMesa != null) imgMesa.dispose();
        if (imgFondo != null) imgFondo.dispose();
    }
}