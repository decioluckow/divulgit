package org.divulgit.azure.pullrequest;

import org.divulgit.azure.AzureURLBuilder;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.remote.exception.RemoteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AzurePullRequestService {

    @Autowired
    private AzurePullRequestsCaller azurePullRequestsCaller;

    @Autowired
    private AzureLastPullRequestCaller azureLastPullRequestCaller;

    @Autowired
    private AzureURLBuilder urlBuilder;

    public List<AzurePullRequest> retrievePullRequests(
            Remote remote,
            User user,
            Project project,
            List<Integer> externalIds,
            Authentication authentication) throws RemoteException {
        List<AzurePullRequest> azurePullRequests = azurePullRequestsCaller.retrievePullRequests(remote, user, project, externalIds, authentication);
        azurePullRequests.forEach(pr -> fillWebURL(project, pr));
        return azurePullRequests;
    }

    public List<AzurePullRequest> retrievePullRequests(
            Remote remote,
            User user,
            Project project,
            Integer scanFrom,
            Authentication authentication) throws RemoteException {
        List<AzurePullRequest> azurePullRequests = azurePullRequestsCaller.retrievePullRequests(remote, user, project, scanFrom, authentication);
        azurePullRequests.forEach(pr -> fillWebURL(project, pr));
        return azurePullRequests;
    }

    private void fillWebURL(Project project, AzurePullRequest azurePullRequest) {
        azurePullRequest.setUrl(urlBuilder.buildPullRequestWebURL(project.getUrl(), azurePullRequest.getExternalId()));
    }
}
