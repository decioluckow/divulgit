package org.divulgit.service.mergeRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.User;
import org.divulgit.repository.MergeRequestRepository;
import org.divulgit.vo.UserProjectVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableList;

@Service
public class MergeRequestService {

    private MergeRequestRepository mergeRequestRepository;

    @Autowired
    private MergeRequestService(MergeRequestRepository mergeRequestRepository) {
        this.mergeRequestRepository = mergeRequestRepository;
    }

    public Optional<Integer> findLastExternalId(Project project) {
        Optional<MergeRequest> mergeRequest = mergeRequestRepository.findFirstByProjectIdOrderByExternalIdDesc(project.getId());
        Optional<Integer> LastExternalId = Optional.empty();
        if (mergeRequest.isPresent())
            LastExternalId = Optional.of(mergeRequest.get().getExternalId());
        return LastExternalId;
    }

    public List<MergeRequest> findAllByIds(List<String> mergeRequestIds) {
        return ImmutableList.copyOf(mergeRequestRepository.findAllById(mergeRequestIds));
    }

    public Optional<MergeRequest> findById(String mergeRequestId) {
        return mergeRequestRepository.findById(mergeRequestId);
    }

    public List<MergeRequest> findAllWithHashTaggedCommentsByProjectId(Project project) {
        return mergeRequestRepository.findByProjectIdAndAndCommentsTotalGreaterThanOrderByExternalIdDesc(project.getId(), 0);
    }


    public MergeRequest save(MergeRequest mergeRequest) {
        return mergeRequestRepository.save(mergeRequest);
    }

    public List<MergeRequest> saveAll(List<MergeRequest> mergeRequests) {
        return mergeRequestRepository.saveAll(mergeRequests);
    }
}
