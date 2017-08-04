package com.lsh.base.data.internal.monitor;

import org.springframework.batch.core.JobExecution;

/**
 * Created by wuhao on 16/5/28.
 */
public interface BatchMonitoringNotifier {
    void notify(JobExecution jobExecution);
}
