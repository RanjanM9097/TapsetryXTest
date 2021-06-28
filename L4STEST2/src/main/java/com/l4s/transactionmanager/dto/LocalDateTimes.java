package com.l4s.transactionmanager.dto;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class LocalDateTimes {
	@Value("${EST.zodeid}")
	public String estZodeId;

	@Bean
	public String getDate() {
		String formatDateTime = null;
		try {
			LocalDateTime now = LocalDateTime.now(ZoneId.of(estZodeId));
			DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			formatDateTime = now.format(format).replaceAll("-", "");
		} catch (Exception ex) {

		}
		return formatDateTime;
	}

	@Bean
	public String getDateTime() {
		String formatDateTime = null;
		try {
			LocalDateTime now = LocalDateTime.now(ZoneId.of(estZodeId));
			DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");
			formatDateTime = now.format(format).replaceAll("-", "");
		} catch (Exception ex) {
		}
		return formatDateTime;
	}

	@Bean
	public Timestamp getTimeStamp() {
		Timestamp time = null;
		try {
			LocalDateTime now = LocalDateTime.now(ZoneId.of(estZodeId));
			time = Timestamp.valueOf(now);
			System.out.println(time);
		} catch (Exception ex) {
		}
		return time;
	}

}
