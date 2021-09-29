package org.foi.nwtis.tjukica.web.slusaci;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.tjukica.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.tjukica.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.tjukica.konfiguracije.bp.BP_Konfiguracija;

@WebListener
public class SlusacAplikacije implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            ServletContext sc = sce.getServletContext();
            String datoteke = "Postavke.xml";
            String putanja = sc.getRealPath("WEB-INF") + File.separator + datoteke;
            
            BP_Konfiguracija konf = new BP_Konfiguracija(putanja);
            sc.setAttribute("BP_Konfig", konf);
            
            System.out.println("Aplikacija je pokrenuta!");
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Aplikacija je zaustavljana!");
    }
}
