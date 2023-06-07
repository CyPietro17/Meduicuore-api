package it.svil.studio.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity(name = "pazienti")
public class Paziente implements Serializable {

    @Serial
    private static final long serial = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paziente_id")
    private Long n_id;

    @Column(name = "paziente_nome", nullable = false)
    private String t_nome;

    @Column(name = "paziente_cognome", nullable = false)
    private String t_cognome;

    @Column(name = "paziente_nascita", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date d_dataNascita;

    @Column(name = "paziente_CF", nullable = false, unique = true, length = 16)
    private String t_codiceFiscale;

    @Column(name = "paziente_ricoverato", nullable = false, length = 2)
    private String b_ricoverato;
}
