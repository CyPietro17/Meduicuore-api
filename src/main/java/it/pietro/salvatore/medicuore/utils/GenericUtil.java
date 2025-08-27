package it.pietro.salvatore.medicuore.utils;

import it.pietro.salvatore.medicuore.entity.Impiegato;
import it.pietro.salvatore.medicuore.entity.Paziente;
import it.pietro.salvatore.medicuore.entity.Persona;

public class GenericUtil {

  public static final String MEDICO = "Medico";
  public static final String INFERMIERE = "Infermiere";
  public static final String CAPOREPARTO = "Capo Reparto";
  private static final FiscalCodeImpiegato fcImpiegato = new FiscalCodeImpiegato();
  private static final FiscalCodePaziente fcPaziente = new FiscalCodePaziente();
  protected static int countImpiegato = 100;
  protected static int countPaziente = 100;

  private GenericUtil() {
    throw new IllegalStateException("Classe di Utils");
  }

  public static String setCodiceFiscale(Persona persona) {
    try {
      if (persona instanceof Impiegato) {
        countImpiegato++;
        return fcImpiegato.setFCPersonaBuilder(persona);
      }
      if (persona instanceof Paziente) {
        countPaziente++;
        return fcPaziente.setFCPersonaBuilder(persona);
      }
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
    return "cf null";
  }
}