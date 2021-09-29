/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.tjukica.web.zrna;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.Korisnik;
import org.foi.nwtis.podaci.Odgovor;
import org.foi.nwtis.podaci.OdgovorAerodrom;
import org.foi.nwtis.tjukica.web.klijenti.App2_WS;
import org.foi.nwtis.tjukica.web.klijenti.App3_RS;

/**
 *
 * @author Stalker
 */
@Named(value = "pogled5")
@ViewScoped
public class pogled5 implements Serializable {
    
    @Getter
    @Setter
    List<Aerodrom> aerodromi = new ArrayList<>();
    @Getter
    @Setter
    List<org.foi.nwtis.tjukica.ws.serveri.Aerodrom> aerodromiFiltrirani = new ArrayList<>();
    @Getter
    @Setter
    String udaljenostOd;
    @Getter
    @Setter
    String udaljenostDo;
    @Getter
    @Setter
    String odabraniAerodrom;
    @Getter
    @Setter
    String aerodrom1 = "";
    @Getter
    @Setter
    String aerodrom2 = "";
    @Getter
    @Setter
    String udaljenost = "";
   

    Korisnik korisnik;
    
    public pogled5() {
        FacesContext context1 = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context1.getExternalContext().getSession(false);
        korisnik = (Korisnik) session.getAttribute("korisnik");
        if (korisnik == null) {
            try {
                context1.getExternalContext().redirect("index.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(pogled3.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void odjava() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
            session.removeAttribute("korisnik");
            context.getExternalContext().redirect("index.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(pogled1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<Aerodrom> dajVlastiteAerodrome() {
        List<Aerodrom> aerodromi = new ArrayList<>();
        App3_RS app3 = new App3_RS(korisnik.getKorIme(), korisnik.getLozinka());        
        OdgovorAerodrom odgovor = app3.dajAerodomeKorisnika(OdgovorAerodrom.class);
        aerodromi = Arrays.asList(odgovor.getOdgovor());
        return aerodromi;
    }
    
    public void dajAerodromePoUdaljenosti() {
        App2_WS app2 = new App2_WS();        
        aerodromiFiltrirani = app2.dajAerodromeFilter(
                korisnik.getKorIme(), 
                korisnik.getLozinka(), 
                odabraniAerodrom, 
                Integer.parseInt(udaljenostOd), 
                Integer.parseInt(udaljenostDo));
    }
    
    public void obrisiAerodrom() {
        App3_RS app3 = new App3_RS(korisnik.getKorIme(), korisnik.getLozinka());   
        Response odgovor = app3.izbrisiAerodromKorisnika(Odgovor.class, odabraniAerodrom);
    }
    
    public void obrisiLetove() {
        App3_RS app3 = new App3_RS(korisnik.getKorIme(), korisnik.getLozinka());
        Response odgovor = app3.izbrisiAvioneAerodroma(Odgovor.class, odabraniAerodrom);
    }
    public void izracunajUdaljenost() {
        udaljenost = "";
        App2_WS app2 = new App2_WS();        
        int broj = app2.dajUdaljenost(korisnik.getKorIme(), korisnik.getLozinka(), aerodrom1, aerodrom2);
        udaljenost = "Udaljenost: " + broj;
    }
    
}
