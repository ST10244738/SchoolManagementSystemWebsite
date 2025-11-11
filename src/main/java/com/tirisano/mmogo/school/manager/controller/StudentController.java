package com.tirisano.mmogo.school.manager.controller;

import com.tirisano.mmogo.school.manager.dto.ApiResponse;
import com.tirisano.mmogo.school.manager.model.Student;
import com.tirisano.mmogo.school.manager.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    // ==================== ADMIN ENDPOINTS ====================

    // GET all students (Admin)
    @GetMapping
    public ResponseEntity<ApiResponse<List<Student>>> getAllStudents() {
        try {
            List<Student> students = studentService.getAllStudents();
            return ResponseEntity.ok(ApiResponse.success(students));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET pending students (Admin)
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<Student>>> getPendingStudents() {
        try {
            List<Student> students = studentService.findPendingStudents();
            return ResponseEntity.ok(ApiResponse.success(students));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET approved students (Admin)
    @GetMapping("/approved")
    public ResponseEntity<ApiResponse<List<Student>>> getApprovedStudents() {
        try {
            List<Student> students = studentService.findApprovedStudents();
            return ResponseEntity.ok(ApiResponse.success(students));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET rejected students (Admin)
    @GetMapping("/rejected")
    public ResponseEntity<ApiResponse<List<Student>>> getRejectedStudents() {
        try {
            List<Student> students = studentService.findRejectedStudents();
            return ResponseEntity.ok(ApiResponse.success(students));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // APPROVE student (Admin)
    @PutMapping("/{studentId}/approve")
    public ResponseEntity<ApiResponse<Student>> approveStudent(@PathVariable String studentId) {
        try {
            Student student = studentService.approveStudent(studentId);
            return ResponseEntity.ok(ApiResponse.success(student, "Student approved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // APPROVE student with class and teacher assignment (Admin)
    @PutMapping("/{studentId}/approve-with-class")
    public ResponseEntity<ApiResponse<Student>> approveStudentWithClass(
            @PathVariable String studentId,
            @RequestBody Map<String, String> body) {
        try {
            String className = body.get("className");
            String teacher = body.get("teacher");

            if (className == null || className.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Class name is required"));
            }
            if (teacher == null || teacher.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Teacher name is required"));
            }

            Student student = studentService.approveStudentWithClass(studentId, className, teacher);
            return ResponseEntity.ok(ApiResponse.success(student, "Student approved and assigned to class successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // REJECT student (Admin)
    @PutMapping("/{studentId}/reject")
    public ResponseEntity<ApiResponse<Student>> rejectStudent(
            @PathVariable String studentId,
            @RequestBody Map<String, String> body) {
        try {
            String reason = body.get("reason");
            if (reason == null || reason.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Rejection reason is required"));
            }
            Student student = studentService.rejectStudent(studentId, reason);
            return ResponseEntity.ok(ApiResponse.success(student, "Student rejected successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ==================== PARENT ENDPOINTS ====================

    // GET students by parent ID (Parent)
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<ApiResponse<List<Student>>> getStudentsByParentId(@PathVariable String parentId) {
        try {
            List<Student> students = studentService.findByParentId(parentId);
            return ResponseEntity.ok(ApiResponse.success(students));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ==================== CRUD ENDPOINTS ====================

    // CREATE student
    @PostMapping
    public ResponseEntity<ApiResponse<Student>> createStudent(@RequestBody Student student) {
        try {
            Student createdStudent = studentService.addStudent(student);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(createdStudent, "Student created successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // READ single student by ID
    @GetMapping("/{studentId}")
    public ResponseEntity<ApiResponse<Student>> getStudentById(@PathVariable String studentId) {
        try {
            Student student = studentService.getStudentById(studentId);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Student not found"));
            }
            return ResponseEntity.ok(ApiResponse.success(student));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // UPDATE student
    @PutMapping("/{studentId}")
    public ResponseEntity<ApiResponse<Student>> updateStudent(
            @PathVariable String studentId,
            @RequestBody Student student) {
        try {
            Student updatedStudent = studentService.updateStudent(studentId, student);
            return ResponseEntity.ok(ApiResponse.success(updatedStudent, "Student updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // DELETE student
    @DeleteMapping("/{studentId}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable String studentId) {
        try {
            studentService.deleteStudent(studentId);
            return ResponseEntity.ok(ApiResponse.success(null, "Student deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}