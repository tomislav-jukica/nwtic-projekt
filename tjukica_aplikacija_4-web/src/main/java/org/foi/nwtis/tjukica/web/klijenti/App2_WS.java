/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.tjukica.web.klijenti;

import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.tjukica.ws.serveri.Aerodrom;
import org.foi.nwtis.tjukica.ws.serveri.AvionLeti;

/**
 *
 * @author Stalker
 */
public class App2_WS {
    
    public List<Aerodrom> dajAerodromeKorisnika(String korisnik, String lozinka) {
        List<Aerodrom> aerodromi = new ArrayList<>();
        
        try { 
            org.foi.nwtis.tjukica.ws.serveri.App2_Service service = new org.foi.nwtis.tjukica.ws.serveri.App2_Service();
            org.foi.nwtis.tjukica.ws.serveri.App2 port = service.getApp2Port();
            aerodromi = port.dajVlastiteAerodrome(korisnik, lozinka);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return aerodromi;
    }
    
    public List<Aerodrom> dajAerodromePoNazivu(String korisnik, String lozinka, String naziv) {
        List<Aerodrom> aerodromi = new ArrayList<>();
        try { 
            org.foi.nwtis.tjukica.ws.serveri.App2_Service service = new org.foi.nwtis.tjukica.ws.serveri.App2_Service();
            org.foi.nwtis.tjukica.ws.serveri.App2 port = service.getApp2Port();
            aerodromi = port.dajAerodromePoNazivu(korisnik, lozinka, naziv);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return aerodromi;
    }
    
    public List<Aerodrom> dajAerodromePoDrzavi(String korisnik, String lozinka, String drzava) {
        List<Aerodrom> aerodromi = new ArrayList<>();
        
        try { 
            org.foi.nwtis.tjukica.ws.serveri.App2_Service service = new org.foi.nwtis.tjukica.ws.serveri.App2_Service();
            org.foi.nwtis.tjukica.ws.serveri.App2 port = service.getApp2Port();
            aerodromi = port.dajAerodromePoDrzavi(korisnik, lozinka, drzava);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return aerodromi;
    }
    
    public List<AvionLeti> dajLetovePoAerodromu(String korisnik, String lozinka, String icao, int odVrijeme, int doVrijeme) {
        List<AvionLeti> letovi = new ArrayList<>();        
        try {
            org.foi.nwtis.tjukica.ws.serveri.App2_Service service = new org.foi.nwtis.tjukica.ws.serveri.App2_Service();
            org.foi.nwtis.tjukica.ws.serveri.App2 port = service.getApp2Port();

            letovi = port.dajLetovePoAerodromu(korisnik, lozinka, icao, odVrijeme, doVrijeme);            
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return letovi;
    }
    
    public List<AvionLeti> dajLetovePoAvionu(String korisnik, String lozinka, String icao, int odVrijeme, int doVrijeme) {
        List<AvionLeti> letovi = new ArrayList<>();        
        try {
            org.foi.nwtis.tjukica.ws.serveri.App2_Service service = new org.foi.nwtis.tjukica.ws.serveri.App2_Service();
            org.foi.nwtis.tjukica.ws.serveri.App2 port = service.getApp2Port();

            letovi = port.dajLetovePoAvionu(korisnik, lozinka, icao, odVrijeme, doVrijeme);            
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return letovi;
    }
    
    public List<Aerodrom> dajAerodromeFilter(String korisnik, String lozinka, String icao, int odUdaljenost, int doUdaljenost) {
        List<Aerodrom> aerodromi = new ArrayList<>();        
        try { // Call Web Service Operation
            org.foi.nwtis.tjukica.ws.serveri.App2_Service service = new org.foi.nwtis.tjukica.ws.serveri.App2_Service();
            org.foi.nwtis.tjukica.ws.serveri.App2 port = service.getApp2Port();
            aerodromi = port.dajVlastitePoUdaljenosti(korisnik, lozinka, icao, odUdaljenost, doUdaljenost);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return aerodromi;
    }
    public int dajUdaljenost(String korisnik, String lozinka, String icao1, String icao2) {
        int udaljenost = 0;        
        try { // Call Web Service Operation
            org.foi.nwtis.tjukica.ws.serveri.App2_Service service = new org.foi.nwtis.tjukica.ws.serveri.App2_Service();
            org.foi.nwtis.tjukica.ws.serveri.App2 port = service.getApp2Port();
            udaljenost = port.dajUdaljenost(korisnik, lozinka, icao1, icao2);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return udaljenost;
    }
}
