/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.tjukica.ws.serveri;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.Lokacija;
import org.foi.nwtis.tjukica.ejb.eb.Airplanes;
import org.foi.nwtis.tjukica.ejb.eb.Airports;
import org.foi.nwtis.tjukica.ejb.eb.Korisnici;
import org.foi.nwtis.tjukica.ejb.eb.Myairports;
import org.foi.nwtis.tjukica.ejb.sb.AirplanesFacadeLocal;
import org.foi.nwtis.tjukica.ejb.sb.AirportsFacadeLocal;
import org.foi.nwtis.tjukica.ejb.sb.KorisniciFacadeLocal;
import org.foi.nwtis.tjukica.ejb.sb.MyairportsFacadeLocal;
import org.foi.nwtis.tjukica.podatci.Koordinata;

/**
 *
 * @author Stalker
 */
@WebService(serviceName = "App2")
public class App2 {

    @EJB
    AirportsFacadeLocal airportsFacade;
    @EJB
    MyairportsFacadeLocal myairportsFacade;
    @EJB
    AirplanesFacadeLocal airplanesFacade;
    @EJB
    KorisniciFacadeLocal korisniciFacade;

    private Boolean autentificiraj(String korIme, String lozinka) {
        List<Korisnici> korisnici = new ArrayList<>();
        korisnici = korisniciFacade.findAll();
        for (Korisnici k : korisnici) {
            if (k.getKorIme().equals(korIme) && k.getLozinka().equals(lozinka)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Web service operation
     *
     * @param korisnik
     * @param lozinka
     * @param naziv
     * @return
     */
    @WebMethod(operationName = "dajAerodromePoNazivu")
    public List<Aerodrom> dajAerodromePoNazivu(
            @WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "naziv") String naziv) {
        if (autentificiraj(korisnik, lozinka)) {
            //TODO logiraj
            List<Aerodrom> aerodromi = new ArrayList<>();
            List<Airports> airports = airportsFacade.dajAerodromePoNazivu("%" + naziv + "%");

            for (int i = 0; i < airports.size(); i++) {
                Airports b = airports.get(i);
                String[] ks = b.getCoordinates().split(",");
                Lokacija lokacija = new Lokacija(ks[0].trim(), ks[1].trim());
                Aerodrom noviAerodrom = new Aerodrom(b.getIdent(), b.getName(), b.getIsoCountry(), lokacija);
                aerodromi.add(noviAerodrom);
            }
            if (aerodromi.isEmpty()) {
                return null;
            }
            return aerodromi;
        }
        return null;
    }

    /**
     * Web service operation
     *
     * @param korisnik
     * @param lozinka
     * @param drzava
     * @return
     */
    @WebMethod(operationName = "dajAerodromePoDrzavi")
    public List<Aerodrom> dajAerodromePoDrzavi(
            @WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "drzava") String drzava) {
        if (autentificiraj(korisnik, lozinka)) {
            //TODO logiraj
            List<Aerodrom> aerodromi = new ArrayList<>();
            List<Airports> airports = airportsFacade.dajAerodromePoDrzavi(drzava);

            for (int i = 0; i < airports.size(); i++) {
                Airports b = airports.get(i);
                String[] ks = b.getCoordinates().split(",");
                Lokacija lokacija = new Lokacija(ks[0].trim(), ks[1].trim());
                Aerodrom noviAerodrom = new Aerodrom(b.getIdent(), b.getName(), b.getIsoCountry(), lokacija);
                aerodromi.add(noviAerodrom);
            }
            if (aerodromi.isEmpty()) {
                return null;
            }
            return aerodromi;
        }
        return null;
    }

    /**
     * Web service operation
     *
     * @param korisnik
     * @param lozinka
     * @return
     */
    @WebMethod(operationName = "dajVlastiteAerodrome")
    public List<Aerodrom> dajVlastiteAerodrome(
            @WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka) {
        if (autentificiraj(korisnik, lozinka)) {
            //TODO logiraj
            List<Aerodrom> aerodromi = new ArrayList<>();
            List<Myairports> myairports = myairportsFacade.findAll();

            for (int i = 0; i < myairports.size(); i++) {
                Myairports ma = myairports.get(i);
                if (ma.getUsername().getKorIme().equals(korisnik)) {
                    Airports b = ma.getIdent();
                    String[] ks = b.getCoordinates().split(",");
                    Lokacija lokacija = new Lokacija(ks[0].trim(), ks[1].trim());
                    Aerodrom noviAerodrom = new Aerodrom(b.getIdent(), b.getName(), b.getIsoCountry(), lokacija);
                    aerodromi.add(noviAerodrom);
                }
            }
            if (aerodromi.isEmpty()) {
                return null;
            }
            return aerodromi;
        }
        return null;
    }

    /**
     * Web service operation
     *
     * @param korisnik
     * @param doVrijeme
     * @param lozinka
     * @param odVrijeme
     * @param icao
     * @return
     */
    @WebMethod(operationName = "dajLetovePoAerodromu")
    public List<AvionLeti> dajLetovePoAerodromu(
            @WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "icao") String icao,
            @WebParam(name = "odVrijeme") int odVrijeme,
            @WebParam(name = "doVrijeme") int doVrijeme) {
        if (autentificiraj(korisnik, lozinka)) {
            //TODO logiraj
            List<AvionLeti> letovi = new ArrayList<>();
            List<Airports> airports = airportsFacade.findAll();
            Airports aerodrom = null;
            for (Airports a : airports) {
                if (a.getIdent().equals(icao)) {
                    aerodrom = a;
                    break;
                }
            }
            if (aerodrom == null) {
                return null;
            }
            List<Airplanes> avioni = airplanesFacade.dajLetovePoAerodromu(aerodrom, odVrijeme, doVrijeme);
            for (int i = 0; i < avioni.size(); i++) {
                Airplanes a = avioni.get(i);
                AvionLeti let = new AvionLeti();
                let.setArrivalAirportCandidatesCount(a.getArrivalairportcandidatescount());
                let.setCallsign(a.getCallsign());
                let.setDepartureAirportCandidatesCount(a.getDepartureairportcandidatescount());
                let.setEstArrivalAirport(a.getEstarrivalairport());
                let.setEstArrivalAirportHorizDistance(a.getEstarrivalairporthorizdistance());
                let.setEstArrivalAirportVertDistance(a.getEstarrivalairportvertdistance());
                let.setEstDepartureAirport(a.getEstdepartureairport().getIdent());
                let.setEstDepartureAirportHorizDistance(a.getEstdepartureairporthorizdistance());
                let.setEstDepartureAirportVertDistance(a.getEstdepartureairportvertdistance());
                let.setFirstSeen(a.getFirstseen());
                let.setIcao24(a.getIcao24());
                let.setLastSeen(a.getLastseen());
                letovi.add(let);
            }
            if (letovi.isEmpty()) {
                return null;
            }
            return letovi;
        }
        return null;
    }

