/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.tjukica.ejb.sb;

import java.util.List;
import javax.ejb.Local;
import org.foi.nwtis.tjukica.ejb.eb.Airplanes;
import org.foi.nwtis.tjukica.ejb.eb.Airports;

/**
 *
 * @author Stalker
 */
@Local
public interface AirplanesFacadeLocal {

    void create(Airplanes airplanes);

    void edit(Airplanes airplanes);

    void remove(Airplanes airplanes);

    Airplanes find(Object id);

    List<Airplanes> findAll();

    List<Airplanes> findRange(int[] range);

    int count();
    
    List<Airplanes> dajLetovePoAerodromu(Airports icao, int odVrijeme, int doVrijeme);
    
    List<Airplanes> dajLetovePoAvionu(String icao, int odVrijeme, int doVrijeme);
    
    List<Airplanes> dajLetoveAerodroma(Airports icao);
    
}
