package com.dentist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DentistDBSyncApplication
{
	
	public static void main(String[] args)
	{
		SpringApplication.run(DentistDBSyncApplication.class, args);
	}
}
