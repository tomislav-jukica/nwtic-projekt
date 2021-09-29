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
import org.foi.nwtis.tjukica.ejb.eb.Airplanes;
import org.foi.nwtis.tjukica.ejb.eb.Airports;

/**
 *
 * @author Stalker
 */
@Stateless
public class AirplanesFacade extends AbstractFacade<Airplanes> implements AirplanesFacadeLocal {

    @PersistenceContext(unitName = "NWTiS_DZ3_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AirplanesFacade() {
        super(Airplanes.class);
    }

    @Override
    public List<Airplanes> dajLetovePoAerodromu(Airports icao, int odVrijeme, int doVrijeme) {
        // SELECT * FROM Airplanes WHERE estdepartureairport = icao AND firsteen >= odVrijeme AND lastseen <= doVrijeme
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Airplanes> q = cb.createQuery(Airplanes.class);
        Root<Airplanes> a = q.from(Airplanes.class);

        q.select(a);
        ParameterExpression<Airports> u1 = cb.parameter(Airports.class);
        ParameterExpression<Integer> u2 = cb.parameter(Integer.class);
        ParameterExpression<Integer> u3 = cb.parameter(Integer.class);
        q.where(
            cb.and(
                cb.equal(a.get("estdepartureairport"), u1),
                cb.greaterThanOrEqualTo(a.get("firstseen"), u2),
                cb.lessThanOrEqualTo(a.get("lastseen"), u3)
            )
        );

        TypedQuery<Airplanes> qe = em.createQuery(q);
        qe.setParameter(u1, icao);
        qe.setParameter(u2, odVrijeme);
        qe.setParameter(u3, doVrijeme);
        List<Airplanes> airplanes = qe.getResultList();

        return airplanes;
    }

    @Override
    public List<Airplanes> dajLetovePoAvionu(String icao, int odVrijeme, int doVrijeme) {
        // SELECT * FROM Airplanes WHERE icao24 = icao AND firsteen >= odVrijeme AND lastseen <= doVrijeme
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Airplanes> q = cb.createQuery(Airplanes.class);
        Root<Airplanes> a = q.from(Airplanes.class);

        q.select(a);
        ParameterExpression<String> u1 = cb.parameter(String.class);
        ParameterExpression<Integer> u2 = cb.parameter(Integer.class);
        ParameterExpression<Integer> u3 = cb.parameter(Integer.class);
        q.where(
            cb.and(
                cb.equal(a.get("icao24"), u1),
                cb.greaterThanOrEqualTo(a.get("firstseen"), u2),
                cb.lessThanOrEqualTo(a.get("lastseen"), u3)
            )
        );

        TypedQuery<Airplanes> qe = em.createQuery(q);
        qe.setParameter(u1, icao);
        qe.setParameter(u2, odVrijeme);
        qe.setParameter(u3, doVrijeme);
        List<Airplanes> airplanes = qe.getResultList();

        return airplanes;
    }

    @Override
    public List<Airplanes> dajLetoveAerodroma(Airports icao) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Airplanes> q = cb.createQuery(Airplanes.class);
        Root<Airplanes> a = q.from(Airplanes.class);

        q.select(a);
        ParameterExpression<Airports> u1 = cb.parameter(Airports.class);

        q.where(cb.equal(a.get("estdepartureairport"), u1));

        TypedQuery<Airplanes> qe = em.createQuery(q);
        qe.setParameter(u1, icao);
        List<Airplanes> airplanes = qe.getResultList();

        return airplanes;
    }
}
