/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.tjukica.web.dretve;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.tjukica.ejb.eb.Airplanes;
import org.foi.nwtis.tjukica.ejb.eb.Airports;
import org.foi.nwtis.tjukica.ejb.eb.Myairports;
import org.foi.nwtis.tjukica.ejb.eb.Myairportslog;
import org.foi.nwtis.tjukica.ejb.eb.MyairportslogPK;
import org.foi.nwtis.tjukica.ejb.sb.AirplanesFacadeLocal;
import org.foi.nwtis.tjukica.ejb.sb.AirportsFacadeLocal;
import org.foi.nwtis.tjukica.ejb.sb.MyairportsFacadeLocal;
import org.foi.nwtis.tjukica.ejb.sb.MyairportslogFacadeLocal;
import org.foi.nwtis.tjukica.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author Stalker
 */
public class PreuzimanjeLetovaAvionaAerodromaDretva extends Thread {

    BP_Konfiguracija konf;
    String korisnik;
    String lozinka;
    String pocetak;
    String kraj;
    Boolean preuzimanjeStatus;
    Boolean krajPreuzimanja;
    int intervalCiklusa;
    int pauza;

    String adresa = "localhost";
    int port;
    String korIme = "tjukica"; //TODO Hardkodirano
    String loz = "s8t7go";

    AirportsFacadeLocal airportsFacade;
    AirplanesFacadeLocal airplanesFacade;
    MyairportsFacadeLocal myairportsFacade;
    MyairportslogFacadeLocal myairportslogFacade;

    List<Airports> airports = new ArrayList<>();
    List<Airplanes> airplanes = new ArrayList<>();
    List<Myairports> myairports = new ArrayList<>();
    List<Myairportslog> myairportslog = new ArrayList<>();

    public PreuzimanjeLetovaAvionaAerodromaDretva(BP_Konfiguracija bpk, AirportsFacadeLocal a, AirplanesFacadeLocal air, MyairportsFacadeLocal ma, MyairportslogFacadeLocal mal) {
        this.airportsFacade = a;
        this.airplanesFacade = air;
        this.myairportsFacade = ma;
        this.myairportslogFacade = mal;
        this.konf = bpk;
    }

    @Override
    public synchronized void start() {
        korisnik = konf.getKonfig().dajPostavku("OpenSkyNetwork.korisnik");
        lozinka = konf.getKonfig().dajPostavku("OpenSkyNetwork.lozinka");
        pocetak = konf.getKonfig().dajPostavku("preuzimanje.pocetak");
        kraj = konf.getKonfig().dajPostavku("preuzimanje.kraj");
        preuzimanjeStatus = Boolean.getBoolean(konf.getKonfig().dajPostavku("preuzimanje.status"));
        intervalCiklusa = Integer.parseInt(konf.getKonfig().dajPostavku("preuzimanje.ciklus"));
        pauza = Integer.parseInt(konf.getKonfig().dajPostavku("preuzimanje.pauza"));
        port = Integer.parseInt(konf.getKonfig().dajPostavku("posluzitelj.port"));

        //airplanes = airplanesFacade.findAll();
        airports = airportsFacade.findAll();
        myairports = myairportsFacade.findAll();
        myairportslog = myairportslogFacade.findAll();
        super.start();
    }

