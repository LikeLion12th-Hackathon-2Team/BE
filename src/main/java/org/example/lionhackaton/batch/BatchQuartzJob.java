package org.example.lionhackaton.batch;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class BatchQuartzJob extends QuartzJobBean {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private BatchConfig batchConfig;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		try {
			jobLauncher.run(batchConfig.userJob(), new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters());
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}
}
