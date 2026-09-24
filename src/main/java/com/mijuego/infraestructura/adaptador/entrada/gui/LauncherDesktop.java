package com.mijuego.infraestructura.adaptador.entrada.gui;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class LauncherDesktop {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Incremental Clipper");
        config.setWindowedMode(1280, 720);
        config.setForegroundFPS(60);
        config.setResizable(false);

        new Lwjgl3Application(new JuegoGUI(), config);
    }
}
