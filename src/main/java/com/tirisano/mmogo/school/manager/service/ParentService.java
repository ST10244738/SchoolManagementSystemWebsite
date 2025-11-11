package com.tirisano.mmogo.school.manager.service;

import com.tirisano.mmogo.school.manager.model.Parent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParentService {

    private final FirebaseService firebaseService;

    // CREATE
    public Parent createParent(Parent parent) {
        try {
            firebaseService.save("parents", parent).join();
            log.info("Parent created successfully with ID: {}", parent.getParentId());
            return parent;
        } catch (Exception e) {
            log.error("Error creating parent", e);
            throw new RuntimeException("Failed to create parent: " + e.getMessage());
        }
    }

    // READ - Get all parents
    public List<Parent> getAllParents() {
        try {
            return firebaseService.findAll("parents", Parent.class).join();
        } catch (Exception e) {
            log.error("Error fetching all parents", e);
            throw new RuntimeException("Failed to fetch parents: " + e.getMessage());
        }
    }

    // READ - Get parent by ID
    public Parent findById(String parentId) {
        try {
            return firebaseService.findById("parents", parentId, Parent.class).join();
        } catch (Exception e) {
            log.error("Error finding parent by ID: {}", parentId, e);
            throw new RuntimeException("Failed to find parent: " + e.getMessage());
        }
    }

    // READ - Get parent by UID
    public Parent findByUid(String uid) {
        try {
            List<Parent> parents = firebaseService.findByField("parents", "uid", uid, Parent.class).join();
            return parents.isEmpty() ? null : parents.get(0);
        } catch (Exception e) {
            log.error("Error finding parent by UID: {}", uid, e);
            throw new RuntimeException("Failed to find parent: " + e.getMessage());
        }
    }

    // UPDATE
    public Parent updateParent(String parentId, Parent parent) {
        try {
            Parent existingParent = findById(parentId);
            if (existingParent == null) {
                throw new RuntimeException("Parent not found with ID: " + parentId);
            }

            parent.setParentId(parentId);
            // Preserve original creation timestamp
            if (parent.getCreatedAt() == null) {
                parent.setCreatedAt(existingParent.getCreatedAt());
            }

            firebaseService.save("parents", parent, parentId).join();
            log.info("Parent updated successfully: {}", parentId);
            return parent;
        } catch (Exception e) {
            log.error("Error updating parent: {}", parentId, e);
            throw new RuntimeException("Failed to update parent: " + e.getMessage());
        }
    }

    // DELETE
    public void deleteParent(String parentId) {
        try {
            Parent parent = findById(parentId);
            if (parent == null) {
                throw new RuntimeException("Parent not found with ID: " + parentId);
            }

            firebaseService.delete("parents", parentId).join();
            log.info("Parent deleted successfully: {}", parentId);
        } catch (Exception e) {
            log.error("Error deleting parent: {}", parentId, e);
            throw new RuntimeException("Failed to delete parent: " + e.getMessage());
        }
    }
}