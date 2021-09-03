package org.divulgit.task;

public interface TaskListener {

    void onCreation();
    void onStart();
    void onStartStep();
    void onFinishStep();
    void onFinish();
    void onError();
}
