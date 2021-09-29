/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.tjukica.ejb.sb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.foi.nwtis.tjukica.ejb.eb.Grupe;

/**
 *
 * @author Stalker
 */
@Stateless
public class GrupeFacade extends AbstractFacade<Grupe> implements GrupeFacadeLocal {

    @PersistenceContext(unitName = "NWTiS_DZ3_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GrupeFacade() {
        super(Grupe.class);
    }
    
}
