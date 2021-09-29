/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.tjukica.ejb.sb;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.Lokacija;
import org.foi.nwtis.tjukica.ejb.eb.Airports;

/**
 *
 * @author Stalker
 */
@Stateless
public class AirportsFacade extends AbstractFacade<Airports> implements AirportsFacadeLocal {

    @PersistenceContext(unitName = "NWTiS_DZ3_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AirportsFacade() {
        super(Airports.class);
    }
    
    @Override
    public List<Airports> dajAerodromePoNazivu(String naziv) {
        //SELECT * FROM Airports WHERE Airports.name LIKE 
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Airports> q = cb.createQuery(Airports.class);        
        Root<Airports> a = q.from(Airports.class);
        
        ParameterExpression<String> u = cb.parameter(String.class);
        q.select(a).where(cb.like(a.get("name"), u));
        TypedQuery<Airports> qe = em.createQuery(q);
        qe.setParameter(u, naziv);
        List<Airports> airports = qe.getResultList();      
        
        return airports;
    }
    
    @Override
    public List<Airports> dajAerodromePoDrzavi(String drzava) {
        //SELECT * FROM Airports WHERE Airports.name LIKE 
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Airports> q = cb.createQuery(Airports.class);        
        Root<Airports> a = q.from(Airports.class);
        
        ParameterExpression<String> u = cb.parameter(String.class);
        q.select(a).where(cb.equal(a.get("isoCountry"), u));
        TypedQuery<Airports> qe = em.createQuery(q);
        qe.setParameter(u, drzava);
        List<Airports> airports = qe.getResultList();      
        
        return airports;
    }
    
    @Override
    public Airports dajAerodromePoIcao(String naziv) {
        //SELECT * FROM Airports WHERE Airports.ident = naziv
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Airports> q = cb.createQuery(Airports.class);        
        Root<Airports> a = q.from(Airports.class);
        
        ParameterExpression<String> u = cb.parameter(String.class);
        q.select(a).where(cb.equal(a.get("ident"), u));
        TypedQuery<Airports> qe = em.createQuery(q);
        qe.setParameter(u, naziv);
        Airports airports;
        try {
            airports = qe.getSingleResult();   
        } catch (Exception e) {
            airports = null;
        }
        return airports;
    }
}
