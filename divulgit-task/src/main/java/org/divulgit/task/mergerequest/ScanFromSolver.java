package org.divulgit.task.mergerequest;

import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.gitlab.mergerequest.GitLabMergeRequest;
import org.divulgit.gitlab.mergerequest.MergeRequestCaller;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.repository.UserRepository;
import org.divulgit.service.MergeRequestService;
import org.divulgit.task.RemoteScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
class ScanFromSolver {

    @Autowired
    private MergeRequestService mergeRequestService;

    @Autowired
    private ScanFromSolver(MergeRequestService mergeRequestService) {
        this.mergeRequestService = mergeRequestService;
    }

    public Integer solveScanFrom(Optional<Integer> requestedScanFrom, Project project) {
        Optional<Integer> lastExternalId = mergeRequestService.findLastExternalId(project);
        Integer scanFrom = 0;
        if (lastExternalId.isPresent() && requestedScanFrom.isEmpty()) {
            scanFrom = lastExternalId.get() + 1;
        } else if (lastExternalId.isEmpty() && requestedScanFrom.isPresent()) {
            scanFrom = requestedScanFrom.get();
        } else if (lastExternalId.isPresent() && requestedScanFrom.isPresent()) {
            scanFrom = Math.max(lastExternalId.get() + 1, requestedScanFrom.get());
        }
        return scanFrom;
    }
}
