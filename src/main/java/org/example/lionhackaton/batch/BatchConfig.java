package org.example.lionhackaton.batch;

import static java.lang.Thread.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.example.lionhackaton.domain.ChatGPTRequest;
import org.example.lionhackaton.domain.ChatGPTResponse;
import org.example.lionhackaton.domain.Diary;
import org.example.lionhackaton.domain.TodayRecommend;
import org.example.lionhackaton.domain.User;
import org.example.lionhackaton.repository.TodayRecommendRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableBatchProcessing
@EnableScheduling
@Slf4j
public class BatchConfig {

	@Value("${openai.model}")
	private String model;

	@Value("${openai.api.url}")
	private String apiURL;

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final JobLauncher jobLauncher;
	private final EntityManagerFactory entityManagerFactory;
	private final RestTemplate template;
	private final TodayRecommendRepository todayRecommendRepository;

	@Autowired
	public BatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager,
		JobLauncher jobLauncher, EntityManagerFactory entityManagerFactory, RestTemplate template,
		TodayRecommendRepository todayRecommendRepository) {
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.jobLauncher = jobLauncher;
		this.entityManagerFactory = entityManagerFactory;
		this.template = template;
		this.todayRecommendRepository = todayRecommendRepository;
	}

	@Bean
	public Job userJob() {
		return new JobBuilder("userJob", jobRepository)
			.start(userStep())
			.build();
	}

	@Bean
	public TaskletStep userStep() {
		return new StepBuilder("userStep", jobRepository)
			.tasklet(userTasklet(), transactionManager)
			.build();
	}

	@Bean
	public Tasklet userTasklet() {
		return (contribution, chunkContext) -> {
			JpaPagingItemReader<User> reader = new JpaPagingItemReaderBuilder<User>()
				.name("userItemReader")
				.entityManagerFactory(entityManagerFactory)
				.queryString("SELECT u FROM User u")
				.pageSize(10)
				.build();
			reader.open(chunkContext.getStepContext().getStepExecution().getExecutionContext());
			User user;
			while ((user = reader.read()) != null) {
				Set<Diary> diaries = user.getDiaries();
				Optional<Diary> latestDiary = diaries.stream()
					.max(Comparator.comparing(Diary::getUpdatedAt));
				if (latestDiary.isPresent() && latestDiary.get()
					.getUpdatedAt()
					.isAfter(LocalDateTime.now().minusDays(1))) {
					sleep(100);
					recommendJobs(latestDiary.get(), user);
				} else {
					sleep(100);
					recommendJobs(null, user);
				}
			}
			reader.close();
			return RepeatStatus.FINISHED;
		};
	}

	public void perform() throws Exception {
		JobParameters params = new JobParametersBuilder()
			.addLong("time", System.currentTimeMillis())
			.toJobParameters();
		jobLauncher.run(userJob(), params);
	}

	public void recommendJobs(Diary diary, User user) {
		try {
			TaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
			JobParametersBuilder paramsBuilder = new JobParametersBuilder()
				.addLong("time", System.currentTimeMillis());

			if (diary != null) {
				paramsBuilder.addString("userId", user.getId().toString());
				paramsBuilder.addString("diaryContent", diary.getContent());
			} else {
				paramsBuilder.addString("userId", user.getId().toString());
				paramsBuilder.addString("diaryContent", "랜덤 추천");
			}

			JobParameters params = paramsBuilder.toJobParameters();

			taskExecutor.execute(() -> {
				try {
					jobLauncher.run(recommendJob(), params);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Bean
	public Job recommendJob() {
		return new JobBuilder("recommendStep", jobRepository)
			.start(recommendStep())
			.build();
	}

	@Bean
	public TaskletStep recommendStep() {
		return new StepBuilder("recommendStep", jobRepository)
			.tasklet((contribution, chunkContext) -> {
				String userId = chunkContext.getStepContext().getJobParameters().get("userId").toString();
				String diaryContent = chunkContext.getStepContext().getJobParameters().get("diaryContent").toString();

				ChatGPTRequest chatGPTRequest = getChatGPTRequest(diaryContent);

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				headers.set("Authorization", "Bearer " + "sk-None-L1NGcSKoHf6WQyw1rFJoT3BlbkFJXw1grS2f76lqjp5b6ZEJ");

				HttpEntity<ChatGPTRequest> entity = new HttpEntity<>(chatGPTRequest, headers);

				ResponseEntity<ChatGPTResponse> chatGPTResponse = template.exchange(apiURL, HttpMethod.POST, entity,
					ChatGPTResponse.class);

				Optional<TodayRecommend> todayRecommendRepositoryByUserId = todayRecommendRepository.findByUserId(
					Long.valueOf(userId));

				if (todayRecommendRepositoryByUserId.isPresent()) {
					todayRecommendRepositoryByUserId.get()
						.setGptRecommend(Objects.requireNonNull(chatGPTResponse.getBody())
							.getChoices()
							.get(0)
							.getMessage()
							.getContent());
					todayRecommendRepository.save(todayRecommendRepositoryByUserId.get());
				} else {
					TodayRecommend todayRecommend = new TodayRecommend();
					todayRecommend.setGptRecommend(Objects.requireNonNull(chatGPTResponse.getBody())
						.getChoices()
						.get(0)
						.getMessage()
						.getContent());
					todayRecommend.setUserId(Long.valueOf(userId));
					todayRecommendRepository.save(todayRecommend);
				}

				return RepeatStatus.FINISHED;
			}, transactionManager)
			.build();
	}

	private ChatGPTRequest getChatGPTRequest(String diaryContent) {
		String prompt =
			"I'll show you my diary. It's a diary between small quotes.  diary = '" + diaryContent
				+ "' From now on, you have to look at the diary up there and recommend a product or exercise that fits that diary. [IMPORTANT] Write down the answer in 3 lines\n"
				+ "[IMPORTANT] Don't write anything other than 4 lines.\n"
				+ "Write it down in honorifics. I'll give you a diary, read it and recommend a product that fits your diary. If it says \"랜덤 추천\" you can just randomly recommend a product. Find and recommend the most popular exercise or product in Korea recently. In the first line, you can write down the merits of the product, the description of the product, and where the product is good. In the second line, you can write down why you recommended it in your diary, and if you don't have a diary, you can just write it down randomly because there was no diary you wrote yesterday. And in the end, you can write down your support. I believe you will write it down well. Temperature = 0.9, Top-p = 0.5, Tone = warm, Writing-style = conversational"
				+ "[IMPORTANT] DO not say any words except 3lines such as diaryContent or title or 다이어리 : , And say the result in Korean.";

		return new ChatGPTRequest(model, prompt);
	}

}
