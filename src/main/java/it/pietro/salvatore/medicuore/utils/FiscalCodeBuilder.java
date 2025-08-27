package it.pietro.salvatore.medicuore.utils;

import it.pietro.salvatore.medicuore.entity.Persona;

abstract class FiscalCodeBuilder {

  abstract <T extends Persona> String setFCPersonaBuilder(T persona);
}
