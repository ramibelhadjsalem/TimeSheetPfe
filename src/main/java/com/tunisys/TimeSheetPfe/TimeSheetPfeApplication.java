package com.tunisys.TimeSheetPfe;

import com.tunisys.TimeSheetPfe.models.ERole;
import com.tunisys.TimeSheetPfe.models.Role;
import com.tunisys.TimeSheetPfe.models.UserModel;
import com.tunisys.TimeSheetPfe.repositories.RoleRepository;
import com.tunisys.TimeSheetPfe.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
// @EnableScheduling
@SpringBootApplication
public class TimeSheetPfeApplication {

	@Autowired
	private PasswordEncoder encoder;

	public static void main(String[] args) {
		SpringApplication.run(TimeSheetPfeApplication.class, args);
	}

	@Bean
	CommandLineRunner run(RoleRepository roleRepository , UserRepository userRepository){
		return args -> {

			if (roleRepository.count()<1) {

				roleRepository.save(new Role( null, ERole.ROLE_ADMIN));
				roleRepository.save(new Role(null, ERole.ROLE_MANAGER));
				roleRepository.save(new Role(null,ERole.ROLE_EMPLOYEE));
			}
			if(!userRepository.existsByEmail("admin@gmail.com")){
				UserModel user = new UserModel(
						"admin@gmail.com",
						encoder.encode("password")
				);

				Role adminRole = roleRepository.findRoleByName(ERole.ROLE_ADMIN)
						.orElseThrow(() -> new RuntimeException("Error: Role is not found."));

				user.getRoles().add(adminRole);
				userRepository.save(user);
			}
		};
	}

	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}

}
