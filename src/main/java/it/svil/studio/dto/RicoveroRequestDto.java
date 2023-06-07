package it.svil.studio.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class RicoveroRequestDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date d_inizioRicovero;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date d_fineRicovero;
    private Long n_paziente;
    private Long n_reparto;
}
