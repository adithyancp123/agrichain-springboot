
package com.agrichain.agriapp.service;

import com.agrichain.agriapp.model.Field;
import com.agrichain.agriapp.model.Crop;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.agrichain.agriapp.model.IrrigationSchedule;

import java.util.ArrayList;
import java.util.List;

@Service
public class FieldService {

	private static final Logger logger = LoggerFactory.getLogger(FieldService.class);

	private final List<Field> fields = new ArrayList<>();
	private long nextId = 1L;

	private final FarmerService farmerService;
	private final CropService cropService;
	private final IrrigationScheduleService irrigationScheduleService;

	@Autowired
	public FieldService(FarmerService farmerService,
			CropService cropService,
			IrrigationScheduleService irrigationScheduleService) {
		this.farmerService = farmerService;
		this.cropService = cropService;
		this.irrigationScheduleService = irrigationScheduleService;
	}

	public Field create(Field field) {
		if (field == null) {
			return null;
		}

		validateFarmerIdOrThrow(field.getFarmerId());

		logger.info("Creating Field: name='{}', area={}, farmerId={}",
				field.getName(), field.getArea(), field.getFarmerId());

		Field created = new Field();
		created.setId(nextId++);
		copyInto(created, field);

		fields.add(created);
		logger.info("Field created successfully with id={}", created.getId());
		return created;
	}

	public List<Field> getAll() {
		return new ArrayList<>(fields);
	}

	public Field getById(Long id) {
		if (id == null) {
			return null;
		}

		for (Field field : fields) {
			if (id.equals(field.getId())) {
				return field;
			}
		}
		return null;
	}

	public Field update(Long id, Field field) {
		if (id == null || field == null) {
			return null;
		}

		Field existing = getById(id);
		if (existing == null) {
			return null;
		}

		validateFarmerIdOrThrow(field.getFarmerId());

		logger.info("Updating Field id={} -> name='{}', area={}, farmerId={}",
				id, field.getName(), field.getArea(), field.getFarmerId());

		copyInto(existing, field);
		logger.info("Field updated successfully with id={}", id);
		return existing;
	}

	public List<Field> getByFarmerId(Long farmerId) {
		if (farmerId == null) {
			return new ArrayList<>();
		}

		List<Field> result = new ArrayList<>();
		for (Field field : fields) {
			if (farmerId.equals(field.getFarmerId())) {
				result.add(field);
			}
		}
		return result;
	}

	public Crop getCropByFieldId(Long fieldId) {
		return cropService.getByFieldId(fieldId);
	}

	public IrrigationSchedule getIrrigationScheduleByFieldId(Long fieldId) {
		return irrigationScheduleService.getByFieldId(fieldId);
	}

	public boolean delete(Long id) {
		if (id == null) {
			return false;
		}

		logger.info("Deleting Field id={} (cascading to crop and irrigation schedule)", id);

		// Cascade delete: Field -> (Crop, IrrigationSchedule)
		boolean deletedCrop = cropService.deleteByFieldId(id);
		boolean deletedSchedule = irrigationScheduleService.deleteByFieldId(id);
		logger.info("Field id={} cascade results: cropDeleted={}, scheduleDeleted={}", id, deletedCrop, deletedSchedule);

		boolean removed = fields.removeIf(field -> id.equals(field.getId()));
		if (removed) {
			logger.info("Field deleted successfully with id={}", id);
		}
		return removed;
	}

	private void validateFarmerIdOrThrow(Long farmerId) {
		if (farmerId == null || farmerService.getById(farmerId) == null) {
			throw new RuntimeException("Farmer not found");
		}
	}

	private void copyInto(Field target, Field source) {
		target.setName(source.getName());
		target.setArea(source.getArea());
		target.setFarmerId(source.getFarmerId());
	}
}

