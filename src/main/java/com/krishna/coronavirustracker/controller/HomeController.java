package com.krishna.coronavirustracker.controller;

import java.text.NumberFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.krishna.coronavirustracker.data.CoronaVirusDataService;
import com.krishna.coronavirustracker.model.LocationStats;

@Controller
public class HomeController {

	@Autowired
	private CoronaVirusDataService coronaVirusDataService;

	private Map<String, LocationStats> allStats;
	private Map<Object, Object> res;
	private Integer totalCases;
	private Integer totalNewCases;
	private NumberFormat myFormat = NumberFormat.getInstance();

	@PostConstruct
	public void init() {
		allStats = coronaVirusDataService.getAllStats();
		totalCases = coronaVirusDataService.getTotal_Cases_In_A_Single_Day();
		totalNewCases = coronaVirusDataService.getTotal_Differeence_In_Cases();
	}

	@GetMapping("/")
	public String home(Model model) {
		Integer pageNo = 1;
		res = allStats.entrySet().stream().limit(15)
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		model.addAttribute("data", res);
		model.addAttribute("totalCases", myFormat.format(totalCases));
		model.addAttribute("totalNewCases", myFormat.format(totalNewCases));
		model.addAttribute("date", String.format("%tB %<te, %<tY", new Date()));
		model.addAttribute("currentPageNo", pageNo);
		model.addAttribute("totalPages", (allStats.size() / 20));
		return "home";
	}

	@GetMapping("/{pageNo}")
	public String homeModified(@PathVariable(name = "pageNo") Integer pageNo, Model model) {
		

		if (pageNo > (allStats.size() / 20) || pageNo<=0)
			pageNo = 1;

		res = allStats.entrySet().stream().skip((pageNo - 1) * 20).limit(20)
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

		model.addAttribute("data", res);
		model.addAttribute("totalCases", myFormat.format(totalCases));
		model.addAttribute("totalNewCases", myFormat.format(totalNewCases));
		model.addAttribute("date", String.format("%tB %<te, %<tY", new Date()));
		model.addAttribute("currentPageNo", pageNo);
		model.addAttribute("totalPages", (allStats.size() / 20));
		return "home";
	}
}
