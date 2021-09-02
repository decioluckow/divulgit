package org.divulgit.gitlab.error;

import lombok.Data;

@Data
public class ErrorMessage {

    private String error;
    private String errorDescription;
}
