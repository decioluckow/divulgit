package org.divulgit.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.divulgit.type.RemoteType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Setter
@Builder
@Document(collection = "remote")
public class Remote {

    @Id
    private String id;

    private String url;
    private RemoteType type;
}