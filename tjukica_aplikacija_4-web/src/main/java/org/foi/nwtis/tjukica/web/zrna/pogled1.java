/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.tjukica.web.zrna;

import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.podaci.Korisnik;
import org.foi.nwtis.tjukica.ejb.eb.Korisnici;
import org.foi.nwtis.tjukica.ejb.sb.KorisniciFacadeLocal;

/**
 *
 * @author Stalker
 */
@Named(value = "pogled1")
@SessionScoped
public class pogled1 implements Serializable {

    @EJB
    KorisniciFacadeLocal korisniciFacade;
    
    @Getter
    @Setter
    String korIme;
    @Getter
    @Setter
    String lozinka;
    
    @Getter
    @Setter
    Boolean prijavljen = false;
    
    @Getter
    String greskaKodPrijave = "";
    
    @Getter
    List<Korisnici> korisnici = new ArrayList<>();
    /**
     * Creates a new instance of pogled1
     */
    public pogled1() {
        
    }
    
    @PostConstruct
    private void dohvatiPodatke() {
        korisnici = korisniciFacade.findAll();
    }
    
    public void prijava() {
        greskaKodPrijave = "";
        FacesContext context = FacesContext.getCurrentInstance();
        for(Korisnici k : korisnici) {
            if(k.getKorIme().equals(korIme) && k.getLozinka().equals(lozinka)) {
                try {
                    prijavljen = true;
                    Korisnik korisnik = new Korisnik();
                    korisnik.setKorIme(korIme);
                    korisnik.setLozinka(lozinka);
                    HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
                    session.setAttribute("korisnik", korisnik);
                    context.getExternalContext().redirect("pogled3.xhtml");
                    break;
                } catch (IOException ex) {
                    Logger.getLogger(pogled1.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        if(prijavljen == false) {
            greskaKodPrijave = "Pogresno ime ili lozinka!";
        }
    }
    
    
    
}
