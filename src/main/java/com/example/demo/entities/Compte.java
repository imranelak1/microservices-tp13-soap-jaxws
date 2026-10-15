package com.example.demo.entities;
import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;
import lombok.*;
import java.util.Date;
/** Même objet métier persisté par JPA et sérialisé par JAXB. */
@Entity @Data @NoArgsConstructor @AllArgsConstructor
@XmlRootElement @XmlAccessorType(XmlAccessType.FIELD)
public class Compte {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private double solde;
    @Temporal(TemporalType.DATE) private Date dateCreation;
    @Enumerated(EnumType.STRING) private TypeCompte type;
}
