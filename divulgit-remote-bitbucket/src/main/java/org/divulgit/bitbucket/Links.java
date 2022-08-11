package org.divulgit.bitbucket;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Links {
    private Link avatar;
    private Link html;
    public static class Link {
        @Getter@Setter
        private String href;
    }
}



