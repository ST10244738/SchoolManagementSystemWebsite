package com.tirisano.mmogo.school.manager.controller;

import com.tirisano.mmogo.school.manager.dto.ApiResponse;
import com.tirisano.mmogo.school.manager.model.DocumentRequest;
import com.tirisano.mmogo.school.manager.model.Parent;
import com.tirisano.mmogo.school.manager.model.Student;
import com.tirisano.mmogo.school.manager.service.FirebaseService;
import com.tirisano.mmogo.school.manager.service.ParentService;
import com.tirisano.mmogo.school.manager.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/parents")
@RequiredArgsConstructor
public class ParentController {

    private final ParentService parentService;
    private final StudentService studentService;
    private final FirebaseService firebaseService;

    // ==================== CRUD ENDPOINTS ====================

    // CREATE - Create parent
    @PostMapping
    public ResponseEntity<ApiResponse<Parent>> createParent(@Valid @RequestBody Parent parent) {
        try {
            Parent createdParent = parentService.createParent(parent);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(createdParent, "Parent created successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // READ - Get all parents (Admin only)
    @GetMapping
    public ResponseEntity<ApiResponse<List<Parent>>> getAllParents() {
        try {
            List<Parent> parents = parentService.getAllParents();
            return ResponseEntity.ok(ApiResponse.success(parents));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // READ - Get parent by ID
    @GetMapping("/{parentId}")
    public ResponseEntity<ApiResponse<Parent>> getParent(@PathVariable String parentId) {
        try {
            Parent parent = parentService.findById(parentId);
            if (parent == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Parent not found"));
            }
            return ResponseEntity.ok(ApiResponse.success(parent));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // UPDATE - Update parent
    @PutMapping("/{parentId}")
    public ResponseEntity<ApiResponse<Parent>> updateParent(
            @PathVariable String parentId,
            @Valid @RequestBody Parent parent) {
        try {
            Parent updatedParent = parentService.updateParent(parentId, parent);
            return ResponseEntity.ok(ApiResponse.success(updatedParent, "Parent updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // DELETE - Delete parent
    @DeleteMapping("/{parentId}")
    public ResponseEntity<ApiResponse<Void>> deleteParent(@PathVariable String parentId) {
        try {
            parentService.deleteParent(parentId);
            return ResponseEntity.ok(ApiResponse.success(null, "Parent deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ==================== CHILD (STUDENT) MANAGEMENT ====================

    // CREATE - Add child to parent
    @PostMapping("/{parentId}/children")
    public ResponseEntity<ApiResponse<Student>> addChild(
            @PathVariable String parentId,
            @Valid @RequestBody Student student) {
        try {
            student.setParentId(parentId);
            Student savedStudent = studentService.addStudent(student);
            return ResponseEntity.ok(ApiResponse.success(savedStudent, "Child added successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // READ - Get children of parent
    @GetMapping("/{parentId}/children")
    public ResponseEntity<ApiResponse<List<Student>>> getChildren(@PathVariable String parentId) {
        try {
            List<Student> children = studentService.findByParentId(parentId);
            return ResponseEntity.ok(ApiResponse.success(children));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // UPDATE - Update child data (parent can update their own child's data)
    @PutMapping("/{parentId}/children/{studentId}")
    public ResponseEntity<ApiResponse<Student>> updateChild(
            @PathVariable String parentId,
            @PathVariable String studentId,
            @Valid @RequestBody Student student) {
        try {
            // Verify the student belongs to this parent
            Student existingStudent = studentService.getStudentById(studentId);
            if (existingStudent == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Student not found"));
            }
            if (!existingStudent.getParentId().equals(parentId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("You can only update your own children's data"));
            }

            // Ensure parentId is not changed
            student.setParentId(parentId);
            Student updatedStudent = studentService.updateStudent(studentId, student);
            return ResponseEntity.ok(ApiResponse.success(updatedStudent, "Child data updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ==================== DOCUMENT REQUEST ====================

    // CREATE - Submit document request
    @PostMapping("/{parentId}/document-requests")
    public ResponseEntity<ApiResponse<DocumentRequest>> requestDocument(
            @PathVariable String parentId,
            @RequestBody DocumentRequest request) {
        try {
            request.setParentId(parentId);
            firebaseService.save("documentRequests", request).join();
            // FirebaseService automatically sets requestId on the request object
            return ResponseEntity.ok(ApiResponse.success(request, "Document request submitted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}