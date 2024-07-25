package org.example.lionhackaton.batch;

import org.example.lionhackaton.batch.BatchQuartzJob;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.CronScheduleBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

	@Bean
	public JobDetail quartzJobDetail() {
		return JobBuilder.newJob(BatchQuartzJob.class)
			.storeDurably()
			.build();
	}

	@Bean
	public Trigger quartzTrigger() {
		return TriggerBuilder.newTrigger()
			.forJob(quartzJobDetail())
			.withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 * * ?"))
			.build();
	}
}