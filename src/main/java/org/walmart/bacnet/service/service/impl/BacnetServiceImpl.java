package org.walmart.bacnet.service.service.impl;

import java.util.List;
import java.util.Set;

import org.code_house.bacnet4j.wrapper.api.BacNetClient;
import org.code_house.bacnet4j.wrapper.api.BacNetObject;
import org.code_house.bacnet4j.wrapper.api.BypassBacnetConverter;
import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.ip.BacNetIpClient;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import ch.qos.logback.classic.Logger;

@Configuration
@EnableScheduling
public class BacnetServiceImpl {

	Logger logger = (Logger) LoggerFactory.getLogger(BacnetServiceImpl.class);

	@Value("${broadcast.address}")
	private String broadcastAddr;
	
	/**
	 * Generating a BACnet client with the Broadcast IP to search for BACnet devices 
	 * within the defined subnet
	 * 
	 */
	@Scheduled(fixedRate = 10000)
	public Set<Device> sendWhoIsRequest() {
		Set<Device> devicesFound = null;

		logger.info("Broadcast Address :: " + broadcastAddr);
		int clientDeviceId = 2002;
		BacNetIpClient client = new BacNetIpClient("0.0.0.0", broadcastAddr, clientDeviceId);
		client.start();

		devicesFound = scanForDevices(client);
		client.stop();
		return devicesFound;
	}

	/**
	 * This method is used to discover BACnet devices under the same subnet
	 * Also, this method fetches the list of objects configured for the device.
	 * 
	 * @param client
	 * @return Set<Device>
	 */
	private Set<Device> scanForDevices(BacNetClient client) {
		logger.info("Discovering devices.");

		Set<Device> devices = client.discoverDevices(5000); // given number is timeout in millis

		logger.info("Found devices: " + devices.size());
		for (Device device : devices) {
			List<BacNetObject> bacnetObjects = client.getDeviceObjects(device);
			logger.info("Found Objects: " + bacnetObjects.size());
			for (BacNetObject bacnetObject : bacnetObjects) {
				readObjectValue(client, bacnetObject);
			}
			
		}
		return devices;
	}
	
	/**
	 * This method is responsible for fetching the property values from actual device
	 * 
	 * @param client
	 * @param object
	 */
	private void readObjectValue(BacNetClient client, BacNetObject bacnetObject) {
		try {
			if (bacnetObject.getUnits() != null) {
				logger.info("Property Name : " + bacnetObject.getDescription() + " : Present Value : "
						+ client.getPresentValue(bacnetObject, new BypassBacnetConverter()) + " " + bacnetObject.getUnits());
			} else {
				logger.info("Property Name : " + bacnetObject.getDescription() + " : Present Value : "
						+ client.getPresentValue(bacnetObject, new BypassBacnetConverter()));
			}
		} catch (Exception ex) {
			logger.error("Property Name : " + bacnetObject.getDescription() + " :: Error occurred : " + ex.getMessage() );
		}
	}
	
}
