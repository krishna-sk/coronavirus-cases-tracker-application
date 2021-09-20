package com.krishna.coronavirustracker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationStats {
	
	private Integer totalCases;
	private Integer diff;
	
}
