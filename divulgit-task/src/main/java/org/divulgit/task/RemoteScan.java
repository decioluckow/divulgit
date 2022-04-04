package org.divulgit.task;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
