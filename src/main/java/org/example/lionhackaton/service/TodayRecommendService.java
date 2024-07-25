package org.example.lionhackaton.service;

import java.util.Objects;
import java.util.Optional;

import org.example.lionhackaton.domain.ChatGPTRequest;
import org.example.lionhackaton.domain.ChatGPTResponse;
import org.example.lionhackaton.domain.TodayRecommend;
import org.example.lionhackaton.domain.oauth.CustomUserDetails;
import org.example.lionhackaton.repository.TodayRecommendRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TodayRecommendService {

	@Value("${openai.model}")
	private String model;

	@Value("${openai.api.url}")
	private String apiURL;

	private final TodayRecommendRepository todayRecommendRepository;
	private final RestTemplate template;

	public TodayRecommendService(TodayRecommendRepository todayRecommendRepository, RestTemplate template) {
		this.todayRecommendRepository = todayRecommendRepository;
		this.template = template;
	}

	public String getTodayRecommend(CustomUserDetails customUserDetails) {
		Optional<TodayRecommend> todayRecommend = todayRecommendRepository.findByUserId(customUserDetails.getId());
		if (todayRecommend.isPresent()) {
			return todayRecommend.get().getGptRecommend();
		} else {
			ChatGPTRequest chatGPTRequest = getChatGPTRequestRecommend();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", "Bearer " + "sk-None-L1NGcSKoHf6WQyw1rFJoT3BlbkFJXw1grS2f76lqjp5b6ZEJ");

			HttpEntity<ChatGPTRequest> entity = new HttpEntity<>(chatGPTRequest, headers);

			ResponseEntity<ChatGPTResponse> chatGPTResponse = template.exchange(apiURL, HttpMethod.POST, entity,
				ChatGPTResponse.class);

			TodayRecommend newTodayRecommend = new TodayRecommend();
			newTodayRecommend.setUserId(customUserDetails.getId());
			newTodayRecommend.setGptRecommend(
				Objects.requireNonNull(chatGPTResponse.getBody()).getChoices().get(0).getMessage().getContent());
			todayRecommendRepository.save(newTodayRecommend);
			return newTodayRecommend.getGptRecommend();
		}
	}

	private ChatGPTRequest getChatGPTRequestRecommend() {
		String prompt = "넌 이제부터 제품이나, 운동같은걸 추천해줘야해. "
			+ "너가 할일은 내가 일기를 줄건데 그 일기를 읽고 그 일기에 맞는 제품을 추천해주는 거야. "
			+ "그런데 지금은 계정을 방금 만들어서 아직 일기가 없어. 그래서 그냥 랜덤으로 제품을 추천해주면 돼. "
			+ "한국에서 최근 가장 인기있는 제품을 찾아서 추천해줘. 근데 가전제품 이런거 말고, 먹는 제품같은걸 추천해줘"
			+ "답은 짧게 3줄정도로 적어주고, 존댓말로 적어줘. 그리고 3줄 말고는 다른말은 적어주지 마."
			+ "첫째줄은 그 저품에 대한 장점이나 그 제품에 대한 설명, 제품이 어디에 좋은지를 적어주면 돼."
			+ "두번째줄은 아직 일기가 없어서 랜덤으로 추천했다고 적어주면 돼."
			+ "그리고 마지막은 응원글을 적어주면 돼. 잘 적어줄거라고 믿어. "
			+ "Temperature = 0.9, Top-p = 0.5, Tone = warm, Writing-style = converstaional";

		return new ChatGPTRequest(model, prompt);
	}
}
