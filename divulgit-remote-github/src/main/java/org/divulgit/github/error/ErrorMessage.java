package org.divulgit.github.error;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorMessage {

    private String message;
    private String documentation_url;
}
