package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.NodeAInfo;
import com.example.demo.repository.FlamRepository;

@Service
public class FlamService {

	@Autowired
	private FlamRepository flrepo;

	public void getstatusclayer(int layer) {
		try {

			List<NodeAInfo> list = new ArrayList<NodeAInfo>();
			list = flrepo.findAll();
			for (NodeAInfo nodeAInfo : list) {
				if (nodeAInfo.getCurrentLayer() == layer) {
					nodeAInfo.setStatus("Bonded");
					flrepo.save(nodeAInfo);
					System.out.println(nodeAInfo.toString());
				}
			}

			System.out.println();

		} catch (Exception e) {
			e.getMessage();
		}

	}
}
