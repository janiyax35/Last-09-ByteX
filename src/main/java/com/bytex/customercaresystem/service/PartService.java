package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Part;
import java.util.List;
import java.util.Optional;

public interface PartService {

    /**
     * Finds all parts in the inventory.
     * @return A list of all parts.
     */
    List<Part> findAll();

    /**
     * Finds a part by its ID.
     * @param id The ID of the part.
     * @return An Optional containing the part if found.
     */
    Optional<Part> findById(Long id);

    /**
     * Saves a part (new or updated).
     * @param part The part to save.
     * @return The saved part.
     */
    Part save(Part part);

    /**
     * Finds all parts that are low on stock or out of stock.
     * @return A list of parts with low stock.
     */
    List<Part> findLowStockParts();

    /**
     * Discontinues a part by changing its status.
     * @param id The ID of the part to discontinue.
     */
    void discontinuePart(Long id);
}
