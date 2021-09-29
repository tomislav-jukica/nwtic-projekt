/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.tjukica.web.zrna;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.podaci.Korisnik;
import org.foi.nwtis.rest.klijenti.LIQKlijent;
import org.foi.nwtis.rest.podaci.Adresa;
import org.foi.nwtis.rest.podaci.Lokacija;
import org.foi.nwtis.tjukica.ejb.eb.Airplanes;
import org.foi.nwtis.tjukica.ejb.eb.Airports;
import org.foi.nwtis.tjukica.ejb.eb.Myairports;
import org.foi.nwtis.tjukica.ejb.eb.Myairportslog;
import org.foi.nwtis.tjukica.ejb.sb.AirplanesFacadeLocal;
import org.foi.nwtis.tjukica.ejb.sb.AirportsFacadeLocal;
import org.foi.nwtis.tjukica.ejb.sb.MyairportsFacadeLocal;
import org.foi.nwtis.tjukica.ejb.sb.MyairportslogFacadeLocal;
import org.foi.nwtis.tjukica.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author Stalker
 */
@Named(value = "pogled3")
@ViewScoped
public class pogled3 implements Serializable{
    
    @Inject
    ServletContext context;    

    @EJB
    AirportsFacadeLocal airportsFacade;
    @EJB
    MyairportsFacadeLocal myairportsFacade;
    @EJB
    MyairportslogFacadeLocal myairportslogFacade;
    @EJB
    AirplanesFacadeLocal airplanesFacade;
    
    @Getter
    List<Airports> airports = new ArrayList<>();
    @Getter
    List<Myairports> myairports = new ArrayList<>();
    @Getter
    List<Myairportslog> myairportslog = new ArrayList<>();
    @Getter
    List<Myairports> mojiAerodromi = new ArrayList<>();
    @Getter
    List<Airports> aerodromiFiltrirani = new ArrayList<>();
    @Getter
    @Setter
    String aerodromNaziv;
    @Getter
    @Setter
    Airports odabraniAerodrom;
    
    Korisnik korisnik;
    /**
     * Creates a new instance of pogled3
     */
    public pogled3() {
        FacesContext context1 = FacesContext.getCurrentInstance(); 
        HttpSession session = (HttpSession) context1.getExternalContext().getSession(false);
        korisnik = (Korisnik) session.getAttribute("korisnik");
        if(korisnik == null) {
            try {
                context1.getExternalContext().redirect("index.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(pogled3.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @PostConstruct
    private void dohvatiPodatke() {
        airports = airportsFacade.findAll();
        myairports = myairportsFacade.findAll();
        myairportslog = myairportslogFacade.findAll();
    }
    
    public int brojPratitelja(String aerodrom) {
        int brojPratitelja = 0;
        for (int i = 0; i < myairports.size(); i++) {
            String a = myairports.get(i).getIdent().getIdent();
            if (myairports.get(i).getIdent().getIdent().equals(aerodrom)) {
                brojPratitelja++;
            }
        }
        return brojPratitelja;
    }
    
    public int brojPreuzetihDana(String aerodrom) {
        int brojDana = 0;
        for (int i = 0; i < myairportslog.size(); i++) {
            String a = myairportslog.get(i).getAirports().getIdent();
            if (a.equals(aerodrom)) {
                brojDana++;
            }
        }
        return brojDana;
    }
    
    public int brojPreuzetihLetova(Airports aerodrom) {
        int brojLetova = 0;
        List<Airplanes>  airplanes = airplanesFacade.dajLetoveAerodroma(aerodrom);
        brojLetova = airplanes.size();
        return brojLetova;        
    }
    
    public String geolokacijskiPodatci(Airports aerodrom) {
        String retVal = null;
        /*
        try {
            BP_Konfiguracija konf = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
            LIQKlijent klijent = new LIQKlijent(konf.getKonfig().dajPostavku("LocationIQ.token"));
            String pom = aerodrom.getCoordinates();
            String[] ks = pom.split(",");
            Lokacija lokacija = new Lokacija(ks[0], ks[1]);
            Adresa adresa = klijent.getAddress(lokacija);
            String encodedAdresa = URLEncoder.encode(adresa.getNaziv(), "UTF-8");
            Lokacija geolokacija = klijent.getGeoLocation(encodedAdresa);
            retVal =  geolokacija.toString();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(pogled3.class.getName()).log(Level.SEVERE, null, ex);
        }
*/
        return retVal;
    }
    
    public void filtrirajPoNazivu() {
        aerodromiFiltrirani = airportsFacade.dajAerodromePoNazivu(aerodromNaziv);
    }
    
    public List<Myairports> mojiAerodromi() {
        for(Myairports m : myairports) {
            String ime = m.getUsername().getKorIme();
            if(m.getUsername().getKorIme().equals(korisnik.getKorIme())) {
                mojiAerodromi.add(m);
            }
        }
        return mojiAerodromi;
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
}
