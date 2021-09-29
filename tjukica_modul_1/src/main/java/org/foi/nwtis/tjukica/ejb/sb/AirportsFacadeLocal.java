/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.tjukica.ejb.sb;

import java.util.List;
import javax.ejb.Local;
import org.foi.nwtis.tjukica.ejb.eb.Airports;

/**
 *
 * @author Stalker
 */
@Local
public interface AirportsFacadeLocal {

    void create(Airports airports);

    void edit(Airports airports);

    void remove(Airports airports);

    Airports find(Object id);

    List<Airports> findAll();

    List<Airports> findRange(int[] range);

    int count();
    
    List<Airports> dajAerodromePoNazivu(String naziv);
    
    List<Airports> dajAerodromePoDrzavi(String drzava);
    
    Airports dajAerodromePoIcao(String icao);
    
}
