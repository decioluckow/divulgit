package org.divulgit.model.util;

import java.util.List;
import java.util.Optional;

import org.divulgit.model.MergeRequest;

public class MergeRequestUtil {
	
    public static MergeRequest.Comment getComment(List<MergeRequest.Comment> comments, String commentExternalId) {
        Optional<MergeRequest.Comment> comment = comments.stream()
                .filter(c -> commentExternalId.equals(c.getExternalId())).findFirst();
        if (comment.isPresent())
        	return comment.get();
        else
        	throw new IllegalStateException("Comment " + commentExternalId + " not found for merge request");
    }

}
