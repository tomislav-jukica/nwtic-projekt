/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.tjukica.web.zrna;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.podaci.Korisnik;
import org.foi.nwtis.tjukica.ejb.eb.Korisnici;
import org.foi.nwtis.tjukica.ejb.sb.KorisniciFacadeLocal;
import org.foi.nwtis.tjukica.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author Stalker
 */
@Named(value = "pogled1")
@SessionScoped
public class pogled1 implements Serializable {

    @Inject
    ServletContext context;

    BP_Konfiguracija konf;

    @EJB
    KorisniciFacadeLocal korisniciFacade;

    @Getter
    List<Korisnici> korisnici = new ArrayList<>();

    @Getter
    @Setter
    Boolean prijavljen = false;
    @Getter
    String greskaKodPrijave = "";
    @Getter
    String greskaKodRegistracije = "";

    @Getter
    @Setter
    String korIme;
    @Getter
    @Setter
    String lozinka;

    String adresa = "localhost";
    int port = 9000; //TODO hardkodirano

    Korisnik korisnik;

    /**
     * Creates a new instance of pogled1
     */
    public pogled1() {
        //konf = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
        //port = Integer.parseInt(konf.getKonfig().dajPostavku("posluzitelj.port"));
        FacesContext context1 = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context1.getExternalContext().getSession(false);
        korisnik = (Korisnik) session.getAttribute("korisnik");
        if (korisnik == null) {
            prijavljen = false;
        }
    }

    @PostConstruct
    private void dohvatiPodatke() {
        korisnici = korisniciFacade.findAll();
    }

    public void odjava() {
        try {
            FacesContext context1 = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) context1.getExternalContext().getSession(false);
            session.removeAttribute("korisnik");
            context1.getExternalContext().redirect("index.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(pogled1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void prijava() {
        greskaKodPrijave = "";

        try (
                Socket veza = new Socket(adresa, port);
                InputStreamReader inps = new InputStreamReader(veza.getInputStream(), "UTF8");
                OutputStreamWriter osw = new OutputStreamWriter(veza.getOutputStream(), "UTF8");) {

            String posalji = "KORISNIK " + korIme + "; LOZINKA " + lozinka + ";";

            osw.write(posalji);
            osw.flush();
            veza.shutdownOutput();
            StringBuilder tekst = new StringBuilder();
            while (true) {
                int i = inps.read();
                if (i == -1) {
                    break;
                }
                tekst.append((char) i);
            }
            System.out.println(tekst);
            if (tekst.toString().equals("OK 10;")) {
                try {
                    FacesContext context = FacesContext.getCurrentInstance();
                    prijavljen = true;
                    Korisnik korisnik = new Korisnik();
                    korisnik.setKorIme(korIme);
                    korisnik.setLozinka(lozinka);
                    HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
                    session.setAttribute("korisnik", korisnik);
                    context.getExternalContext().redirect("pogled2.xhtml");
                } catch (IOException ex) {
                    Logger.getLogger(pogled1.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                prijavljen = false;
                greskaKodPrijave = "Pogrešno ime ili lozinka!";
            }
            veza.shutdownInput();
            veza.close();
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void registracija() {
        greskaKodRegistracije = "";
        try (
                Socket veza = new Socket(adresa, port);
                InputStreamReader inps = new InputStreamReader(veza.getInputStream(), "UTF8");
                OutputStreamWriter osw = new OutputStreamWriter(veza.getOutputStream(), "UTF8");) {

            String posalji = "KORISNIK " + korIme + "; LOZINKA " + lozinka + "; DODAJ;";

            osw.write(posalji);
            osw.flush();
            veza.shutdownOutput();
            StringBuilder tekst = new StringBuilder();
            while (true) {
                int i = inps.read();
                if (i == -1) {
                    break;
                }
                tekst.append((char) i);
            }
            System.out.println(tekst);
            if (tekst.toString().equals("OK 10;")) {
                prijavljen = true;
                greskaKodRegistracije = "Korisnik registriran!";
            } else {
                prijavljen = false;
                greskaKodRegistracije = "Greška prilikom registracije!";
            }
            veza.shutdownInput();
            veza.close();
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
