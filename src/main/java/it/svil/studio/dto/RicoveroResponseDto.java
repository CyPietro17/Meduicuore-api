package it.svil.studio.dto;

import it.svil.studio.entity.Paziente;
import it.svil.studio.entity.Reparto;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class RicoveroResponseDto {

    private Long n_id;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date d_inizioRicovero;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date d_fineRicovero;
    private Paziente n_paziente;
    private Reparto t_reparto;
}
