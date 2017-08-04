package com.lsh.base.data.internal.monitor;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;

import com.lsh.base.data.internal.monitor.*;

/**
 * Created by wuhao on 16/5/28.
 */
public class MonitoringExecutionListener {
    private BatchMonitoringNotifier monitoringNotifier;

    @BeforeJob
    public void executeBeforeJob(JobExecution jobExecution) {
        // Do nothing
    }

    @AfterJob
    public void executeAfterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.FAILED) {
            // Notify when job fails
            monitoringNotifier.notify(jobExecution);
        }
    }

    public void setMonitoringNotifier(BatchMonitoringNotifier monitoringNotifier) {
        this.monitoringNotifier = monitoringNotifier;
    }
}
