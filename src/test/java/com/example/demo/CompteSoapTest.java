package com.example.demo;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.repositories.CompteRepository;
import java.net.*;
import java.net.http.*;
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
class CompteSoapTest {
    @LocalServerPort int port;
    @Autowired CompteRepository repository;
    HttpClient http=HttpClient.newHttpClient();
    @BeforeEach void clear() { repository.deleteAll(); }
    String call(String body) throws Exception {
        String xml="<s:Envelope xmlns:s='http://schemas.xmlsoap.org/soap/envelope/' xmlns:b='http://ws.demo.example.com/'><s:Body>"+body+"</s:Body></s:Envelope>";
        return http.send(HttpRequest.newBuilder(URI.create("http://localhost:"+port+"/ws")).header("Content-Type","text/xml; charset=utf-8").POST(HttpRequest.BodyPublishers.ofString(xml)).build(),HttpResponse.BodyHandlers.ofString()).body();
    }
    @Test void wsdlExposesFourOperations() throws Exception {
        String wsdl=http.send(HttpRequest.newBuilder(URI.create("http://localhost:"+port+"/ws?wsdl")).build(),HttpResponse.BodyHandlers.ofString()).body();
        for(String operation:new String[]{"getComptes","getCompteById","createCompte","deleteCompte","BanqueWS"}) assertTrue(wsdl.contains(operation),operation);
    }
    @Test void androidAliasKeepsRequiredNamespace() throws Exception {
        String wsdl=http.send(HttpRequest.newBuilder(URI.create("http://localhost:"+port+"/services/ws?wsdl")).build(),HttpResponse.BodyHandlers.ofString()).body();
        assertTrue(wsdl.contains("http://ws.soapAcount/")); assertTrue(wsdl.contains("createCompte"));
        String xml="<s:Envelope xmlns:s='http://schemas.xmlsoap.org/soap/envelope/' xmlns:b='http://ws.soapAcount/'><s:Body><b:createCompte><solde>800</solde><type>COURANT</type></b:createCompte></s:Body></s:Envelope>";
        var response=http.send(HttpRequest.newBuilder(URI.create("http://localhost:"+port+"/services/ws")).header("Content-Type","text/xml").POST(HttpRequest.BodyPublishers.ofString(xml)).build(),HttpResponse.BodyHandlers.ofString());
        assertEquals(200,response.statusCode()); assertTrue(response.body().contains("800.0"));
    }
    @Test void soapCreatePersistsJaxbFields() throws Exception {
        String response=call("<b:createCompte><solde>1250</solde><type>COURANT</type></b:createCompte>");
        assertTrue(response.contains("1250.0"),response); assertTrue(response.contains("COURANT"));
        assertEquals(1,repository.count()); assertNotNull(repository.findAll().get(0).getDateCreation());
    }
    @Test void listAndLookupUseDatabase() throws Exception {
        call("<b:createCompte><solde>300</solde><type>EPARGNE</type></b:createCompte>");
        Long id=repository.findAll().get(0).getId();
        assertTrue(call("<b:getComptes/>").contains("300.0"));
        assertTrue(call("<b:getCompteById><id>"+id+"</id></b:getCompteById>").contains("EPARGNE"));
    }
    @Test void deleteReturnsTrueThenFalse() throws Exception {
        call("<b:createCompte><solde>10</solde><type>COURANT</type></b:createCompte>");
        Long id=repository.findAll().get(0).getId();
        assertTrue(call("<b:deleteCompte><id>"+id+"</id></b:deleteCompte>").contains("true"));
        assertEquals(0,repository.count());
        assertTrue(call("<b:deleteCompte><id>"+id+"</id></b:deleteCompte>").contains("false"));
    }
    @Test void invalidBalanceReturnsSoapFaultWithoutInsertion() throws Exception {
        assertTrue(call("<b:createCompte><solde>-1</solde><type>COURANT</type></b:createCompte>").contains("Fault"));
        assertEquals(0,repository.count());
    }
}
