package org.divulgit.model;

import java.util.ArrayList;

import org.bson.Document;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertCallback;
import org.springframework.stereotype.Component;

@Component
class UserCallback implements AfterConvertCallback<User> {

	@Override
	public User onAfterConvert(User user, Document document, String s) {
		if (user.getUserProjects() == null)
			user.setUserProjects(new ArrayList<>());
		return user;
	}
}