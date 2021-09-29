package org.foi.nwtis.tjukica.web.slusaci;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.tjukica.ejb.eb.Airports;
import org.foi.nwtis.tjukica.ejb.eb.Myairports;
import org.foi.nwtis.tjukica.ejb.eb.Myairportslog;
import org.foi.nwtis.tjukica.ejb.sb.AirplanesFacadeLocal;
import org.foi.nwtis.tjukica.ejb.sb.AirportsFacadeLocal;
import org.foi.nwtis.tjukica.ejb.sb.MyairportsFacadeLocal;
import org.foi.nwtis.tjukica.ejb.sb.MyairportslogFacadeLocal;
import org.foi.nwtis.tjukica.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.tjukica.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.tjukica.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.tjukica.web.dretve.PreuzimanjeLetovaAvionaAerodromaDretva;




@WebListener
public class SlusacAplikacije implements ServletContextListener {
    
    @EJB
    AirportsFacadeLocal airportsFacade;
    @EJB
    MyairportsFacadeLocal myairportsFacade;
    @EJB
    MyairportslogFacadeLocal myairportslogFacade;
    @EJB
    AirplanesFacadeLocal airplanesFacade;
    
    List<Airports> airports = new ArrayList<>();
    List<Myairports> myairports = new ArrayList<>();
    List<Myairportslog> myairportslog = new ArrayList<>();
    
    @PostConstruct
    public void dohvatiPodatke() {
        airports = airportsFacade.findAll();
        myairports = myairportsFacade.findAll();
        myairportslog = myairportslogFacade.findAll();
    }
    
    PreuzimanjeLetovaAvionaAerodromaDretva plaa;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {

        ServletContext sc = sce.getServletContext();
        String datoteke = "Postavke.xml";
        String putanja = sc.getRealPath("WEB-INF") + File.separator + datoteke;
        try {
            BP_Konfiguracija konf = new BP_Konfiguracija(putanja);
            sc.setAttribute("BP_Konfig", konf);
            plaa = new PreuzimanjeLetovaAvionaAerodromaDretva(konf,airportsFacade,airplanesFacade, myairportsFacade, myairportslogFacade);
            plaa.start();
                     

        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("Aplikacija je pokrenuta!");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        
        System.out.println("Aplikacija je zaustavljana!");
    }
}
