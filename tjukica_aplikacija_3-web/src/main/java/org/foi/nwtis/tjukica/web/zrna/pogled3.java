/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.tjukica.web.zrna;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.podaci.Korisnik;
import org.foi.nwtis.tjukica.ejb.eb.Mqtt;
import org.foi.nwtis.tjukica.ejb.sb.MqttFacadeLocal;

/**
 *
 * @author Stalker
 */
@Named(value = "pogled3")
@ViewScoped
public class pogled3 implements Serializable {

    
    @EJB
    MqttFacadeLocal mqttFacade;
    
    @Setter
    @Getter
    List<Mqtt> poruke = new ArrayList<>();
    
    Korisnik korisnik;
    
    /**
     * Creates a new instance of pogled3
     */
    public pogled3() {
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
    
    @PostConstruct
    private void dohvatiPodatke() {
        poruke = mqttFacade.findAll();
    }
    
    public void odjava() {
        try {
            FacesContext context1 = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) context1.getExternalContext().getSession(false);
            session.removeAttribute("korisnik");
            context1.getExternalContext().redirect("index.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(pogled3.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void obrisiSve() {
        System.out.println("Obrisi sve MQTT poruke!");
        for(Mqtt m : poruke) {
            mqttFacade.remove(m);
        }
    }
    
}
