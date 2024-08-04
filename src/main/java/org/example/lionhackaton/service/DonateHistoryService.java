package org.example.lionhackaton.service;

import java.util.List;

import org.example.lionhackaton.domain.DonateHistory;
import org.example.lionhackaton.domain.dto.request.DonateHistoryRequest;
import org.example.lionhackaton.domain.dto.response.DonateHistoryResponse;
import org.example.lionhackaton.domain.oauth.CustomUserDetails;
import org.example.lionhackaton.repository.DonateHistoryRepository;
import org.example.lionhackaton.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class DonateHistoryService {

	private final DonateHistoryRepository donateHistoryRepository;
	private final UserRepository userRepository;

	public DonateHistoryService(DonateHistoryRepository donateHistoryRepository, UserRepository userRepository) {
		this.donateHistoryRepository = donateHistoryRepository;
		this.userRepository = userRepository;
	}

	public void saveDonateHistory(CustomUserDetails customUserDetails, DonateHistoryRequest donateHistoryRequest) {
		userRepository.findById(customUserDetails.getId()).ifPresent(user -> {
			if (user.getPoint() < donateHistoryRequest.getPoint()) {
				throw new RuntimeException("Insufficient point");
			}
			user.setPoint(user.getPoint() - donateHistoryRequest.getPoint());
			DonateHistory donateHistory = new DonateHistory();
			donateHistory.setPoint(donateHistoryRequest.getPoint());
			donateHistory.setLocation(donateHistoryRequest.getLocation());
			donateHistory.setUser(user);
			userRepository.save(user);
			donateHistoryRepository.save(donateHistory);
		});
	}

	public List<DonateHistoryResponse> getDonateHistory(CustomUserDetails customUserDetails) {
		List<DonateHistoryResponse> list = new java.util.ArrayList<>(
			donateHistoryRepository.findAllByUserId(customUserDetails.getId())
				.stream()
				.map(donateHistory -> new DonateHistoryResponse(
					donateHistory.getDonateHistoryId(),
					donateHistory.getPoint(),
					donateHistory.getLocation(),
					donateHistory.getUser().getId(),
					donateHistory.getCreatedAt()
				))
				.toList());
		if (list.isEmpty()) {
			list.add(new DonateHistoryResponse(null, null, null, null, null));
		}
		return list;
	}
}
