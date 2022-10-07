package io.github.msj.mspessoa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class MsPessoaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsPessoaApplication.class, args);
	}

}
