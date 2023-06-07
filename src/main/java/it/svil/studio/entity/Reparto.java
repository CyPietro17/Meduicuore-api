package it.svil.studio.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Data
@Entity(name = "reparti")
public class Reparto implements Serializable {

    @Serial
    private static final long serial = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reparto_id")
    private Long n_id;

    @Column(name = "reparto_nome", unique = true, nullable = false)
    private String t_nome;

    @Column(name = "posti_letto_effettivi", nullable = false)
    private Integer n_postiLettoEffettivi;

    @Column(name = "posti_letto_disponibili", nullable = false)
    private Integer n_postiLettoDisponibili;

    @Column(name = "posti_liberi", nullable = false)
    private String b_postiLiberi;
}
