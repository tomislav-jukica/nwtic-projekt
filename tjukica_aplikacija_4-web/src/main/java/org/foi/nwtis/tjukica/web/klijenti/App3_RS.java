/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.tjukica.web.klijenti;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * Jersey REST client generated for REST resource:App3Rest [rest]<br>
 * USAGE:
 * <pre>
 *        App3_RS client = new App3_RS();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author Stalker
 */
public class App3_RS {

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "http://localhost:8084/tjukica_aplikacija_3-web/webresources";
    private String korisnik = "";
    private String lozinka = "";

    public App3_RS() {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("rest");
    }
    public App3_RS(String korisnik, String lozinka) {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("rest");
        this.korisnik = korisnik;
        this.lozinka = lozinka;
    }
    
    public <T> T dajAerodomeKorisnika(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header("korisnik", korisnik)
                .header("lozinka", lozinka)
                .get(responseType);
    }

    public <T> T dajAerodomKorisnika(Class<T> responseType, String icao) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(icao);
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header("korisnik", korisnik)
                .header("lozinka", lozinka)
                .get(responseType);
    }

    public Response izbrisiAerodromKorisnika(Object requestEntity, String icao) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(icao);
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header("korisnik", korisnik)
                .header("lozinka", lozinka)
                .delete();        
    }

    public Response izbrisiAvioneAerodroma(Object requestEntity, String icao) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(icao + "/avioni");
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header("korisnik", korisnik)
                .header("lozinka", lozinka)
                .delete(); }

    public Response saljiJMSPoruku(Object requestEntity) throws ClientErrorException {
        return webTarget.path("komande").request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
    }

    

    public <T> T dajAerodromeFilter(Class<T> responseType, String drzava, String naziv) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (drzava != null) {
            resource = resource.queryParam("drzava", drzava);
        }
        if (naziv != null) {
            resource = resource.queryParam("naziv", naziv);
        }
        resource = resource.path("svi");
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header("korisnik", korisnik)
                .header("lozinka", lozinka)
                .get(responseType);
    }

    public void close() {
        client.close();
    }
    
}
