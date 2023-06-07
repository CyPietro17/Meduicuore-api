package it.svil.studio.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity(name = "impiegati")
public class Impiegato implements Serializable {

    @Serial
    private static final long serial = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "impiegato_id")
    private Long n_id;

    @Column(name = "impiegato_nome", nullable = false)
    private String t_nome;

    @Column(name = "impiegato_cognome", nullable = false)
    private String t_cognome;

    @Column(name = "impiegato_nascita", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date d_dataNascita;

    @Column(name = "impiegato_CF", nullable = false, unique = true, length = 16)
    private String t_codiceFiscale;

    @Column(name = "imp_professione", nullable = false)
    private String t_professione;

    @ManyToOne
    @JoinColumn(name = "impiegato_reparto", referencedColumnName = "reparto_nome")
    private Reparto t_reparto;

    @Column(name = "impiegato_stato_assunzione", length = 2, nullable = false)
    private String b_active;
}
