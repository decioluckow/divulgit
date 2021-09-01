package br.com.decioluckow.divulgit.restcaller.error;

import lombok.Data;

@Data
public class ErrorMessage {

    private String error;
    private String errorDescription;
}
