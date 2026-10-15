package com.example.demo.ws;
import com.example.demo.entities.*;
import com.example.demo.repositories.CompteRepository;
import jakarta.jws.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
/** Opérations SOAP bancaires ; le repository assure la persistance H2. */
@Component @Transactional
@WebService(serviceName="BanqueWS",portName="BanqueWSPort",targetNamespace="http://ws.demo.example.com/")
public class CompteSoapService {
    private final CompteRepository comptes;
    public CompteSoapService(CompteRepository comptes) { this.comptes=comptes; }
    @WebMethod public List<Compte> getComptes() { return comptes.findAll(); }
    @WebMethod public Compte getCompteById(@WebParam(name="id") Long id) { return comptes.findById(id).orElse(null); }
    @WebMethod public Compte createCompte(@WebParam(name="solde") double solde,@WebParam(name="type") TypeCompte type) {
        if(!Double.isFinite(solde) || solde<0 || type==null) throw new IllegalArgumentException("Compte invalide");
        return comptes.save(new Compte(null,solde,new Date(),type));
    }
    @WebMethod public boolean deleteCompte(@WebParam(name="id") Long id) {
        if(!comptes.existsById(id)) return false;
        comptes.deleteById(id); return true;
    }
}
