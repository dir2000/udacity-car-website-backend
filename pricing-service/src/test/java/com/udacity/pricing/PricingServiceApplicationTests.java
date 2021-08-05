package com.udacity.pricing;

import static org.junit.Assert.*;

import com.udacity.pricing.domain.price.Price;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PricingServiceApplicationTests {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void getAllPrices() {
        ResponseEntity<Map> response = restTemplate.getForEntity(getUrl(), Map.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void createPrice() {
		Price price = insertPrice(1L);
		assertNotNull(price);
        assertEquals(price.getCurrency(), "USD");
    }

	@Test
	public void readPrice() {
		long vehicleId = 2L;
		insertPrice(vehicleId);
		String resourceUrl = getUrl() + '/' + vehicleId;
		ResponseEntity<Price> response = restTemplate.getForEntity(resourceUrl, Price.class);
		Price readPrice = response.getBody();

		assertEquals(response.getStatusCode(), HttpStatus.OK);
		assertNotNull(readPrice);
		long readVehicleId = readPrice.getVehicleId();
		assertEquals(readVehicleId, vehicleId);
	}

	@Test
	public void updatePrice() {
		long vehicleId = 3L;
		Price price = insertPrice(vehicleId);

		// Update Resource
		String newCurrency = "EUR";
		Price updatedInstance = new Price(vehicleId, newCurrency, BigDecimal.valueOf(14000));
		String resourceUrl = getUrl() + '/' + vehicleId;
		HttpEntity<Price> requestUpdate = new HttpEntity<>(updatedInstance);
		restTemplate.exchange(resourceUrl, HttpMethod.PUT, requestUpdate, Void.class);

		// Check that Resource was updated
		ResponseEntity<Price> updateResponse = restTemplate.exchange(resourceUrl, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Price.class);
		Price updatedPrice = updateResponse.getBody();
		assertEquals(updatedPrice.getCurrency(), newCurrency);
	}

	private String getUrl() {
		return "http://localhost:" + port + "/prices";
	}

	private Price insertPrice(Long vehicleId) {
		HttpEntity<Price> request = new HttpEntity<>(new Price(vehicleId, "USD", BigDecimal.valueOf(12000)));
		Price price = restTemplate.postForObject(getUrl(), request, Price.class);
		return price;
	}
}
