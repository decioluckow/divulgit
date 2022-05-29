package org.divulgit.model;

import lombok.*;
import org.divulgit.type.RemoteType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Setter
@Builder
@Document(collection = "remote")
@NoArgsConstructor
@AllArgsConstructor
public class Remote {

    @Id
    private String id;

    private String url;
    private RemoteType type;

    public Remote(String url) {
        this.url = url;
    }
}
