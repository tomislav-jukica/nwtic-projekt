/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.tjukica.ws.klijenti;

import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.tjukica.ws.serveri.Aerodrom;

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
}
