package org.walmart.bacnet.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

	/**
	 * Controller Endpoint to check the health status of the application
	 * @return
	 */
	@GetMapping(value = "/health-status")
	public String healthStatus () {
		
		return "Ok";
	}
}
