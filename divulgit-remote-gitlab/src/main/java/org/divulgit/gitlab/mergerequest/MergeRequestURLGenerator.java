package org.divulgit.gitlab.mergerequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.gitlab.error.ErrorMapper;
import org.divulgit.gitlab.error.ErrorMessage;
import org.divulgit.gitlab.restcaller.GitLabRestCaller;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.util.URLUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class MergeRequestURLGenerator {

    @Value("${rest.caller.pageSize:50}")
    private int pageSize;

    public String build(final Remote remote, Project project, List<Integer> requestedMergeRequestExternalIds, final String page) {
        String idParams = URLUtil.toListOfParams(requestedMergeRequestExternalIds, "iids[]");
        return MessageFormat.format("https://{0}/api/v4/projects/{1}/merge_requests?per_page={2}&page={3}{4}",
                remote.getUrl(),
                project.getExternalId(),
                String.valueOf(pageSize),
                page,
                URLUtil.prepareToConcat(idParams, true));
    }
}
