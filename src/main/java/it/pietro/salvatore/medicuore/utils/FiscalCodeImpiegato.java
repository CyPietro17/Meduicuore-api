package it.pietro.salvatore.medicuore.utils;

import it.pietro.salvatore.medicuore.entity.Impiegato;
import it.pietro.salvatore.medicuore.entity.Persona;

class FiscalCodeImpiegato extends FiscalCodeBuilder {

  @Override
  protected <T extends Persona> String setFCPersonaBuilder(T persona) {
    StringBuilder fiscalCode = new StringBuilder();
    String mesiCod = "ABCDEHLMPRST";
    Impiegato impiegato = (Impiegato) persona;
    boolean condizioneNome = true;
    boolean condizioneCognome = true;
    int countNome = 0;
    int countCognome = 0;
    String nome = impiegato.getT_nome().toUpperCase();
    while (nome.length() < 3) {
      nome += "A";
    }
    String cognome = impiegato.getT_cognome().toUpperCase().replaceAll("\\s", "").replaceAll("'", "");
    while (cognome.length() < 3) {
      cognome += "A";
    }
    String annoNascita;
    if (impiegato.getD_dataNascita().getYear() > 99) {
      annoNascita = String.valueOf(impiegato.getD_dataNascita().getYear() - 100);
      if (annoNascita.length() == 1) {
        annoNascita = "0" + (impiegato.getD_dataNascita().getYear() - 100);
      }
    } else {
      annoNascita = String.valueOf(impiegato.getD_dataNascita().getYear());
    }
    String meseNascitaImp = String.valueOf(mesiCod.charAt(impiegato.getD_dataNascita().getMonth()));
    String giornoNascitaImp;
    if (impiegato.getD_dataNascita().getDate() < 10) {
      giornoNascitaImp = "0" + (impiegato.getD_dataNascita().getDate());
    } else {
      giornoNascitaImp = String.valueOf(impiegato.getD_dataNascita().getDate());
    }
    String conteggioImpiegato = String.valueOf(GenericUtil.countImpiegato);
    while (condizioneCognome) {
      int j = 0;
      countCognome++;
      for (int i = 0; i < cognome.length(); i++) {
        if (fiscalCode.length() == 3) {
          condizioneCognome = false;
          break;
        }
        if (countCognome > 1) {
          if (fiscalCode.charAt(j) != cognome.charAt(i)) {
            fiscalCode.append(cognome.charAt(i));
          }
          j++;
          continue;
        }
        if (cognome.charAt(i) != 'A' && cognome.charAt(i) != 'E' && cognome.charAt(i) != 'I' && cognome.charAt(i) != 'O' &&
              cognome.charAt(i) != 'U') {
          fiscalCode.append(cognome.charAt(i));
        }
      }
    }
    while (condizioneNome) {
      int j = 3;
      countNome++;
      for (int i = 0; i < nome.length(); i++) {
        if (fiscalCode.length() == 6) {
          condizioneNome = false;
          break;
        }
        if (countNome > 1) {
          if (fiscalCode.charAt(j) != nome.charAt(i)) {
            fiscalCode.append(nome.charAt(i));
          }
          j++;
          continue;
        }
        if (nome.charAt(i) != 'A' && nome.charAt(i) != 'E' && nome.charAt(i) != 'I' && nome.charAt(i) != 'O' &&
              nome.charAt(i) != 'U') {
          fiscalCode.append(nome.charAt(i));
        }
      }
    }
    fiscalCode.append(annoNascita);
    fiscalCode.append(meseNascitaImp);
    fiscalCode.append(giornoNascitaImp);
    fiscalCode.append('A');
    fiscalCode.append(conteggioImpiegato);
    fiscalCode.append('Q');
    return fiscalCode.toString();
  }
}
