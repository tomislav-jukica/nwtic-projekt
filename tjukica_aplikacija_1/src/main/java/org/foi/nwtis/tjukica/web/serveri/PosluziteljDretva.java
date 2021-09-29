/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.tjukica.web.serveri;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import org.foi.nwtis.tjukica.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.tjukica.web.dretve.ObradaKorisnikaDretva;
import org.foi.nwtis.tjukica.web.dretve.ObradaKorisnikaDretva;

/**
 *
 * @author Stalker
 */
public class PosluziteljDretva extends Thread {

    ServerSocket serverSocket = null;
    public static String status = "RADI";

    BP_Konfiguracija bpk;

    public PosluziteljDretva(BP_Konfiguracija konf) {
        System.out.println("Poslužitelj je pokrenut!");
        this.bpk = konf;
    }

    @Override
    public void interrupt() {
        if (!serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(PosluziteljDretva.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.interrupt();
    }

    @Override
    public void run() {
        int port = Integer.parseInt(bpk.getKonfig().dajPostavku("posluzitelj.port"));

        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                System.out.println("Veza otvorena na portu: " + port);
                System.out.println("Čekam na spajanje...");
                Socket veza = serverSocket.accept();

                Thread dretva = new ObradaKorisnikaDretva(veza, bpk);
                dretva.start();
                dretva.join();
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (InterruptedException ex) {
            Logger.getLogger(PosluziteljDretva.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }
}
