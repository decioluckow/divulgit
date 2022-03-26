package org.divulgit.github.error;

import lombok.Data;

@Data
public class ErrorMessage {

    private String message;
    private String documentation_url;
}
