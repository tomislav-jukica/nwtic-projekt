/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.tjukica.rs.serveri;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.enterprise.context.RequestScoped;
import javax.jms.TextMessage;
import javax.ws.rs.DELETE;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.foi.nwtis.podaci.Odgovor;
import org.foi.nwtis.tjukica.ejb.eb.Airplanes;

import org.foi.nwtis.tjukica.ejb.eb.Airports;
import org.foi.nwtis.tjukica.ejb.eb.Myairports;
import org.foi.nwtis.tjukica.ejb.sb.AirplanesFacadeLocal;
import org.foi.nwtis.tjukica.ejb.sb.MyairportsFacadeLocal;
import org.foi.nwtis.tjukica.ws.klijenti.App2_WS;
import org.foi.nwtis.tjukica.ws.serveri.Aerodrom;
import org.foi.nwtis.tjukica.ws.serveri.Lokacija;

/**
 * REST Web Service
 *
 * @author Stalker
 */
@Path("rest")
@RequestScoped
public class App3Rest {

    @EJB
    MyairportsFacadeLocal myairportsFacade;
    @EJB
    AirplanesFacadeLocal airplanesFacade;

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of App3Rest
     */
    public App3Rest() {
    }

    /**
     * Retrieves representation of an instance of
     * org.foi.nwtis.tjukica.rs.serveri.App3Rest
     *
     * @param korisnik
     * @param lozinka
     * @return an instance of Response
     */
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response dajAerodomeKorisnika(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka) {

        List<Aerodrom> aerodromi;
        App2_WS aws = new App2_WS();
        aerodromi = aws.dajAerodromeKorisnika(korisnik, lozinka);
        if (aerodromi == null) {
            aerodromi = new ArrayList<>();
        }
        Odgovor odgovor = new Odgovor();
        odgovor.setStatus("10");
        odgovor.setPoruka("OK");
        odgovor.setOdgovor(aerodromi.toArray());

        return Response
                .ok(odgovor)
                .status(200)
                .build();
    }

    @Path("/{icao}")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response dajAerodomKorisnika(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao) {
        List<Myairports> myairports = myairportsFacade.findAll();
        Airports airport = null;
        for (int i = 0; i < myairports.size(); i++) {
            Myairports m = myairports.get(i);
            if (m.getUsername().getKorIme().equals(korisnik)
                    && m.getUsername().getLozinka().equals(lozinka)) {
                if (m.getIdent().getIdent().equals(icao)) {
                    airport = myairports.get(i).getIdent();
                }
            }
        }

        List<Aerodrom> aerodromi = new ArrayList<>();
        Odgovor odgovor = new Odgovor();
        if (airport != null) {
            Aerodrom a = new Aerodrom();
            a.setIcao(airport.getIdent());
            a.setNaziv(airport.getName());
            a.setDrzava(airport.getIsoCountry());

            String[] ks = airport.getCoordinates().split(",");
            Lokacija lokacija = new Lokacija();
            lokacija.setLatitude(ks[0]);
            lokacija.setLongitude(ks[1]);

            a.setLokacija(lokacija);
            aerodromi.add(a);

            odgovor.setStatus("10");
            odgovor.setPoruka("OK");
            odgovor.setOdgovor(aerodromi.toArray());
        } else {
            odgovor.setStatus("40");
            odgovor.setPoruka("Korisnik ne prati taj aerodrom.");
            odgovor.setOdgovor(aerodromi.toArray());
        }

        return Response
                .ok(odgovor)
                .status(200)
                .build();
    }

