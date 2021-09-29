package org.foi.nwtis.tjukica.web.slusaci;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.tjukica.ejb.sb.MqttFacadeLocal;
import org.foi.nwtis.tjukica.konfiguracije.NeispravnaKonfiguracija;

import org.foi.nwtis.tjukica.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.tjukica.konfiguracije.bp.BP_Konfiguracija;



@WebListener
public class SlusacAplikacije implements ServletContextListener {
    
    @EJB
    MqttFacadeLocal mqttFacade;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        String datoteke = "Postavke.xml";
        String putanja = sc.getRealPath("WEB-INF") + File.separator + datoteke;
        try {
            BP_Konfiguracija konf = new BP_Konfiguracija(putanja);
            sc.setAttribute("BP_Konfig", konf);
            try {
                MqttListener listener = new MqttListener(konf, mqttFacade);
                listener.start();
            } catch (Exception ex) {
                Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
            }
            
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
