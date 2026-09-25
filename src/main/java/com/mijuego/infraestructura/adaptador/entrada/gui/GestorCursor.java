package com.mijuego.infraestructura.adaptador.entrada.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mijuego.infraestructura.config.ConfiguracionJuego;

public class GestorCursor {

    private final Texture imgPuntoBlanco;
    private final Vector3 posMouseWorld = new Vector3();
    private final float tamanioPunto = 10f;

    public GestorCursor() {
        Pixmap pixmap = new Pixmap(8, 8, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fillCircle(4, 4, 3);
        imgPuntoBlanco = new Texture(pixmap);
        pixmap.dispose();
    }

    public void render(SpriteBatch batch, Viewport viewport) {
        boolean usarPuntoBlanco = ConfiguracionJuego.getInstancia().isUsarCursorPuntoBlanco();

        // Control del cursor del SO
        Gdx.input.setCursorCatched(usarPuntoBlanco);

        if (usarPuntoBlanco) {
            posMouseWorld.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(posMouseWorld);

            batch.draw(imgPuntoBlanco,
                    posMouseWorld.x - (tamanioPunto / 2f),
                    posMouseWorld.y - (tamanioPunto / 2f),
                    tamanioPunto,
                    tamanioPunto
            );
        }
    }

    public void dispose() {
        if (imgPuntoBlanco != null) {
            imgPuntoBlanco.dispose();
        }
    }
}
