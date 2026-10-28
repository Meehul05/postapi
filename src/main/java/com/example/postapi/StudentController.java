package com.example.postapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentRepository studentRepository;

    // Simple in-memory cache
    private final Map<Integer, Student> cache = new HashMap<>();

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // Pagination + Sorting
    @GetMapping
    public Page<Student> getStudents(
            @PageableDefault(size = 5) Pageable pageable) {

        return studentRepository.findAll(pageable);
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentRepository.save(student);
    }

    // Get single student with caching
    @GetMapping("/{id}")
    public Student getStudent(@PathVariable Integer id) {

        // Check cache first
        if (cache.containsKey(id)) {
            System.out.println("CACHE HIT");
            return cache.get(id);
        }

        // Student not in cache
        System.out.println("CACHE MISS");

        Student student = studentRepository.findById(id)
                .orElse(null);

        // Store student in cache
        if (student != null) {
            cache.put(id, student);
        }

        return student;
    }

    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Integer id) {

        if (!studentRepository.existsById(id)) {
            return "Student not found";
        }

        studentRepository.deleteById(id);
        cache.remove(id);

        return "Student deleted successfully";
    }
}