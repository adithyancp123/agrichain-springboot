package com.agrichain.agriapp.service;

import com.agrichain.agriapp.model.Farmer;
import com.agrichain.agriapp.model.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;

@Service
public class FarmerService {

	private static final Logger logger = LoggerFactory.getLogger(FarmerService.class);

	private final List<Farmer> farmers = new ArrayList<>();
	private long nextId = 1L;

	private final FieldService fieldService;

	public FarmerService(@Lazy FieldService fieldService) {
		this.fieldService = fieldService;
	}

	public Farmer create(Farmer farmer) {
		if (farmer == null) {
			return null;
		}

		logger.info("Creating Farmer: name='{}', region='{}', experienceYears={}",
				farmer.getName(), farmer.getRegion(), farmer.getExperienceYears());

		Farmer created = new Farmer();
		created.setId(nextId++);
		copyInto(created, farmer);

		farmers.add(created);
		logger.info("Farmer created successfully with id={}", created.getId());
		return created;
	}

	public List<Farmer> getAll() {
		return new ArrayList<>(farmers);
	}

	public Farmer getById(Long id) {
		if (id == null) {
			return null;
		}

		for (Farmer farmer : farmers) {
			if (id.equals(farmer.getId())) {
				return farmer;
			}
		}
		return null;
	}

	public Farmer update(Long id, Farmer farmer) {
		if (id == null || farmer == null) {
			return null;
		}

		Farmer existing = getById(id);
		if (existing == null) {
			return null;
		}

		logger.info("Updating Farmer id={} -> name='{}', region='{}', experienceYears={}",
				id, farmer.getName(), farmer.getRegion(), farmer.getExperienceYears());

		copyInto(existing, farmer);
		logger.info("Farmer updated successfully with id={}", id);
		return existing;
	}

	public List<Field> getFieldsByFarmerId(Long farmerId) {
		return fieldService.getByFarmerId(farmerId);
	}

	public List<Farmer> getByRegion(String region) {
		if (region == null || region.isBlank()) {
			return new ArrayList<>();
		}

		List<Farmer> result = new ArrayList<>();
		for (Farmer farmer : farmers) {
			if (farmer.getRegion() != null && farmer.getRegion().equalsIgnoreCase(region)) {
				result.add(farmer);
			}
		}
		return result;
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
		logger.info("Farmer id={} has {} field(s) to cascade delete", id, fieldsToDelete.size());
		for (Field field : fieldsToDelete) {
			fieldService.delete(field.getId());
		}

		boolean removed = farmers.removeIf(farmer -> id.equals(farmer.getId()));
		if (removed) {
			logger.info("Farmer deleted successfully with id={}", id);
		}
		return removed;
	}

	private void copyInto(Farmer target, Farmer source) {
		target.setName(source.getName());
		target.setRegion(source.getRegion());
		target.setExperienceYears(source.getExperienceYears());
	}
}

