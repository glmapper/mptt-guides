package com.glmapper;

import com.glmapper.repo.TestTreeNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.glmapper.entities")
@EnableJpaRepositories(basePackages = { "com.glmapper.repo" })
public class MpttGuidesApplication {

	@Autowired
	private TestTreeNodeRepository testTreeNodeRepository;

	public static void main(String[] args) {
		SpringApplication.run(MpttGuidesApplication.class, args);
	}

}
