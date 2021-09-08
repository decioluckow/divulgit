package org.divulgit.task.comment;

import lombok.extern.slf4j.Slf4j;
import org.divulgit.model.Project;
import org.divulgit.service.MergeRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
            scanFrom = lastExternalId.get();
        } else if (lastExternalId.isEmpty() && requestedScanFrom.isPresent()) {
            scanFrom = requestedScanFrom.get();
        } else if (lastExternalId.isPresent() && requestedScanFrom.isPresent()) {
            scanFrom = Math.max(lastExternalId.get(), requestedScanFrom.get());
        }
        return scanFrom;
    }
}
