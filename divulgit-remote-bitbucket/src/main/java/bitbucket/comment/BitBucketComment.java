package bitbucket.comment;
import bitbucket.Links;
import com.fasterxml.jackson.annotation.JsonProperty;
import bitbucket.user.BitBucketUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.divulgit.model.MergeRequest;
import org.divulgit.remote.model.RemoteComment;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BitBucketComment implements RemoteComment {
    @JsonProperty("id")
    private String externalId;
    private BitBucketUser user;

    private Content content;

    private Links links;

    public String getUrl() {
        return getLinks().getHtml().getHref();
    }

    public String getText() {
        return getContent().getRaw();
    }

    @Override
    public String getAuthor() {
        return user.getNickName();
    }

    public MergeRequest.Comment.CommentBuilder toComment() {
        return MergeRequest.Comment.builder()
                .externalId(String.valueOf(externalId))
                .text(getText())
                .url(getUrl())
                .author(user.getNickName());
    }
}
