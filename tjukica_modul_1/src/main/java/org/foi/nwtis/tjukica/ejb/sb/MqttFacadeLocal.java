/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.tjukica.ejb.sb;

import java.util.List;
import javax.ejb.Local;
import org.foi.nwtis.tjukica.ejb.eb.Mqtt;

/**
 *
 * @author Stalker
 */
@Local
public interface MqttFacadeLocal {

    void create(Mqtt mqtt);

    void edit(Mqtt mqtt);

    void remove(Mqtt mqtt);

    Mqtt find(Object id);

    List<Mqtt> findAll();

    List<Mqtt> findRange(int[] range);

    int count();
    
}
