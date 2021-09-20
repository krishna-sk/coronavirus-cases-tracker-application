package com.krishna.coronavirustracker.data;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.krishna.coronavirustracker.model.LocationStats;

@Component
public class CoronaVirusDataService {

	private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/"
			+ "csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

	private Map<String, LocationStats> allStats = new LinkedHashMap<>();

	private Integer totalCases=0;
	private Integer totalChangeInCases=0;

	public Map<String, LocationStats> getAllStats() {
		return allStats;
	}

	public Integer getTotal_Cases_In_A_Single_Day() {
		return totalCases;
	}

	public Integer getTotal_Differeence_In_Cases() {
		return totalChangeInCases;
	}
	
	@PostConstruct
	@Scheduled(cron = "* * 0/1 * * * ")
	public void fetchCoronaVirusData() throws IOException, InterruptedException {
		Map<String, LocationStats> newStats = new HashMap<>();

		HttpClient httpClient = HttpClient.newHttpClient();
		HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build();
		HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

		StringReader csvBodyReader = new StringReader(httpResponse.body());
		Iterable<CSVRecord> records = CSVFormat.Builder.create().setHeader().build().parse(csvBodyReader);

		for (CSVRecord record : records) {

			String country = record.get("Country/Region");
			Integer todayCases = Integer.parseInt(record.get(record.size() - 1));
			Integer previousDayCases = Integer.parseInt(record.get(record.size() - 2));
			
			totalCases += todayCases;
			totalChangeInCases += (previousDayCases - todayCases);

			LocationStats locationStats = LocationStats.builder().totalCases(todayCases)
					.diff(previousDayCases - todayCases).build();

			if (newStats.containsKey(country)) {
				LocationStats tempLocationStats = newStats.get(country);
				tempLocationStats.setTotalCases(tempLocationStats.getTotalCases() + locationStats.getTotalCases());
				tempLocationStats.setDiff(tempLocationStats.getDiff() + locationStats.getDiff());
			} else {
				newStats.put(country, locationStats);
			}
		}
		this.allStats = newStats;
	}

}
