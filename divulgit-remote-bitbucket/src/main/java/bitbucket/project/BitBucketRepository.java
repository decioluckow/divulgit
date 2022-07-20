package bitbucket.project;
import bitbucket.Links;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import org.divulgit.model.Project;
import org.divulgit.remote.model.RemoteProject;

@Getter
@Data
public class BitBucketRepository implements RemoteProject {
    @JsonProperty("uuid")
    private String externalId;

    private Links links;
    private String name;
    private String description;

    public String getUrl() {
        return getLinks().getHtml().getHref();
    }
    public Project convertToProject() {
        return Project.builder().externalId(externalId).url(getUrl()).name(name).description(description).build();
    }
}
