package com.agrichain.agriapp.service;

import com.agrichain.agriapp.model.Crop;
import com.agrichain.agriapp.model.Distributor;
import com.agrichain.agriapp.model.Field;
import com.agrichain.agriapp.model.Farmer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DistributorService {

	private static final Logger logger = LoggerFactory.getLogger(DistributorService.class);

	private final List<Distributor> distributors = new ArrayList<>();
	private long nextId = 1L;

	private final FarmerService farmerService;
	private final FieldService fieldService;
	private final CropService cropService;

	public DistributorService(FarmerService farmerService, FieldService fieldService, CropService cropService) {
		this.farmerService = farmerService;
		this.fieldService = fieldService;
		this.cropService = cropService;
	}

	public Distributor create(Distributor distributor) {
		if (distributor == null) {
			return null;
		}

		logger.info("Creating Distributor: name='{}', contact='{}', interestedCropType='{}'",
				distributor.getName(), distributor.getContact(), distributor.getInterestedCropType());

		Distributor created = new Distributor();
		created.setId(nextId++);
		copyInto(created, distributor);

		distributors.add(created);
		logger.info("Distributor created successfully with id={}", created.getId());
		return created;
	}

	public List<Distributor> getAll() {
		return new ArrayList<>(distributors);
	}

	public Distributor getById(Long id) {
		if (id == null) {
			return null;
		}

		for (Distributor distributor : distributors) {
			if (id.equals(distributor.getId())) {
				return distributor;
			}
		}
		return null;
	}

	public Distributor update(Long id, Distributor distributor) {
		if (id == null || distributor == null) {
			return null;
		}

		Distributor existing = getById(id);
		if (existing == null) {
			return null;
		}

		logger.info("Updating Distributor id={} -> name='{}', contact='{}', interestedCropType='{}'",
				id, distributor.getName(), distributor.getContact(), distributor.getInterestedCropType());

		copyInto(existing, distributor);
		logger.info("Distributor updated successfully with id={}", id);
		return existing;
	}

	public boolean delete(Long id) {
		if (id == null) {
			return false;
		}

		logger.info("Deleting Distributor id={}", id);
		boolean removed = distributors.removeIf(distributor -> id.equals(distributor.getId()));
		if (removed) {
			logger.info("Distributor deleted successfully with id={}", id);
		}
		return removed;
	}

	/**
	 * Derived relationship: Distributor has many Farmers via interestedCropType.
	 * Distributor -> Crop.type -> Field.fieldId -> Farmer.farmerId
	 */
	public List<Farmer> getFarmersByDistributorId(Long distributorId) {
		if (distributorId == null) {
			return new ArrayList<>();
		}

		Distributor distributor = getById(distributorId);
		if (distributor == null) {
			return new ArrayList<>();
		}

		String interestedType = distributor.getInterestedCropType();
		if (interestedType == null || interestedType.isBlank()) {
			return new ArrayList<>();
		}

		Set<Long> seenFarmerIds = new HashSet<>();
		List<Farmer> result = new ArrayList<>();

		for (Crop crop : cropService.getAll()) {
			if (!interestedType.equals(crop.getType())) {
				continue;
			}

			Long fieldId = crop.getFieldId();
			Field field = fieldService.getById(fieldId);
			if (field == null) {
				continue;
			}

			Long farmerId = field.getFarmerId();
			if (farmerId == null || seenFarmerIds.contains(farmerId)) {
				continue;
			}

			Farmer farmer = farmerService.getById(farmerId);
			if (farmer != null) {
				seenFarmerIds.add(farmerId);
				result.add(farmer);
			}
		}

		return result;
	}

	private void copyInto(Distributor target, Distributor source) {
		target.setName(source.getName());
		target.setContact(source.getContact());
		target.setInterestedCropType(source.getInterestedCropType());
	}
}

