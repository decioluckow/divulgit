package br.com.decioluckow.divulgit.model;

import br.com.decioluckow.divulgit.model.type.OriginType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document(collection = "origin")
public class Origin {

    @Id
    private String id;

    private String url;
    private OriginType type;
}
