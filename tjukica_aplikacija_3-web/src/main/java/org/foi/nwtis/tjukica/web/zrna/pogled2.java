/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.tjukica.web.zrna;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.podaci.Korisnik;
import org.foi.nwtis.tjukica.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author Stalker
 */
@Named(value = "pogled2")
@ViewScoped
public class pogled2 implements Serializable{

    @Inject
    ServletContext context;
    
    BP_Konfiguracija konf;
    
    @Getter
    @Setter
    String korIme;
    @Getter
    @Setter
    String lozinka;
    @Getter
    @Setter
    String status;
    @Getter
    @Setter
    String aerodromi;

    @Getter
    String registracijaOdgovor = "";
    @Getter
    String statusGrupe = "";
    @Getter
    String odjavaOdgovor = "";
    @Getter
    String aktivirajOdgovor = "";
    @Getter
    String blokirajOdgovor = "";
    @Getter
    String aerodromiOdgovor = "";
    
    String adresa = "localhost";
    int port = 9000; //TODO hardkodirano
    
    Korisnik korisnik;
    
    /**
     * Creates a new instance of pogled2
     */
    public pogled2() {
        //konf = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
        //port = Integer.parseInt(konf.getKonfig().dajPostavku("posluzitelj.port"));
        
        FacesContext context1 = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context1.getExternalContext().getSession(false);
        korisnik = (Korisnik) session.getAttribute("korisnik");
        if (korisnik == null) {
            try {
                context1.getExternalContext().redirect("index.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(pogled2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void odjava() {
        try {
            FacesContext context1 = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) context1.getExternalContext().getSession(false);
            session.removeAttribute("korisnik");
            context1.getExternalContext().redirect("index.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(pogled2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void registrirajGrupu() {
        registracijaOdgovor = "";
        try (
                Socket veza = new Socket(adresa, port);
                InputStreamReader inps = new InputStreamReader(veza.getInputStream(), "UTF8");
                OutputStreamWriter osw = new OutputStreamWriter(veza.getOutputStream(), "UTF8");) {

            String posalji = "KORISNIK " + korIme + "; LOZINKA " + lozinka + "; GRUPA PRIJAVI;";

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
            registracijaOdgovor = tekst.toString();
            veza.shutdownInput();
            veza.close();
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void provjeriStatusGrupe() {
        statusGrupe = "";
        try (
                Socket veza = new Socket(adresa, port);
                InputStreamReader inps = new InputStreamReader(veza.getInputStream(), "UTF8");
                OutputStreamWriter osw = new OutputStreamWriter(veza.getOutputStream(), "UTF8");) {

            String posalji = "KORISNIK " + korIme + "; LOZINKA " + lozinka + "; GRUPA STANJE;";

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
            if(tekst.toString().equals("OK 21;")) {
                statusGrupe = "OK 21; - Grupa je aktivna.";
            } else if (tekst.toString().equals("OK 22;")){
                statusGrupe = "OK 22; - Grupa je blokirana.";
            } else if (tekst.toString().equals("ERR 21;")) {
                statusGrupe = "ERR 21; - Grupa ne postoji.";
            } else {
                statusGrupe = "Greška prilikom dohvaćanja statusa grupe!";
            }
            veza.shutdownInput();
            veza.close();
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void odjaviGrupu() {
        odjavaOdgovor = "";
        try (
                Socket veza = new Socket(adresa, port);
                InputStreamReader inps = new InputStreamReader(veza.getInputStream(), "UTF8");
                OutputStreamWriter osw = new OutputStreamWriter(veza.getOutputStream(), "UTF8");) {

            String posalji = "KORISNIK " + korIme + "; LOZINKA " + lozinka + "; GRUPA ODJAVI;";

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
            odjavaOdgovor = tekst.toString();
            veza.shutdownInput();
            veza.close();
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void aktivirajGrupu() {
        aktivirajOdgovor = "";
        try (
                Socket veza = new Socket(adresa, port);
                InputStreamReader inps = new InputStreamReader(veza.getInputStream(), "UTF8");
                OutputStreamWriter osw = new OutputStreamWriter(veza.getOutputStream(), "UTF8");) {

            String posalji = "KORISNIK " + korIme + "; LOZINKA " + lozinka + "; GRUPA AKTIVIRAJ;";

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
            aktivirajOdgovor = tekst.toString();
            veza.shutdownInput();
            veza.close();
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void blokirajGrupu() {
        blokirajOdgovor = "";
        try (
                Socket veza = new Socket(adresa, port);
                InputStreamReader inps = new InputStreamReader(veza.getInputStream(), "UTF8");
                OutputStreamWriter osw = new OutputStreamWriter(veza.getOutputStream(), "UTF8");) {

            String posalji = "KORISNIK " + korIme + "; LOZINKA " + lozinka + "; GRUPA BLOKIRAJ;";

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
            blokirajOdgovor = tekst.toString();
            veza.shutdownInput();
            veza.close();
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void statusPauza() {
        try (
                Socket veza = new Socket(adresa, port);
                InputStreamReader inps = new InputStreamReader(veza.getInputStream(), "UTF8");
                OutputStreamWriter osw = new OutputStreamWriter(veza.getOutputStream(), "UTF8");) {

            String posalji = "KORISNIK " + korIme + "; LOZINKA " + lozinka + "; PAUZA;";

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
            if(tekst.toString().equals("OK 10;")) {
                status = "PAUZA";
            }
            veza.shutdownInput();
            veza.close();
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void statusRadi() {
        try (
                Socket veza = new Socket(adresa, port);
                InputStreamReader inps = new InputStreamReader(veza.getInputStream(), "UTF8");
                OutputStreamWriter osw = new OutputStreamWriter(veza.getOutputStream(), "UTF8");) {

            String posalji = "KORISNIK " + korIme + "; LOZINKA " + lozinka + "; RADI;";

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
            if(tekst.toString().equals("OK 10;")) {
                status = "RADI";
            }
            veza.shutdownInput();
            veza.close();
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void statusKraj() {
        try (
                Socket veza = new Socket(adresa, port);
                InputStreamReader inps = new InputStreamReader(veza.getInputStream(), "UTF8");
                OutputStreamWriter osw = new OutputStreamWriter(veza.getOutputStream(), "UTF8");) {

            String posalji = "KORISNIK " + korIme + "; LOZINKA " + lozinka + "; KRAJ;";

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
            if(tekst.toString().equals("OK 10;")) {
                status = "KRAJ";
            }
            veza.shutdownInput();
            veza.close();
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void provjeriStatus() {
        try (
                Socket veza = new Socket(adresa, port);
                InputStreamReader inps = new InputStreamReader(veza.getInputStream(), "UTF8");
                OutputStreamWriter osw = new OutputStreamWriter(veza.getOutputStream(), "UTF8");) {

            String posalji = "KORISNIK " + korIme + "; LOZINKA " + lozinka + "; STANJE;";

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
            if(tekst.toString().equals("OK 11;")) {
                status = "RADI";
            } else if (tekst.toString().equals("OK 12;")) {
                status = "PAUZA";
            } else if (tekst.toString().equals("KRAJ;")) {
                status = "KRAJ";
            }
            veza.shutdownInput();
            veza.close();
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void aerodromiGrupa() {
        aerodromiOdgovor = "";
        try (
                Socket veza = new Socket(adresa, port);
                InputStreamReader inps = new InputStreamReader(veza.getInputStream(), "UTF8");
                OutputStreamWriter osw = new OutputStreamWriter(veza.getOutputStream(), "UTF8");) {

            String posalji = "KORISNIK " + korIme + "; LOZINKA " + lozinka + "; GRUPA AERODROMI " + aerodromi + ";";

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
            aerodromiOdgovor = tekst.toString();
            veza.shutdownInput();
            veza.close();
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
