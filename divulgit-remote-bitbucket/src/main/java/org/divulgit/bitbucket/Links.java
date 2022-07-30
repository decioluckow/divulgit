package org.divulgit.bitbucket;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//TODO procurar forma de melhorar a navegacao entre nós
public class Links {
    private Link avatar;
    private Link html;
    public static class Link {
        @Getter@Setter
        private String href;
    }
}



