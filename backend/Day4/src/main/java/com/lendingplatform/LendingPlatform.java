package com.lendingplatform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.lendingplatform.entities.Role;
import com.lendingplatform.entities.User;
import com.lendingplatform.repo.UserRepository;

@SpringBootApplication
public class LendingPlatform implements CommandLineRunner{
    @Autowired
    private UserRepository userRepository;
	public static void main(String[] args) {
		SpringApplication.run(LendingPlatform .class, args);
	}
	@Override
	public void run(String... args) throws Exception {
		User adminAccount=userRepository.findByRole(Role.ADMIN);
		if(null==adminAccount) {
			User user=new User();
			user.setEmail("admin@gmail.com");
			user.setName("admin");
			user.setRole(Role.ADMIN);
			user.setPassword(new BCryptPasswordEncoder().encode("admin"));
			userRepository.save(user);
		}
		
	}

}
