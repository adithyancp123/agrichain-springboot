package com.agrichain.agriapp.service;

import com.agrichain.agriapp.model.IrrigationSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IrrigationScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(IrrigationScheduleService.class);

    private final List<IrrigationSchedule> schedules = new ArrayList<>();
    private long nextId = 1L;

    public IrrigationSchedule create(IrrigationSchedule schedule) {
        if (schedule == null) {
            return null;
        }

        // Field has one IrrigationSchedule: enforce uniqueness by fieldId
        if (getByFieldId(schedule.getFieldId()) != null) {
            return null;
        }

        IrrigationSchedule created = new IrrigationSchedule();
        created.setId(nextId++);
        copyInto(created, schedule);

        schedules.add(created);
        logger.info("IrrigationSchedule created successfully with id={}", created.getId());
        return created;
    }

    public List<IrrigationSchedule> getAll() {
        return new ArrayList<>(schedules);
    }

    public IrrigationSchedule getById(Long id) {
        if (id == null) {
            return null;
        }

        for (IrrigationSchedule schedule : schedules) {
            if (id.equals(schedule.getId())) {
                return schedule;
            }
        }
        return null;
    }

    public IrrigationSchedule update(Long id, IrrigationSchedule schedule) {
        if (id == null || schedule == null) {
            return null;
        }

        IrrigationSchedule existing = getById(id);
        if (existing == null) {
            return null;
        }

        // If changing fieldId, enforce one schedule per field
        IrrigationSchedule otherForField = getByFieldId(schedule.getFieldId());
        if (otherForField != null && !otherForField.getId().equals(id)) {
            return null;
        }

        copyInto(existing, schedule);
        logger.info("IrrigationSchedule updated successfully with id={}", id);
        return existing;
    }

    public IrrigationSchedule getByFieldId(Long fieldId) {
        if (fieldId == null) {
            return null;
        }

        for (IrrigationSchedule schedule : schedules) {
            if (fieldId.equals(schedule.getFieldId())) {
                return schedule;
            }
        }
        return null;
    }

    public boolean delete(Long id) {
        if (id == null) {
            return false;
        }

        logger.info("Deleting IrrigationSchedule id={}", id);
        boolean removed = schedules.removeIf(schedule -> id.equals(schedule.getId()));
        if (removed) {
            logger.info("IrrigationSchedule deleted successfully with id={}", id);
        }
        return removed;
    }

    public boolean deleteByFieldId(Long fieldId) {
        if (fieldId == null) {
            return false;
        }

        logger.info("Deleting IrrigationSchedule(s) for fieldId={}", fieldId);
        boolean removed = schedules.removeIf(schedule -> fieldId.equals(schedule.getFieldId()));
        if (removed) {
            logger.info("IrrigationSchedule deletion by fieldId completed for fieldId={}", fieldId);
        }
        return removed;
    }

    private void copyInto(IrrigationSchedule target, IrrigationSchedule source) {
        target.setDate(source.getDate());
        target.setMethod(source.getMethod());
        target.setFieldId(source.getFieldId());
    }
}