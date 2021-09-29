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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.dkermek.ws.serveri.AerodromiWS;
import org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service;
import org.foi.nwtis.dkermek.ws.serveri.StatusKorisnika;
import org.foi.nwtis.tjukica.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.tjukica.web.serveri.PosluziteljDretva;

/**
 *
 * @author Stalker
 */
public class ObradaKorisnikaDretva extends Thread {

    Socket veza;
    BP_Konfiguracija bpk;

    static final String REGEX_AUTENTIFIKACIJA = "(KORISNIK)\\s(\\w+);\\s(LOZINKA)\\s(\\w+);";
    static final String REGEX_KOMANDA = "(KORISNIK)\\s(\\w+);\\s(LOZINKA)\\s(\\w+);\\s(\\w+);";
    static final String REGEX_GRUPA = "(KORISNIK)\\s(\\w+);\\s(LOZINKA)\\s(\\w+);\\s(GRUPA)\\s(\\w+);";
    static final String REGEX_GRUPA_AERODROMI = "(KORISNIK)\\s(\\w+);\\s(LOZINKA)\\s(\\w+);\\s(GRUPA)\\s(\\w+)\\s([\\w,\\s]+);";

    public ObradaKorisnikaDretva(Socket veza, BP_Konfiguracija bpk) {
        this.veza = veza;
        this.bpk = bpk;
        System.out.println("Dretva: ObradaKorisnikaDretva je pokrenuta!");
    }

