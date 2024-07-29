package org.example.lionhackaton.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DonateHistoryRequest {
	private Long point;
	private String location;
}
