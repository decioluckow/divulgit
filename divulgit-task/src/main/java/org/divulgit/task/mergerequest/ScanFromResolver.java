package org.divulgit.task.mergerequest;

import java.util.Optional;

import org.divulgit.model.Project;
import org.divulgit.service.mergeRequest.MergeRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
class ScanFromResolver {

    private MergeRequestService mergeRequestService;

    @Autowired
    public ScanFromResolver(MergeRequestService mergeRequestService) {
        this.mergeRequestService = mergeRequestService;
    }

    public Integer resolveScanFrom(Optional<Integer> requestedScanFrom, Project project) {
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