    @Override
    public void run() {
        try (
                InputStreamReader inps = new InputStreamReader(veza.getInputStream(), "UTF8");
                OutputStreamWriter outs = new OutputStreamWriter(veza.getOutputStream(), "UTF8");) {
            StringBuilder tekst = new StringBuilder();
            while (true) {
                int i = inps.read();
                if (i == -1) {
                    break;
                }
                tekst.append((char) i);
            }
            String komanda = tekst.toString().trim();
            Pattern pAutentifikacija = Pattern.compile(REGEX_AUTENTIFIKACIJA);
            Pattern pKomanda = Pattern.compile(REGEX_KOMANDA);
            Pattern pGrupa = Pattern.compile(REGEX_GRUPA);
            Pattern pAerodromi = Pattern.compile(REGEX_GRUPA_AERODROMI);
            Matcher mAutentifikacija = pAutentifikacija.matcher(komanda);
            Matcher mKomanda = pKomanda.matcher(komanda);
            Matcher mGrupa = pGrupa.matcher(komanda);
            Matcher mAerodromi = pAerodromi.matcher(komanda);
            System.out.println(komanda);
            if (mAutentifikacija.matches()) {
                String odgovor = autentificiraj(mAutentifikacija);
                System.out.println(odgovor);
                outs.write(odgovor);
                outs.flush();
            } else if (mKomanda.matches()) {
                outs.write(obradiKomandu(mKomanda));
                outs.flush();
            } else if (mGrupa.matches()) {
                outs.write(obradiGrupu(mGrupa));
                outs.flush();
            } else if (mAerodromi.matches()) {
                outs.write(obradiAerodrome(mAerodromi));
                outs.flush();
            } else {
                outs.write("POGREŠNA SINTAKSA KOMANDE!");
                outs.flush();
            }
            veza.shutdownOutput();
            veza.shutdownInput();
            veza.close();
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void interrupt() {
        if (!veza.isClosed()) {
            try {
                veza.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        super.interrupt();
    }

    private String obradiKomandu(Matcher mKomanda) {
        String korisnik = mKomanda.group(2);
        String lozinka = mKomanda.group(4);
        String komanda = mKomanda.group(5);
        String retVal = null;

        if (autentificiraj(mKomanda).equals("OK 10;")) {
            if (null != komanda) {
                switch (komanda) {
                    case "PAUZA":
                        if (PosluziteljDretva.status.equals("RADI")) {
                            PosluziteljDretva.status = "PAUZA";
                            retVal = "OK 10;";
                        } else {
                            retVal = "ERR 14;";
                        }
                        break;
                    case "RADI":
                        if (PosluziteljDretva.status.equals("PAUZA")) {
                            PosluziteljDretva.status = "RADI";
                            retVal = "OK 10;";
                        } else {
                            retVal = "ERR 14;";
                        }
                        break;
                    case "KRAJ":
                        PosluziteljDretva.status = "KRAJ";
                        retVal = "OK 10;";
                        break;
                    case "STANJE":
                        if (PosluziteljDretva.status.equals("RADI")) {
                            retVal = "OK 11;";
                        } else if (PosluziteljDretva.status.equals("PAUZA")) {
                            retVal = "OK 12;";
                        } else if (PosluziteljDretva.status.equals("KRAJ")) {
                            retVal = "KRAJ;";
                        }
                        break;
                    default:
                        retVal = "Greška kod autentificiranja!";
                        break;
                }
            }
        } else if (komanda.equals("DODAJ")) {
            if (autentificiraj(mKomanda).equals("ERR 11;")) { //ne postoji
                retVal = komandaDodaj(korisnik, lozinka);
            } else { //vec postoji
                retVal = "ERR 12;";
            }

        } else {
            return "ERR 11;";
        }
        return retVal;
    }

    private String autentificiraj(Matcher komanda) {
        String korisnik = komanda.group(2);
        String lozinka = komanda.group(4);

        String url = bpk.getServerDatabase() + bpk.getUserDatabase();
        String bpkorisnik = bpk.getUserUsername();
        String bplozinka = bpk.getUserPassword();
        String upit = "SELECT COUNT(id) AS ukupno FROM TJUKICA.KORISNICI WHERE korime = '" + korisnik + "' AND lozinka = '" + lozinka + "'";
        String retVal = null;
        try {
            Class.forName(bpk.getDriverDatabase(url));
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {
                while (rs.next()) {
                    int ukupno = rs.getInt("ukupno");
                    if (ukupno == 0) {
                        retVal = "ERR 11;";
                    } else if (ukupno > 0) {
                        retVal = "OK 10;";
                    }
                }

            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ObradaKorisnikaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retVal;
    }

    private String komandaDodaj(String korisnik, String lozinka) {
        String url = bpk.getServerDatabase() + bpk.getUserDatabase();
        String bpkorisnik = bpk.getUserUsername();
        String bplozinka = bpk.getUserPassword();
        String upit = "INSERT INTO TJUKICA.KORISNICI(korime, lozinka) VALUES ('" + korisnik + "', '" + lozinka + "')";
        String retVal = null;
        try {
            Class.forName(bpk.getDriverDatabase(url));
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement()) {

                int brojAzuriranja = s.executeUpdate(upit);
                if (brojAzuriranja == 1) {
                    return "OK 10;";
                } else {
                    return "ERR 12;";
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        } catch (ClassNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        return retVal;
    }

    private String obradiGrupu(Matcher mGrupa) {
        String korisnik = mGrupa.group(2);
        String lozinka = mGrupa.group(4);
        String komanda = mGrupa.group(6);
        String retVal = null;

        if (autentificirajGrupu(mGrupa).equals("OK 10;")) {
            if (null != komanda) {
                switch (komanda) {
                    case "PRIJAVI":
                        retVal = registrirajGrupu(mGrupa);
                        break;
                    case "ODJAVI":
                        retVal = odjaviGrupu(mGrupa);
                        break;
                    case "AKTIVIRAJ":
                        retVal = aktivirajGrupu(mGrupa);
                        break;
                    case "BLOKIRAJ":
                        retVal = blokirajGrupu(mGrupa);
                        break;
                    case "STANJE":
                        retVal = stanjeGrupe(mGrupa);
                        break;
                    default:
                        retVal = "Pogrešna komanda!";
                        break;
                }
            }
        } else {
            return "ERR 11;";
        }
        return retVal;
    }

    private String obradiAerodrome(Matcher komanda) {
        String korisnik = komanda.group(2);
        String lozinka = komanda.group(4);
        String aerodromiString = komanda.group(7);
        String retVal = "";
        
        String stanje = stanjeGrupe(komanda);
        if (stanje.equals("OK 22;")) {
            String[] strSplit = aerodromiString.split(",");
            List<String> aerodromi = new ArrayList<>();
            for (int i = 0; i < strSplit.length; i++) {
                String pom = strSplit[i].trim();
                aerodromi.add(pom);
            }
            AerodromiWS_Service aerodromiWS_Service = new AerodromiWS_Service();
            AerodromiWS port = aerodromiWS_Service.getAerodromiWSPort();
            port.obrisiSveAerodromeGrupe(korisnik, lozinka);
            for(String a : aerodromi) {
                port.dodajAerodromIcaoGrupi(korisnik, lozinka, a);
            }
            retVal = "OK 20;";            
        } else if (stanje.equals("OK 21;")) {
            retVal = "ERR 31;";
        } else {
            retVal = "ERR 32;";
        }
        return retVal;
    }

    private String autentificirajGrupu(Matcher komanda) {
        String korisnik = komanda.group(2);
        String lozinka = komanda.group(4);
        String retVal = null;

        AerodromiWS_Service aerodromiWS_Service = new AerodromiWS_Service();
        AerodromiWS port = aerodromiWS_Service.getAerodromiWSPort();
        Boolean postoji = port.autenticirajGrupu(korisnik, lozinka);       

        if (postoji) {
            retVal = "OK 10;";
        } else {
            retVal = "ERR 11;";
        }
        return retVal;
    }

    private String registrirajGrupu(Matcher mGrupa) {
        String korisnik = mGrupa.group(2);
        String lozinka = mGrupa.group(4);
        AerodromiWS_Service aerodromiWS_Service = new AerodromiWS_Service();
        AerodromiWS port = aerodromiWS_Service.getAerodromiWSPort();
        String retVal = null;
        Boolean odgovor = port.registrirajGrupu(korisnik, lozinka);
        if (odgovor) {
            retVal = "OK 20;";
        } else {
            retVal = "ERR 20;";
        }
        return retVal;
    }

    private String odjaviGrupu(Matcher mGrupa) {
        String korisnik = mGrupa.group(2);
        String lozinka = mGrupa.group(4);
        AerodromiWS_Service aerodromiWS_Service = new AerodromiWS_Service();
        AerodromiWS port = aerodromiWS_Service.getAerodromiWSPort();
        String retVal = null;
        Boolean odgovor = port.deregistrirajGrupu(korisnik, lozinka);
        if (odgovor) {
            retVal = "OK 20;";
        } else {
            retVal = "ERR 21;";
        }
        return retVal;
    }

    private String aktivirajGrupu(Matcher mGrupa) {
        String korisnik = mGrupa.group(2);
        String lozinka = mGrupa.group(4);
        AerodromiWS_Service aerodromiWS_Service = new AerodromiWS_Service();
        AerodromiWS port = aerodromiWS_Service.getAerodromiWSPort();
        String retVal = null;
        if (stanjeGrupe(mGrupa).equals("OK 22;")) { //nije aktivna
            Boolean odgovor = port.aktivirajGrupu(korisnik, lozinka);
            if (odgovor) {
                retVal = "OK 20;";
            } else {
                retVal = "Greška kod aktivacije grupe!";
            }
        } else if (stanjeGrupe(mGrupa).equals("OK 21;")) { //grupa je aktivna
            retVal = "ERR 22;";
        } else if (stanjeGrupe(mGrupa).equals("ERR 21;")) { //nepostoji
            retVal = "ERR 21;";
        }
        return retVal;
    }

    private String stanjeGrupe(Matcher mGrupa) {
        String korisnik = mGrupa.group(2);
        String lozinka = mGrupa.group(4);
        AerodromiWS_Service aerodromiWS_Service = new AerodromiWS_Service();
        AerodromiWS port = aerodromiWS_Service.getAerodromiWSPort();
        String retVal = null;
        StatusKorisnika status = port.dajStatusGrupe(korisnik, lozinka);
        System.out.println("Status grupe je: " + status);
        switch (status) {
            case AKTIVAN:
                retVal = "OK 21;";
                break;
            case BLOKIRAN:
                retVal = "OK 22;";
                break;
            case NEPOSTOJI:
                retVal = "ERR 21;";
                break;
            case REGISTRIRAN:
                retVal = "OK 22;";
                break;
            case DEREGISTRIRAN:
                retVal = "OK 22;";
                break;
            default:
                break;
        }
        return retVal;
    }

    private String blokirajGrupu(Matcher mGrupa) {
        String korisnik = mGrupa.group(2);
        String lozinka = mGrupa.group(4);
        AerodromiWS_Service aerodromiWS_Service = new AerodromiWS_Service();
        AerodromiWS port = aerodromiWS_Service.getAerodromiWSPort();
        String retVal = null;
        StatusKorisnika status = port.dajStatusGrupe(korisnik, lozinka);
        Boolean odgovor = null;
        switch (status) {
            case AKTIVAN:
                odgovor = port.blokirajGrupu(korisnik, lozinka);
                if (odgovor) {
                    retVal = "OK 20;";
                } else {
                    retVal = "Greška kod blokiranja grupe!";
                }   break;
            case BLOKIRAN:
                //nije aktivna
                retVal = "ERR 23;";
                break;
            case NEPOSTOJI:
                retVal = "ERR 21;";
                break;
            case REGISTRIRAN:
                odgovor = port.blokirajGrupu(korisnik, lozinka);
                if (odgovor) {
                    retVal = "OK 20;";
                } else {
                    retVal = "Greška kod blokiranja grupe!";
                }   break;
            default:
                retVal = "STATUS GRUPE JE: " + status;
                break;
        }
        return retVal;
    }
}
