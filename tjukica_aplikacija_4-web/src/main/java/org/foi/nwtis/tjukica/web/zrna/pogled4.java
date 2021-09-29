/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.tjukica.web.zrna;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceRef;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.podaci.Korisnik;
import org.foi.nwtis.tjukica.web.klijenti.App2_WS;
import org.foi.nwtis.tjukica.ws.serveri.Aerodrom;
import org.foi.nwtis.tjukica.ws.serveri.App2_Service;
import org.foi.nwtis.tjukica.ws.serveri.AvionLeti;

/**
 *
 * @author Stalker
 */
@Named(value = "pogled4")
@ViewScoped
public class pogled4 implements Serializable {

    @Getter
    @Setter
    List<Aerodrom> mojiAerodromi = new ArrayList<>();

    @Getter
    List<AvionLeti> letovi = new ArrayList<>();

    @Getter
    @Setter
    String odVrijeme;
    @Getter
    @Setter
    String doVrijeme;
    @Getter
    @Setter
    String odabranAerodrom;
    @Getter
    @Setter
    String odabranAvion;

    Korisnik korisnik;

    public pogled4() {
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
        App2_WS app2 = new App2_WS();
        mojiAerodromi = app2.dajAerodromeKorisnika(korisnik.getKorIme(), korisnik.getLozinka());
        return mojiAerodromi;
    }

    public void dajLetovePoAerodromu() {
        try {
            App2_WS app2 = new App2_WS();
            Date dateOd = new SimpleDateFormat("dd.MM.yyyy hh:mm").parse(odVrijeme);
            Date dateDo = new SimpleDateFormat("dd.MM.yyyy hh:mm").parse(doVrijeme);
            long brojOdL =  dateOd.getTime() / 1000;
            long brojDoL =  dateDo.getTime() / 1000;
            int brojOd = (int) brojOdL;
            int brojDo = (int) brojDoL;
            letovi = app2.dajLetovePoAerodromu(korisnik.getKorIme(), korisnik.getLozinka(), odabranAerodrom, brojOd, brojDo);
        } catch (ParseException ex) {
            Logger.getLogger(pogled4.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void dajLetovePoAvionu() {
        try {
            App2_WS app2 = new App2_WS();
            Date dateOd = new SimpleDateFormat("dd.MM.yyyy hh:mm").parse(odVrijeme);
            Date dateDo = new SimpleDateFormat("dd.MM.yyyy hh:mm").parse(doVrijeme);
            long brojOdL =  dateOd.getTime() / 1000;
            long brojDoL =  dateDo.getTime() / 1000;
            int brojOd = (int) brojOdL;
            int brojDo = (int) brojDoL;
            letovi = app2.dajLetovePoAvionu(korisnik.getKorIme(), korisnik.getLozinka(), odabranAvion, brojOd, brojDo);
        } catch (ParseException ex) {
            Logger.getLogger(pogled4.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String dajDatum(int unix) {
        Date time = new Date((long)unix * 1000);
        return time.toString();
    }

}
