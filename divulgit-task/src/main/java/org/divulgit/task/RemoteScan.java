package org.divulgit.task;


import com.google.common.collect.ImmutableList;
import lombok.*;
import org.apache.logging.log4j.util.Strings;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface RemoteScan extends Runnable {

    UniqueKey uniqueKey();

    enum State {
        WAITING, RUNNING, FINISHED, ERROR;
    }

    @Getter
    @AllArgsConstructor
    class UniqueKey {
        private String taskUniqueKey;
    }
}
