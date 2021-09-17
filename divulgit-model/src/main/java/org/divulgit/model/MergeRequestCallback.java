package org.divulgit.model;

import org.bson.Document;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveCallback;
import org.springframework.stereotype.Component;

@Component
class MergeRequestCallback implements BeforeSaveCallback<MergeRequest> {

	@Override
	public MergeRequest onBeforeSave(MergeRequest mergeRequest, Document document, String s) {
		mergeRequest.calculateComments();
		document.put("commentsTotal", mergeRequest.getCommentsTotal());
		document.put("commentsDiscussed", mergeRequest.getCommentsDiscussed());
		return mergeRequest;
	}
}