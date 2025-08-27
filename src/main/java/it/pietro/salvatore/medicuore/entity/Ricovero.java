package it.pietro.salvatore.medicuore.entity;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@Alias(value = "ricoveri")
public class Ricovero implements Serializable {

  @Serial
  private static final long serial = 1L;

  private Long n_id;
  //    @Temporal(TemporalType.DATE)
  private Date d_inizioRicovero;
  //    @Temporal(TemporalType.DATE)
  private Date d_fineRicovero;
  //    @ManyToOne
  //    @JoinColumn(name = "paziente_id", nullable = false)
  private Paziente n_paziente;
  //    @ManyToOne
  //    @JoinColumn(name = "ricovero_reparto", nullable = false, referencedColumnName = "reparto_nome")
  private Reparto t_reparto;
}
