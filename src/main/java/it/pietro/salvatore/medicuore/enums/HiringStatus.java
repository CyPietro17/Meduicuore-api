package it.pietro.salvatore.medicuore.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * Enum representing the hiring status of an employee.
 */
@Getter
public enum HiringStatus {
    HIRED("Hired", "The employee is currently hired and active"),
    RESIGNED("Resigned", "The employee has been resigned or is no longer active");

    private final String status;
    private final String description;

    HiringStatus(String status, String description) {
        this.status = status;
        this.description = description;
    }

    /**
     * Restituisce il valore da usare nella serializzazione JSON.
     * Sarà "MEDICO", "INFERMIERE", o "CAPOREPARTO".
     */
    @JsonValue
    public String toValue() {
        return this.name();
    }

    /**
     * Converte una stringa in Enum, gestendo case insensitive e valori legacy.
     *
     * @param value stringa da convertire
     * @throws IllegalArgumentException se valore non valido
     */
    public static void fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Professione non può essere vuota");
        }

        String normalized = value.trim().toUpperCase();

        try {
            HiringStatus.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
              String.format("Professione '%s' non valida. Valori ammessi: MEDICO, INFERMIERE, CAPOREPARTO", value)
            );
        }
    }

    /**
     * Verifica se una stringa rappresenta una professione valida.
     *
     * @param value stringa da verificare
     * @return true se valida
     */
    public static boolean isValid(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }

        try {
            fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}