    /**
     * Web service operation
     *
     * @param korisnik
     * @param doVrijeme
     * @param lozinka
     * @param odVrijeme
     * @param icao
     * @return
     */
    @WebMethod(operationName = "dajLetovePoAvionu")
    public List<AvionLeti> dajLetovePoAvionu(
            @WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "icao") String icao,
            @WebParam(name = "odVrijeme") int odVrijeme,
            @WebParam(name = "doVrijeme") int doVrijeme) {
        if (autentificiraj(korisnik, lozinka)) {
            //TODO logiraj
            List<AvionLeti> letovi = new ArrayList<>();
            List<Airplanes> avioni = airplanesFacade.dajLetovePoAvionu(icao, odVrijeme, doVrijeme);
            for (int i = 0; i < avioni.size(); i++) {
                Airplanes a = avioni.get(i);
                AvionLeti let = new AvionLeti();
                let.setArrivalAirportCandidatesCount(a.getArrivalairportcandidatescount());
                let.setCallsign(a.getCallsign());
                let.setDepartureAirportCandidatesCount(a.getDepartureairportcandidatescount());
                let.setEstArrivalAirport(a.getEstarrivalairport());
                let.setEstArrivalAirportHorizDistance(a.getEstarrivalairporthorizdistance());
                let.setEstArrivalAirportVertDistance(a.getEstarrivalairportvertdistance());
                let.setEstDepartureAirport(a.getEstdepartureairport().getIdent());
                let.setEstDepartureAirportHorizDistance(a.getEstdepartureairporthorizdistance());
                let.setEstDepartureAirportVertDistance(a.getEstdepartureairportvertdistance());
                let.setFirstSeen(a.getFirstseen());
                let.setIcao24(a.getIcao24());
                let.setLastSeen(a.getLastseen());
                letovi.add(let);
            }
            if (letovi.isEmpty()) {
                return null;
            }
            return letovi;
        }
        return null;
    }

    /**
     *
     * @param korisnik
     * @param lozinka
     * @param icao1
     * @param icao2
     * @return
     */
    @WebMethod(operationName = "dajUdaljenost")
    public int dajUdaljenost(
            @WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "icao1") String icao1,
            @WebParam(name = "icao2") String icao2) {
        if (autentificiraj(korisnik, lozinka)) {
            //TODO logiraj
            int udaljenost = 0;
            List<Airports> aerodromi = airportsFacade.findAll();
            Airports aerodrom1 = null;
            Airports aerodrom2 = null;
            for (int i = 0; i < aerodromi.size(); i++) {
                String a = aerodromi.get(i).getIdent();
                if (a.equals(icao1)) {
                    aerodrom1 = aerodromi.get(i);
                } else if (a.equals(icao2)) {
                    aerodrom2 = aerodromi.get(i);
                }
            }
            if (aerodrom1 == null || aerodrom2 == null) {
                return -1;
            }

            String c = aerodrom1.getCoordinates();
            String[] strSplit = c.split(",");
            double geoSirina = Double.parseDouble(strSplit[1]);
            double geoDuljina = Double.parseDouble(strSplit[0]);
            Koordinata k1 = new Koordinata(geoSirina, geoDuljina);

            c = aerodrom2.getCoordinates();
            strSplit = c.split(",");
            geoSirina = Double.parseDouble(strSplit[1]);
            geoDuljina = Double.parseDouble(strSplit[0]);
            Koordinata k2 = new Koordinata(geoSirina, geoDuljina);

            udaljenost = Koordinata.izracunajUdaljenost(k1, k2) * 100;

            return udaljenost;
        }
        return -1;
    }

    /**
     *
     * @param korisnik
     * @param lozinka
     * @param icao
     * @param odUdaljenost
     * @param doUdaljenost
     * @return
     */
    @WebMethod(operationName = "dajVlastitePoUdaljenosti")
    public List<Aerodrom> dajVlastitePoUdaljenosti(
            @WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "icao") String icao,
            @WebParam(name = "odUdaljenost") int odUdaljenost,
            @WebParam(name = "doUdaljenost") int doUdaljenost) {
        if (autentificiraj(korisnik, lozinka)) {
            //TODO logiraj
            List<Aerodrom> aerodromi = new ArrayList<>();
            List<Myairports> myairports = myairportsFacade.findAll();
            Airports airport = airportsFacade.dajAerodromePoIcao(icao);

            Airports aerodrom1 = null;
            Airports aerodrom2 = null;
            int udaljenost = 0;
            
            for (Myairports m : myairports) {
                aerodrom1 = m.getIdent();
                aerodrom2 = airport;
                
                String c = aerodrom1.getCoordinates();
                String[] strSplit = c.split(",");
                double geoSirina = Double.parseDouble(strSplit[1]);
                double geoDuljina = Double.parseDouble(strSplit[0]);
                Koordinata k1 = new Koordinata(geoSirina, geoDuljina);

                c = aerodrom2.getCoordinates();
                strSplit = c.split(",");
                geoSirina = Double.parseDouble(strSplit[1]);
                geoDuljina = Double.parseDouble(strSplit[0]);
                Koordinata k2 = new Koordinata(geoSirina, geoDuljina);

                udaljenost = Koordinata.izracunajUdaljenost(k1, k2) * 100;
                if(udaljenost >= odUdaljenost && udaljenost <= doUdaljenost) {
                    String[] ks = aerodrom1.getCoordinates().split(",");
                    Lokacija lokacija = new Lokacija(ks[0].trim(), ks[1].trim());
                    aerodromi.add(new Aerodrom(aerodrom1.getIdent(), aerodrom1.getName(), aerodrom1.getIsoCountry(), lokacija));
                }
            }
            
            
            if (aerodromi.isEmpty()) {
                return null;
            }

            return aerodromi;
        }
        return null;
    }
}
