package com.agrichain.agriapp.service;

import com.agrichain.agriapp.model.Crop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CropService {

    private static final Logger logger = LoggerFactory.getLogger(CropService.class);

    private final List<Crop> crops = new ArrayList<>();
    private long nextId = 1L;

    public Crop create(Crop crop) {
        if (crop == null) {
            return null;
        }

        Crop created = new Crop();
        created.setId(nextId++);
        copyInto(created, crop);

        crops.add(created);
        logger.info("Crop created successfully with id={}", created.getId());
        return created;
    }

    public List<Crop> getAll() {
        return new ArrayList<>(crops);
    }

    public Crop getById(Long id) {
        if (id == null) {
            return null;
        }

        for (Crop crop : crops) {
            if (id.equals(crop.getId())) {
                return crop;
            }
        }
        return null;
    }

    public Crop update(Long id, Crop crop) {
        if (id == null || crop == null) {
            return null;
        }

        Crop existing = getById(id);
        if (existing == null) {
            return null;
        }

        copyInto(existing, crop);
        logger.info("Crop updated successfully with id={}", id);
        return existing;
    }

    public Crop getByFieldId(Long fieldId) {
        if (fieldId == null) {
            return null;
        }

        for (Crop crop : crops) {
            if (fieldId.equals(crop.getFieldId())) {
                return crop;
            }
        }
        return null;
    }

    public boolean delete(Long id) {
        if (id == null) {
            return false;
        }

        logger.info("Deleting Crop id={}", id);
        boolean removed = crops.removeIf(crop -> id.equals(crop.getId()));
        if (removed) {
            logger.info("Crop deleted successfully with id={}", id);
        }
        return removed;
    }

    public boolean deleteByFieldId(Long fieldId) {
        if (fieldId == null) {
            return false;
        }

        logger.info("Deleting Crop(s) for fieldId={}", fieldId);
        boolean removed = crops.removeIf(crop -> fieldId.equals(crop.getFieldId()));
        if (removed) {
            logger.info("Crop deletion by fieldId completed for fieldId={}", fieldId);
        }
        return removed;
    }

    private void copyInto(Crop target, Crop source) {
        target.setName(source.getName());
        target.setType(source.getType());
        target.setFieldId(source.getFieldId());
    }
}