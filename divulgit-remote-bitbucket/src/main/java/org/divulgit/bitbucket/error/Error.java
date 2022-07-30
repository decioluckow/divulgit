package org.divulgit.bitbucket.error;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Error {
    private String message;
    private String detail;
}
