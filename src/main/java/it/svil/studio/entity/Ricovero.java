package it.svil.studio.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity(name = "ricoveri")
public class Ricovero implements Serializable {

    @Serial
    private static final long serial = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ricovero_id")
    private Long n_id;

    @Column(name = "ricovero_inizio", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date d_inizioRicovero;

    @Column(name = "ricovero_fine")
    @Temporal(TemporalType.DATE)
    private Date d_fineRicovero;

    @ManyToOne
    @JoinColumn(name = "paziente_id", nullable = false)
    private Paziente n_paziente;

    @ManyToOne
    @JoinColumn(name = "ricovero_reparto", nullable = false, referencedColumnName = "reparto_nome")
    private Reparto t_reparto;
}
