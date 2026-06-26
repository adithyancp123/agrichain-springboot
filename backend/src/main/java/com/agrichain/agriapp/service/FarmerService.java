package com.agrichain.agriapp.service;

import com.agrichain.agriapp.model.Farmer;
import com.agrichain.agriapp.model.Field;
import com.agrichain.agriapp.repository.FarmerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;

@Service
public class FarmerService {

	private static final Logger logger = LoggerFactory.getLogger(FarmerService.class);

	private final FarmerRepository farmerRepository;
	private final FieldService fieldService;

	public FarmerService(FarmerRepository farmerRepository, @Lazy FieldService fieldService) {
		this.farmerRepository = farmerRepository;
		this.fieldService = fieldService;
	}

	public Farmer create(Farmer farmer) {
		if (farmer == null) {
			return null;
		}

		Farmer saved = farmerRepository.save(farmer);
		logger.info("Farmer created successfully with id={}", saved.getId());
		return saved;
	}

	public List<Farmer> getAll() {
		return farmerRepository.findAll();
	}

	public Farmer getById(Long id) {
		if (id == null) {
			return null;
		}

		return farmerRepository.findById(id).orElse(null);
	}

	public Farmer update(Long id, Farmer farmer) {
		if (id == null || farmer == null) {
			return null;
		}

		Farmer existing = getById(id);
		if (existing == null) {
			return null;
		}

		copyInto(existing, farmer);
		Farmer saved = farmerRepository.save(existing);
		logger.info("Farmer updated successfully with id={}", id);
		return saved;
	}

	public List<Field> getFieldsByFarmerId(Long farmerId) {
		return fieldService.getByFarmerId(farmerId);
	}

	public List<Farmer> getByRegion(String region) {
		if (region == null || region.isBlank()) {
			return new ArrayList<>();
		}

		return farmerRepository.findByRegionIgnoreCase(region);
	}

	public boolean delete(Long id) {
		if (id == null) {
			return false;
		}

		Farmer existing = getById(id);
		if (existing == null) {
			return false;
		}

		logger.info("Deleting Farmer id={} (cascading to fields)", id);

		// Cascade delete: Farmer -> Fields -> (Crop, IrrigationSchedule)
		List<Field> fieldsToDelete = fieldService.getByFarmerId(id);
		logger.info("Cascade delete: farmerId={}, fieldsToDelete={}", id, fieldsToDelete.size());
		for (Field field : fieldsToDelete) {
			fieldService.delete(field.getId());
		}

		farmerRepository.delete(existing);
		logger.info("Farmer deleted successfully with id={}", id);
		return true;
	}

	private void copyInto(Farmer target, Farmer source) {
		target.setName(source.getName());
		target.setRegion(source.getRegion());
		target.setExperienceYears(source.getExperienceYears());
	}
}
