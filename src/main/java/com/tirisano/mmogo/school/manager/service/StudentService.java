package com.tirisano.mmogo.school.manager.service;

import com.tirisano.mmogo.school.manager.enums.StudentStatus;
import com.tirisano.mmogo.school.manager.model.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final FirebaseService firebaseService;

    // CREATE
    public Student addStudent(Student student) {
        // Check if birth certificate ID already exists
        List<Student> existingStudents = firebaseService.findByField(
                "students",
                "birthCertificateId",
                student.getBirthCertificateId(),
                Student.class
        ).join();

        if (!existingStudents.isEmpty()) {
            throw new RuntimeException("A student with this birth certificate ID already exists");
        }

        student.setStatus(StudentStatus.PENDING);
        firebaseService.save("students", student).join();
        // FirebaseService automatically sets studentId on the student object
        return student;
    }

    // READ - Get all students (for admin)
    public List<Student> getAllStudents() {
        return firebaseService.findAll("students", Student.class).join();
    }

    // READ - Get student by ID
    public Student getStudentById(String studentId) {
        return firebaseService.findById("students", studentId, Student.class).join();
    }

    // READ - Get students by parent ID (for parents)
    public List<Student> findByParentId(String parentId) {
        return firebaseService.findByField("students", "parentId", parentId, Student.class).join();
    }

    // READ - Get pending students (for admin)
    public List<Student> findPendingStudents() {
        return firebaseService.findByField("students", "status", StudentStatus.PENDING, Student.class).join();
    }

    // READ - Get approved students
    public List<Student> findApprovedStudents() {
        return firebaseService.findByField("students", "status", StudentStatus.APPROVED, Student.class).join();
    }

    // READ - Get rejected students
    public List<Student> findRejectedStudents() {
        return firebaseService.findByField("students", "status", StudentStatus.REJECTED, Student.class).join();
    }

    // UPDATE - General update
    public Student updateStudent(String studentId, Student updatedStudent) {
        Student existingStudent = firebaseService.findById("students", studentId, Student.class).join();
        if (existingStudent != null) {
            // Check if birth certificate ID is being changed to a duplicate
            if (!existingStudent.getBirthCertificateId().equals(updatedStudent.getBirthCertificateId())) {
                List<Student> duplicates = firebaseService.findByField(
                        "students",
                        "birthCertificateId",
                        updatedStudent.getBirthCertificateId(),
                        Student.class
                ).join();

                if (!duplicates.isEmpty()) {
                    throw new RuntimeException("A student with this birth certificate ID already exists");
                }
            }

            updatedStudent.setStudentId(studentId);
            // Preserve important fields that shouldn't be overridden
            updatedStudent.setCreatedAt(existingStudent.getCreatedAt());
            firebaseService.save("students", updatedStudent, studentId).join();
            return updatedStudent;
        }
        throw new RuntimeException("Student not found with ID: " + studentId);
    }

    // UPDATE - Approve student
    public Student approveStudent(String studentId) {
        Student student = firebaseService.findById("students", studentId, Student.class).join();
        if (student != null) {
            student.setStatus(StudentStatus.APPROVED);
            student.setRejectionReason(null); // Clear rejection reason if previously rejected
            firebaseService.save("students", student, studentId);
            return student;
        }
        throw new RuntimeException("Student not found with ID: " + studentId);
    }

    // UPDATE - Approve student with class and teacher assignment
    public Student approveStudentWithClass(String studentId, String className, String teacher) {
        Student student = firebaseService.findById("students", studentId, Student.class).join();
        if (student != null) {
            student.setStatus(StudentStatus.APPROVED);
            student.setRejectionReason(null); // Clear rejection reason if previously rejected
            student.setClassName(className);
            student.setTeacher(teacher);
            firebaseService.save("students", student, studentId);
            return student;
        }
        throw new RuntimeException("Student not found with ID: " + studentId);
    }

    // UPDATE - Reject student
    public Student rejectStudent(String studentId, String reason) {
        Student student = firebaseService.findById("students", studentId, Student.class).join();
        if (student != null) {
            student.setStatus(StudentStatus.REJECTED);
            student.setRejectionReason(reason);
            firebaseService.save("students", student, studentId);
            return student;
        }
        throw new RuntimeException("Student not found with ID: " + studentId);
    }

    // DELETE - Delete student
    public void deleteStudent(String studentId) {
        Student student = firebaseService.findById("students", studentId, Student.class).join();
        if (student != null) {
            firebaseService.delete("students", studentId);
        } else {
            throw new RuntimeException("Student not found with ID: " + studentId);
        }
    }
}