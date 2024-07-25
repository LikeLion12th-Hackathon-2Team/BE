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
import org.springframework.scheduling.annotation.Scheduled;
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
				paramsBuilder.addString("diaryContent", "No diary updated in the last 24 hours");
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
		String prompt = "넌 이제부터 제품이나, 운동같은걸 추천해줘야해. "
			+ "내가 일기를 줄건데, 그 일기를 읽고 그 일기에 맞는 제품을 추천해줘. "
			+ "만약에 No diary updated in the last 24 hours 라고 적혀있으면, 그냥 랜덤으로 제품을 추천해주면 돼. "
			+ "한국에서 최근 가장 인기있는 제품을 찾아서 추천해줘. 근데 가전제품 이런거 말고, 먹는 제품같은걸 추천해줘"
			+ "답은 짧게 3줄정도로 적어주고, 존댓말로 적어줘. 그리고 3줄 말고는 다른말은 적어주지 마."
			+ "첫째줄은 그 저품에 대한 장점이나 그 제품에 대한 설명, 제품이 어디에 좋은지를 적어주면 돼."
			+ "두번째줄은 일기에 어떤점 떄문에 추천했는지, 만약 일기가 없다면 그냥 어제 쓴 일기가 없어서 랜덤으로 추천했다고 적어주면 돼."
			+ "그리고 마지막은 응원글을 적어주면 돼. 잘 적어줄거라고 믿어. "
			+ "이제 내가 일기를 보여줄게! \n" + diaryContent
			+ "Temperature = 0.9, Top-p = 0.5, Tone = warm, Writing-style = converstaional";

		ChatGPTRequest chatGPTRequest = new ChatGPTRequest(model, prompt);
		return chatGPTRequest;
	}

}