    @Override
    public void run() {
        int brojac = 0;
        List<AvionLeti> avioni = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyy");
        java.util.Date datePocetak;
        java.util.Date dateKraj;
        long odVremena = 0;
        long doVremena = 0;

        try {
            datePocetak = dateFormat.parse(pocetak);
            odVremena = datePocetak.getTime() / 1000;
            dateKraj = dateFormat.parse(kraj);
            doVremena = dateKraj.getTime() / 1000;
        } catch (ParseException ex) {
            Logger.getLogger(PreuzimanjeLetovaAvionaAerodromaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }

        OSKlijent oSKlijent = new OSKlijent(korisnik, lozinka);

        krajPreuzimanja = false;
        while (!krajPreuzimanja) {
            String status = provjeriStatus();
            if (status.equals("OK 11;")) { // preuzima
                System.out.println("STATUS: RADI");
                System.out.println("Brojac: " + brojac++);
                long timestamp = odVremena * 1000;
                Date d = new Date(timestamp);
                for (int i = 0; i < myairports.size(); i++) {
                    if (!myairportslog.isEmpty()) {
                        for (int j = 0; j < myairportslog.size(); j++) {
                            if (postojiLog(myairports.get(i).getIdent().getIdent(), d)) {
                                break; //Postoji
                            }
                            String icao = myairports.get(i).getIdent().getIdent();
                            avioni = oSKlijent.getDepartures(icao, odVremena, dodajDan(odVremena));
                            for (AvionLeti a : avioni) {
                                if (a.getEstArrivalAirport() != null) {
                                    dodajAvion(a, myairports.get(i).getIdent());
                                }
                            }
                            dodajLog(myairports.get(i), d);
                        }
                    } else {
                        String icao = myairports.get(i).getIdent().toString();
                        avioni = oSKlijent.getDepartures(icao, odVremena, dodajDan(odVremena));
                        for (AvionLeti a : avioni) {
                            if (a.getEstArrivalAirport() != null) {
                                dodajAvion(a, myairports.get(i).getIdent());
                            }
                        }
                        dodajLog(myairports.get(i), d);
                    }

                }
                System.out.println("Kraj ciklusa: " + (brojac - 1));
                try {
                    Thread.sleep(intervalCiklusa * 1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PreuzimanjeLetovaAvionaAerodromaDretva.class.getName()).log(Level.SEVERE, null, ex);
                }
                brojac++;

                odVremena = dodajDan(odVremena);
                if (odVremena >= doVremena) {
                    krajPreuzimanja = true;
                    System.out.println("KRAJ PREUZIMANJA");
                    break;
                }
            } else if (status.equals("OK 12;")) {//ne preuzima
                try {
                    System.out.println("STATUS: PAUZA");
                    Thread.sleep(pauza * 1000); //pauzira
                } catch (InterruptedException ex) {
                    Logger.getLogger(PreuzimanjeLetovaAvionaAerodromaDretva.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (status.equals("KRAJ;")) {
                System.out.println("STATUS: KRAJ");
                krajPreuzimanja = true;
                System.out.println("KRAJ PREUZIMANJA");
            } else {
                System.out.println("Greška kod preuzimanja statusa preuzimanja!");
            }

            java.util.Date trenutniDatum = new java.util.Date();
            if (odVremena >= trenutniDatum.getTime()) {
                try {
                    Thread.sleep(86400000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PreuzimanjeLetovaAvionaAerodromaDretva.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("Pauziranje preuzimanja od jedan dan.");
            }

        }
    }

    public long dodajDan(long pocetniDan) {
        return pocetniDan + 86400;
    }

    public void dodajAvion(AvionLeti b, Airports airport) {
        Airplanes a = new Airplanes();

        a.setCallsign(b.getCallsign());
        a.setIcao24(b.getIcao24());
        a.setFirstseen(b.getFirstSeen());
        a.setLastseen(b.getLastSeen());
        a.setId(1);
        a.setStored(new Date());

        a.setEstarrivalairport(b.getEstArrivalAirport());
        a.setEstarrivalairporthorizdistance(b.getEstArrivalAirportHorizDistance());
        a.setEstarrivalairportvertdistance(b.getEstArrivalAirportVertDistance());

        a.setEstdepartureairport(airport);
        a.setEstdepartureairporthorizdistance(b.getEstDepartureAirportHorizDistance());
        a.setEstdepartureairportvertdistance(b.getEstDepartureAirportVertDistance());

        a.setDepartureairportcandidatescount(b.getDepartureAirportCandidatesCount());
        a.setArrivalairportcandidatescount(b.getArrivalAirportCandidatesCount());

        System.out.println(b.getIcao24());
        airplanesFacade.create(a);
    }

    public Boolean postojiLog(String ident, Date odVremena) {
        Boolean retVal = myairportslogFacade.postojiLog(ident, odVremena);
        return retVal;
    }

    public void dodajLog(Myairports ident, Date vrijeme) {

        //Autor koda: Goran Hubak
        Date date = new Date();
        Myairportslog log = new Myairportslog();
        MyairportslogPK mpk = new MyairportslogPK();
        String id = ident.getIdent().getIdent();
        Airports avion = airportsFacade.find(id);
        mpk.setIdent(id);
        mpk.setFlightdate(vrijeme);        
        log.setAirports(avion);
        log.setStored(new Timestamp(date.getTime()));
        log.setMyairportslogPK(mpk);
        myairportslogFacade.create(log);

        /*
        Myairportslog m = new Myairportslog();        
        MyairportslogPK mpk = new MyairportslogPK();
        mpk.setIdent(ident);
        mpk.setFlightdate(vrijeme);        
        Airports a = airportsFacade.find(ident);        
        m.setMyairportslogPK(mpk);
        m.setAirports(a);
        m.setStored(new Date());              
        myairportslogFacade.create(m);
        myairportslog = myairportslogFacade.findAll();
         */
    }

    private String provjeriStatus() {
        String retVal = null;
        try (
                Socket veza = new Socket(adresa, port);
                InputStreamReader inps = new InputStreamReader(veza.getInputStream(), "UTF8");
                OutputStreamWriter osw = new OutputStreamWriter(veza.getOutputStream(), "UTF8");) {

            String posalji = "KORISNIK " + korIme + "; LOZINKA " + loz + "; STANJE;";

            osw.write(posalji);
            osw.flush();
            veza.shutdownOutput();
            StringBuilder tekst = new StringBuilder();
            while (true) {
                int i = inps.read();
                if (i == -1) {
                    break;
                }
                tekst.append((char) i);
            }
            System.out.println(tekst);
            retVal = tekst.toString();
            veza.shutdownInput();
            veza.close();
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        return retVal;
    }

}
