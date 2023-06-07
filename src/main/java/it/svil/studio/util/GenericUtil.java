package it.svil.studio.util;

import it.svil.studio.entity.Impiegato;
import it.svil.studio.entity.Paziente;

public class GenericUtil {

    private static int countImpiegato = 100;
    private static int countPaziente = 100;

    public static final String MEDICO = "Medico";
    public static final String INFERMIERE = "Infermiere";
    public static final String CAPOREPARTO = "Capo Reparto";

    public static <T> String setCodiceFiscale(T persona){
        StringBuilder CF = new StringBuilder();
        String mesiCod = "ABCDEHLMPRST";
        if(persona instanceof Impiegato){
            Impiegato impiegato = (Impiegato) persona;
            boolean condizioneNome = true;
            boolean condizioneCognome = true;
            int countNome = 0;
            int countCognome = 0;
            String nome = impiegato.getT_nome().toUpperCase();
            while(nome.length() < 3)
                nome += "A";
            String cognome = impiegato.getT_cognome().toUpperCase().replaceAll(" ", "").replaceAll("'", "");
            while(cognome.length() < 3)
                cognome += "A";
            String annoNascita;
            if(impiegato.getD_dataNascita().getYear() > 99){
                annoNascita = String.valueOf(impiegato.getD_dataNascita().getYear()-100);
                if(annoNascita.length() == 1)
                    annoNascita = "0" + String.valueOf(impiegato.getD_dataNascita().getYear()-100);
            } else annoNascita = String.valueOf(impiegato.getD_dataNascita().getYear());
            String meseNascitaImp = String.valueOf(mesiCod.charAt(impiegato.getD_dataNascita().getMonth()));
            String giornoNascitaImp;
            if(impiegato.getD_dataNascita().getDate() < 10)
                giornoNascitaImp = "0" + String.valueOf(impiegato.getD_dataNascita().getDate());
            else giornoNascitaImp = String.valueOf(impiegato.getD_dataNascita().getDate());
            String conteggioImpiegato = String.valueOf(countImpiegato);
            while (condizioneNome) {
                int j = 0;
                countNome++;
                for (int i = 0; i < nome.length(); i++) {
                    if (CF.length() == 3) {
                        condizioneNome = false;
                        break;
                    }
                    if (countNome > 1) {
                        if(CF.charAt(j) != nome.charAt(i))
                            CF.append(nome.charAt(i));
                        j++;
                        continue;
                    }
                    if (
                            nome.charAt(i) != 'A'
                            && nome.charAt(i) != 'E'
                            && nome.charAt(i) != 'I'
                            && nome.charAt(i) != 'O'
                            && nome.charAt(i) != 'U'
                    )
                        CF.append(nome.charAt(i));
                }
            }
            while (condizioneCognome) {
                int j = 3;
                countCognome++;
                for (int i = 0; i < cognome.length(); i++) {
                    if (CF.length() == 6) {
                        condizioneCognome = false;
                        break;
                    }
                    if (countCognome > 1) {
                        if(CF.charAt(j) != cognome.charAt(i))
                            CF.append(cognome.charAt(i));
                        j++;
                        continue;
                    }
                    if (
                            cognome.charAt(i) != 'A'
                            && cognome.charAt(i) != 'E'
                            && cognome.charAt(i) != 'I'
                            && cognome.charAt(i) != 'O'
                            && cognome.charAt(i) != 'U'
                    )
                        CF.append(cognome.charAt(i));
                }
            }
            CF.append(annoNascita);
            CF.append(meseNascitaImp);
            CF.append(giornoNascitaImp);
            CF.append('A');
            CF.append(conteggioImpiegato);
            countImpiegato++;
            CF.append('Q');
        }
        if(persona instanceof Paziente){
            Paziente paziente = (Paziente) persona;
            boolean condizioneNome = true;
            boolean condizioneCognome = true;
            int countNome = 0;
            int countCognome = 0;
            String nome = paziente.getT_nome().toUpperCase();
            while(nome.length() < 3)
                nome += "A";
            String cognome = paziente.getT_cognome().toUpperCase().replaceAll(" ", "").replaceAll("'", "");
            while(cognome.length() < 3)
                cognome += "A";
            String annoNascita;
            if(paziente.getD_dataNascita().getYear() > 99){
                annoNascita = String.valueOf(paziente.getD_dataNascita().getYear()-100);
                if(annoNascita.length() == 1)
                    annoNascita = "0" + String.valueOf(paziente.getD_dataNascita().getYear()-100);
            } else annoNascita = String.valueOf(paziente.getD_dataNascita().getYear());
            String meseNascitaPaz = String.valueOf(mesiCod.charAt(paziente.getD_dataNascita().getMonth()));
            String giornoNascitaPaz;
            if(paziente.getD_dataNascita().getDate() < 10)
                giornoNascitaPaz = "0" + String.valueOf(paziente.getD_dataNascita().getDate());
            else giornoNascitaPaz = String.valueOf(paziente.getD_dataNascita().getDate());
            String conteggioPaziente = String.valueOf(countPaziente);
            while (condizioneNome) {
                int j = 0;
                countNome++;
                for (int i = 0; i < nome.length(); i++) {
                    if (CF.length() == 3) {
                        condizioneNome = false;
                        break;
                    }
                    if (countNome > 1) {
                        if(CF.charAt(j) != nome.charAt(i))
                            CF.append(nome.charAt(i));
                        j++;
                        continue;
                    }
                    if (
                            nome.charAt(i) != 'A'
                            && nome.charAt(i) != 'E'
                            && nome.charAt(i) != 'I'
                            && nome.charAt(i) != 'O'
                            && nome.charAt(i) != 'U'
                    )
                        CF.append(nome.charAt(i));
                }
            }
            while (condizioneCognome) {
                countCognome++;
                int j = 3;
                for (int i = 0; i < cognome.length(); i++) {
                    if (CF.length() == 6) {
                        condizioneCognome = false;
                        break;
                    }
                    if (countCognome > 1) {
                        if(CF.charAt(j) != cognome.charAt(i))
                            CF.append(cognome.charAt(i));
                        j++;
                        continue;
                    }
                    if (    cognome.charAt(i) != 'A'
                            && cognome.charAt(i) != 'E'
                            && cognome.charAt(i) != 'I'
                            && cognome.charAt(i) != 'O'
                            && cognome.charAt(i) != 'U'
                    )
                        CF.append(cognome.charAt(i));
                }
            }
            CF.append(annoNascita);
            CF.append(meseNascitaPaz);
            CF.append(giornoNascitaPaz);
            CF.append('A');
            CF.append(conteggioPaziente);
            countPaziente++;
            CF.append('Q');
        }
        return CF.toString();
    }
}