    @Path("/{icao}")
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response izbrisiAerodromKorisnika(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao) {
        List<Myairports> myairports = myairportsFacade.findAll();
        List<Airplanes> airplanes = new ArrayList<>();
        List<Aerodrom> aerodromi = new ArrayList<>();
        Myairports myairport = null;
        for (int i = 0; i < myairports.size(); i++) {
            Myairports m = myairports.get(i);
            if (m.getUsername().getKorIme().equals(korisnik)
                    && m.getUsername().getLozinka().equals(lozinka)) {
                if (m.getIdent().getIdent().equals(icao)) {
                    myairport = myairports.get(i);
                }
            }

        }
        Odgovor odgovor = new Odgovor();
        if (myairport != null) {
            airplanes = airplanesFacade.dajLetoveAerodroma(myairport.getIdent());
            if (airplanes.size() > 0) {
                odgovor.setStatus("40");
                odgovor.setPoruka("Aerodrom sadrži letove!");
                odgovor.setOdgovor(aerodromi.toArray());
            } else {
                myairportsFacade.remove(myairport);
                odgovor.setStatus("10");
                odgovor.setPoruka("OK");
                odgovor.setOdgovor(aerodromi.toArray());
            }
        } else {
            odgovor.setStatus("40");
            odgovor.setPoruka("Korisnik ne prati taj aerodrom!");
            odgovor.setOdgovor(aerodromi.toArray());
        }

        return Response
                .ok(odgovor)
                .status(200)
                .build();
    }

    @Path("/{icao}/avioni")
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response izbrisiAvioneAerodroma(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao) {
        List<Myairports> myairports = myairportsFacade.findAll();
        List<Airplanes> airplanes = new ArrayList<>();
        List<Aerodrom> aerodromi = new ArrayList<>();
        Myairports myairport = null;
        for (int i = 0; i < myairports.size(); i++) {
            Myairports m = myairports.get(i);
            if (m.getUsername().getKorIme().equals(korisnik)
                    && m.getUsername().getLozinka().equals(lozinka)) {
                if (m.getIdent().getIdent().equals(icao)) {
                    myairport = myairports.get(i);
                }
            }
        }
        Odgovor odgovor = new Odgovor();
        if (myairport != null) {
            airplanes = airplanesFacade.dajLetoveAerodroma(myairport.getIdent());
            for (Airplanes a : airplanes) {
                airplanesFacade.remove(a);
            }
            odgovor.setStatus("10");
            odgovor.setPoruka("OK");
            odgovor.setOdgovor(aerodromi.toArray());
        } else {
            myairportsFacade.remove(myairport);
            odgovor.setStatus("40");
            odgovor.setPoruka("Korisnik nema taj aerodrom!");
            odgovor.setOdgovor(aerodromi.toArray());
        }

        return Response
                .ok(odgovor)
                .status(200)
                .build();
    }

    @Path("/svi")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response dajAerodromeFilter(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @QueryParam("naziv") String naziv,
            @QueryParam("drzava") String drzava) {
        List<Aerodrom> aerodromiDrzava;
        List<Aerodrom> aerodromiNaziv;
        List<Aerodrom> aerodromi = new ArrayList<>();
        App2_WS aws = new App2_WS();
        if (naziv == null && drzava == null) {
            aerodromi = aws.dajAerodromePoNazivu(korisnik, lozinka, "%");
        } else if (naziv != null && drzava == null) {
            aerodromi = aws.dajAerodromePoNazivu(korisnik, lozinka, naziv);
        } else if (naziv == null && drzava != null) {
            aerodromi = aws.dajAerodromePoDrzavi(korisnik, lozinka, drzava);
        } else {
            aerodromiDrzava = aws.dajAerodromePoDrzavi(korisnik, lozinka, drzava);
            aerodromiNaziv = aws.dajAerodromePoNazivu(korisnik, lozinka, naziv);
            for (int i = 0; i < aerodromiDrzava.size(); i++) {
                aerodromi.add(aerodromiDrzava.get(i));
            }
            boolean postoji = false;
            for (int i = 0; i < aerodromiNaziv.size(); i++) {
                if (!aerodromi.contains(aerodromiNaziv.get(i))) {
                    aerodromi.add(aerodromiNaziv.get(i));
                }
            }
        }

        if (aerodromi == null) {
            aerodromi = new ArrayList<>();
        }
        Odgovor odgovor = new Odgovor();
        odgovor.setStatus("10");
        odgovor.setPoruka("OK");
        odgovor.setOdgovor(aerodromi.toArray());

        return Response
                .ok(odgovor)
                .status(200)
                .build();
    }
    
    @Path("/komande")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response saljiJMSPoruku(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @QueryParam("komanda") String komanda,
            @QueryParam("vrijeme") Timestamp vrijeme) {

        //TODO implementiraj
        
        return Response
                .ok()
                .status(200)
                .build();
    }
}
