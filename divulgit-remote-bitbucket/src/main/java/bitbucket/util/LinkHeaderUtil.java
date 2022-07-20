package bitbucket.util;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.ResponseEntity;

public class LinkHeaderUtil {
    public static boolean hasNextPage(ResponseEntity<String> response) {
        String linkHeaderValue = response.getHeaders().getFirst("Link");
        return Strings.isNotEmpty(linkHeaderValue) && linkHeaderValue.contains("rel=\"next\"");
    }
}
