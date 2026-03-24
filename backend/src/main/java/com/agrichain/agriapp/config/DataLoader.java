package com.agrichain.agriapp.config;

import com.agrichain.agriapp.model.Crop;
import com.agrichain.agriapp.model.Farmer;
import com.agrichain.agriapp.model.Field;
import com.agrichain.agriapp.model.User;
import com.agrichain.agriapp.repository.UserRepository;
import com.agrichain.agriapp.service.CropService;
import com.agrichain.agriapp.service.FieldService;
import com.agrichain.agriapp.service.FarmerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

	private final FarmerService farmerService;
	private final FieldService fieldService;
	private final CropService cropService;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public DataLoader(
			FarmerService farmerService,
			FieldService fieldService,
			CropService cropService,
			UserRepository userRepository,
			PasswordEncoder passwordEncoder
	) {
		this.farmerService = farmerService;
		this.fieldService = fieldService;
		this.cropService = cropService;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) {
		seedDefaultAdminUser();

		// Prevent duplicate sample data on hot reload / restarts.
		if (!farmerService.getAll().isEmpty()) {
			return;
		}

		Farmer farmer1 = new Farmer();
		farmer1.setName("John Farmer");
		farmer1.setRegion("North");
		farmer1.setExperienceYears(5);

		Farmer createdFarmer1 = farmerService.create(farmer1);

		Farmer farmer2 = new Farmer();
		farmer2.setName("Mary Grower");
		farmer2.setRegion("South");
		farmer2.setExperienceYears(3);

		Farmer createdFarmer2 = farmerService.create(farmer2);

		Field field1 = new Field();
		field1.setName("Green Acres");
		field1.setArea(10.5);
		field1.setFarmerId(createdFarmer1.getId());

		Field createdField1 = fieldService.create(field1);

		Field field2 = new Field();
		field2.setName("Sunny Farm");
		field2.setArea(7.2);
		field2.setFarmerId(createdFarmer2.getId());

		Field createdField2 = fieldService.create(field2);

		Crop crop1 = new Crop();
		crop1.setName("Wheat Harvest");
		crop1.setType("WHEAT");
		crop1.setFieldId(createdField1.getId());

		Crop createdCrop1 = cropService.create(crop1);

		Crop crop2 = new Crop();
		crop2.setName("Tomato Planting");
		crop2.setType("TOMATO");
		crop2.setFieldId(createdField2.getId());

		Crop createdCrop2 = cropService.create(crop2);

		logger.info("Sample data loaded: farmers={}, fields={}, crops={}",
				farmerService.getAll().size(),
				fieldService.getAll().size(),
				cropService.getAll().size());
	}

	private void seedDefaultAdminUser() {
		if (userRepository.findByUsername("admin").isPresent()) {
			return;
		}

		User admin = new User();
		admin.setUsername("admin");
		admin.setPassword(passwordEncoder.encode("admin"));
		admin.setRole("ADMIN");
		userRepository.save(admin);

		logger.info("Default login user created: username='admin'");
	}
}

