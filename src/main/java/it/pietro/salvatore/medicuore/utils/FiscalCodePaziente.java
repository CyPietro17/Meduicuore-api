package it.pietro.salvatore.medicuore.utils;

import it.pietro.salvatore.medicuore.entity.Paziente;
import it.pietro.salvatore.medicuore.entity.Persona;

class FiscalCodePaziente extends FiscalCodeBuilder {

  @Override
  protected <T extends Persona> String setFCPersonaBuilder(T persona) {
    StringBuilder fiscalCode = new StringBuilder();
    String mesiCod = "ABCDEHLMPRST";
    Paziente paziente = (Paziente) persona;
    boolean condizioneNome = true;
    boolean condizioneCognome = true;
    int countNome = 0;
    int countCognome = 0;
    String nome = paziente.getT_nome().toUpperCase();
    while (nome.length() < 3) {
      nome += "A";
    }
    String cognome = paziente.getT_cognome().toUpperCase().replaceAll("\\s", "").replaceAll("'", "");
    while (cognome.length() < 3) {
      cognome += "A";
    }
    String annoNascita;
    if (paziente.getD_dataNascita().getYear() > 99) {
      annoNascita = String.valueOf(paziente.getD_dataNascita().getYear() - 100);
      if (annoNascita.length() == 1) {
        annoNascita = "0" + String.valueOf(paziente.getD_dataNascita().getYear() - 100);
      }
    } else {
      annoNascita = String.valueOf(paziente.getD_dataNascita().getYear());
    }
    String meseNascitaPaz = String.valueOf(mesiCod.charAt(paziente.getD_dataNascita().getMonth()));
    String giornoNascitaPaz;
    if (paziente.getD_dataNascita().getDate() < 10) {
      giornoNascitaPaz = "0" + String.valueOf(paziente.getD_dataNascita().getDate());
    } else {
      giornoNascitaPaz = String.valueOf(paziente.getD_dataNascita().getDate());
    }
    String conteggioPaziente = String.valueOf(GenericUtil.countPaziente);
    while (condizioneCognome) {
      countCognome++;
      int j = 0;
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
    fiscalCode.append(meseNascitaPaz);
    fiscalCode.append(giornoNascitaPaz);
    fiscalCode.append('A');
    fiscalCode.append(conteggioPaziente);
    fiscalCode.append('Q');
    return fiscalCode.toString();
  }
}
