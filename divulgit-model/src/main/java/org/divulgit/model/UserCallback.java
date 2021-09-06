package org.divulgit.model;

import org.bson.Document;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertCallback;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveCallback;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
class UserCallback implements AfterConvertCallback<User> {

	@Override
	public User onAfterConvert(User user, Document document, String s) {
		if (user.getProjectIds() == null)
			user.setProjectIds(new ArrayList<>());
		return user;
	}
}