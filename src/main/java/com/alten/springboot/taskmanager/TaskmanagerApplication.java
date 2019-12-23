package com.alten.springboot.taskmanager;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import com.alten.springboot.taskmanager.dto.TaskDto;
import com.alten.springboot.taskmanager.entity.Task;

import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

//@ComponentScan
//@EnableAutoConfiguration
@SpringBootApplication
@EnableSwagger2
//@EnableWebMvc
public class TaskmanagerApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(TaskmanagerApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		TypeMap<Task, TaskDto> typeMap = modelMapper.createTypeMap(Task.class, TaskDto.class);

		typeMap.addMappings(mapper -> {
			mapper.map(src -> src.getEmployee().getId(), TaskDto::setEmployeeId);

		});


		return modelMapper;
	}

	@Bean
	public ApiListingResource apiListingResource() {
		return new ApiListingResource();
	}

	@Bean
	public SwaggerSerializers swaggerSerializers() {
		return new SwaggerSerializers();
	}

	// add for swagger
	@Bean
	public Docket newsApi() {

		return new Docket(DocumentationType.SWAGGER_2).groupName("tasks").apiInfo(apiInfo()).select()
				.apis(RequestHandlerSelectors.any()).paths(PathSelectors.any()).build();
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Task Manager API with Swagger").description("Task Manager API with Swagger")
				.contact(new Contact("Katia Versace", "", "caterina.versace@alten.it"))
				.license("Apache License Version 2.0").licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
				.version("2.0").build();
	}
	// end

}
