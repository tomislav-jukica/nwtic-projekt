/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.tjukica.ejb.sb;

import java.util.List;
import javax.ejb.Local;
import org.foi.nwtis.tjukica.ejb.eb.Dnevnik;

/**
 *
 * @author Stalker
 */
@Local
public interface DnevnikFacadeLocal {

    void create(Dnevnik dnevnik);

    void edit(Dnevnik dnevnik);

    void remove(Dnevnik dnevnik);

    Dnevnik find(Object id);

    List<Dnevnik> findAll();

    List<Dnevnik> findRange(int[] range);

    int count();
    
}
