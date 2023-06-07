package it.svil.studio.security;

import lombok.Data;

@Data
public class Authenticaton {

    private String message;

    public Authenticaton(String message) {
        this.message = message;
    }
}
