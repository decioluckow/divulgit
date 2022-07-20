package bitbucket.user;
import bitbucket.Links;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.divulgit.remote.model.RemoteUser;

@Data
public class BitBucketUser implements RemoteUser {

    @JsonProperty("account_id")
    private String internalId;

    @JsonProperty("display_name")
    private String name;

    @JsonProperty("username")
    private String username;

    @JsonProperty("nickname")
    private String nickName;

    private Links links;

    public String getAvatarURL() {
        return getLinks().getAvatar().getHref();
    }
}
