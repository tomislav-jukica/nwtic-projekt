/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.tjukica.ejb.sb;


import java.util.Date;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import org.foi.nwtis.tjukica.ejb.eb.Myairportslog;

/**
 *
 * @author Stalker
 */
@Stateless
public class MyairportslogFacade extends AbstractFacade<Myairportslog> implements MyairportslogFacadeLocal {

    @PersistenceContext(unitName = "NWTiS_DZ3_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MyairportslogFacade() {
        super(Myairportslog.class);
    }
    
    @Override
    public Boolean postojiLog(String ident, Date datum) {
        //SELECT COUNT(ident) as ukupno FROM myairportslog WHERE ident='"
        //        + ident + "' AND flightdate='" + odVremena + "'";

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Myairportslog> myairportslog = cq.from(Myairportslog.class);
        cq.select(myairportslog);
        ParameterExpression<String> id = cb.parameter(String.class);
        ParameterExpression<Date> date = cb.parameter(Date.class);
        cq.where(cb.equal(myairportslog.get("myairportslogPK").get("ident"),
 id), cb.equal(myairportslog.get("myairportslogPK").get("flightdate"), 
date));
        TypedQuery<Myairportslog> q = em.createQuery(cq);
        q.setParameter(id, ident);
        q.setParameter(date, datum);
        Myairportslog mal;
        try {
            mal = q.getSingleResult();
        } catch(Exception e) {
            mal = null;
        }
        return mal != null;
        
        
        
        /*
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Myairportslog> log = cq.from(Myairportslog.class);        
        cq.select(log);
        ParameterExpression<String> a = cb.parameter(String.class);
        ParameterExpression<Date> b = cb.parameter(Date.class);
        cq.where(
                cb.equal(log.get("myairportslogPK").get("ident"), a),
                cb.equal(log.get("myairportslogPK").get("flightdate"), b)
        );
        TypedQuery<Myairportslog> q = em.createQuery(cq);
        q.setParameter(a, ident);
        q.setParameter(b, datum);
        //Autor koda: Domagoj Hamzić
        Myairportslog rezultat;
        try {
            rezultat = q.getSingleResult();
        } catch(Exception e) {
            rezultat = null;
        }
        return rezultat != null;
        //------------------------------
*/
    }
    
}
