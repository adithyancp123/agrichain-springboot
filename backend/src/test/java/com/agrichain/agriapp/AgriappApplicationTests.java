package com.agrichain.agriapp;

import com.agrichain.agriapp.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AgriappApplicationTests {

	@Autowired
	private JwtService jwtService;

	@Autowired
	private com.agrichain.agriapp.service.FarmerService farmerService;

	@Test
	void contextLoads() {
	}

	@Test
	void testJwtGenerationAndValidation() {
		String username = "admin";
		String role = "ADMIN";
		String token = jwtService.generateToken(username, role);
		assertNotNull(token);
		
		boolean isValid = jwtService.validateToken(token);
		assertTrue(isValid, "Token should be valid");
		
		assertEquals(username, jwtService.extractUsername(token));
		assertEquals(role, jwtService.extractRole(token));
	}

	@Test
	void testFarmerDatabasePersistence() {
		com.agrichain.agriapp.model.Farmer farmer = new com.agrichain.agriapp.model.Farmer();
		farmer.setName("Test Persisted Farmer");
		farmer.setRegion("East");
		farmer.setExperienceYears(12);

		com.agrichain.agriapp.model.Farmer created = farmerService.create(farmer);
		assertNotNull(created);
		assertNotNull(created.getId());

		com.agrichain.agriapp.model.Farmer fetched = farmerService.getById(created.getId());
		assertNotNull(fetched);
		assertEquals("Test Persisted Farmer", fetched.getName());
		assertEquals("East", fetched.getRegion());
		assertEquals(12, fetched.getExperienceYears());

		fetched.setRegion("West");
		com.agrichain.agriapp.model.Farmer updated = farmerService.update(created.getId(), fetched);
		assertNotNull(updated);
		assertEquals("West", updated.getRegion());

		boolean deleted = farmerService.delete(created.getId());
		assertTrue(deleted);

		com.agrichain.agriapp.model.Farmer deletedFetched = farmerService.getById(created.getId());
		assertNull(deletedFetched);
	}
}
