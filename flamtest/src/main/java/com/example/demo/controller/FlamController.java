package com.example.demo.controller;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.NodeAInfo;
import com.example.demo.repository.FlamRepository;
import com.example.demo.service.FlamService;

@RestController

public class FlamController {
	@Autowired
	private FlamService flamservice;
		
	@Autowired
	private FlamRepository repo;
	
	@GetMapping("/req/{layer}")
	public String getStatus(@PathVariable int layer) {
		
		flamservice.getstatusclayer(layer);
		
	  return "success";
	}
	@PostMapping("/req/{currentLayer}")
	public String update(@RequestBody String status ,@PathVariable int currentLayer) {
	
		repo.updateByStatus(status, currentLayer);
		return "success";
	}
}
