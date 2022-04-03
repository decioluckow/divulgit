package org.divulgit.remote.model;

import org.divulgit.model.Project;

public interface RemoteProject {

    String getExternalId();
    String getName();
    String getDescription();

    Project convertToProject();
}
