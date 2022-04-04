package com.example.CourseRegistration;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<Users, Integer> {

    // List<Users> findByFirstname(String name);
    Users findByEmailAndPassword(String email, String password);

    Users findByEmail(String email);

    Users findByPhone(String phone);

}
