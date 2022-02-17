package com.donglaistd.jinli.database.entity;

import com.donglaistd.jinli.Jinli;

import java.io.Serializable;

public class TaskRecord implements Serializable {
    private long progress;
    private final long targetProgress;

    public TaskRecord( long targetProgress) {
        this.targetProgress = targetProgress;
    }

    public void addProgram(long program){
        this.progress += program;
        if(this.progress > targetProgress)
            this.progress = targetProgress;
    }

    public long getProgress() {
        return progress;
    }

    public long getTargetProgress() {
        return targetProgress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public boolean isFinish(){
        return progress >= targetProgress;
    }

    public Jinli.TaskRecord toProto(){
        return Jinli.TaskRecord.newBuilder().setProgressAmount(progress).setTargetAmount(targetProgress).build();
    }
}
