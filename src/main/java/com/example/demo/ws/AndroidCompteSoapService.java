package com.example.demo.ws;

import com.example.demo.entities.Compte;
import com.example.demo.entities.TypeCompte;
import jakarta.jws.*;
import org.springframework.stereotype.Component;
import java.util.List;

/** Contrat du TP Android KSOAP, délégué à la même couche métier. */
@Component
@WebService(serviceName="SoapAcount",portName="SoapAcountPort",targetNamespace="http://ws.soapAcount/")
public class AndroidCompteSoapService {
    private final CompteSoapService service;
    public AndroidCompteSoapService(CompteSoapService service) { this.service=service; }
    @WebMethod public List<Compte> getComptes() { return service.getComptes(); }
    @WebMethod public Compte getCompteById(@WebParam(name="id") Long id) { return service.getCompteById(id); }
    @WebMethod public Compte createCompte(@WebParam(name="solde") double solde,@WebParam(name="type") TypeCompte type) { return service.createCompte(solde,type); }
    @WebMethod public boolean deleteCompte(@WebParam(name="id") Long id) { return service.deleteCompte(id); }
}
