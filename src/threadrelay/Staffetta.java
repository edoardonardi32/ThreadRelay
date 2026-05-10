/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package threadrelay;

import java.util.ArrayList;

/**
 *
// * @author nardi
 */
public class Staffetta extends Thread {
    private int n;
    private int sleep;
    private Staffetta successivo; // Il corridore da sbloccare dopo
    private boolean start = false;
    private boolean running = true;
    private boolean paused = false;
    private ArrayList<Corridore> corridori = new ArrayList<>();

    public Staffetta(int n, int sleep) {
        this.n = n;
        this.sleep = sleep;
    }

    public void setSuccessivo(Staffetta successivo) {
        this.successivo = successivo;
    }

    public synchronized void sblocca() {
        this.start = true;
        this.notify();
    }

    public synchronized void setPausa(boolean b) {
        this.paused = b;
        if (!paused) this.notify(); // Sveglia il thread se togliamo la pausa
    }

    public void ferma() {
        this.running = false;
        this.interrupt();
    }

    public static interface Corridore {
        void update(int n, int valore);
    }

    public void addCorridore(Corridore corridore) {
        corridori.add(corridore);
    }

    private void notifyCorridori(int valore) {
        for (Corridore corridore : corridori) {
            corridore.update(n, valore);
        }
    }

    @Override
    public void run() {
        try {
            // 1. Attesa del testimone
            synchronized (this) {
                while (!start) {
                    this.wait();
                }
            }

            // 2. Corsa (da 0 a 100)
            for (int i = 0; i <= 100 && running; i++) {
                
                // Gestione Pausa
                synchronized (this) {
                    while (paused) {
                        this.wait();
                    }
                }

                notifyCorridori(i);

                // Sblocca il successivo al 90%
                if (i == 90 && successivo != null) {
                    successivo.sblocca();
                }

                Thread.sleep(sleep);
            }
        } catch (InterruptedException e) {
            // Thread interrotto (Reset)
        }
    }
}

