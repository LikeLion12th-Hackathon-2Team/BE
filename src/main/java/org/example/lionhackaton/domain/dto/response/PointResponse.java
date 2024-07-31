package org.example.lionhackaton.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class PointResponse {
    private Long point;
    private Long donatePoint;

}
