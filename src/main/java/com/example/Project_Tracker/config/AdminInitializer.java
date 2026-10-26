package com.example.Project_Tracker.config;



import com.example.Project_Tracker.entity.User;
import com.example.Project_Tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;



@Configuration
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {



private final UserRepository userRepository;
private final PasswordEncoder passwordEncoder;



@Override
public void run(String... args) {
  String adminEmail = "admin@projecttracker.com";



  if (userRepository.findByEmail(adminEmail).isEmpty()) {
  User admin = User.builder()
  .name("System Admin")
  .email(adminEmail)
  .passwordHash(passwordEncoder.encode("admin123")) // default password
  .role("ADMIN")
  .build();



  userRepository.save(admin);
  System.out.println("✅ Admin user created successfully!");
  } else {
  System.out.println("ℹ️ Admin user already exists.");
  }
}
}
 