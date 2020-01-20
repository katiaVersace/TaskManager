package com.alten.springboot.taskmanager;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.modelmapper.AbstractConverter;
import org.modelmapper.AbstractProvider;
import org.modelmapper.Conditions;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.Provider;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import com.alten.springboot.taskmanager.dto.TaskDto;
import com.alten.springboot.taskmanager.model.Task;

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


@SpringBootApplication
@EnableSwagger2
public class TaskmanagerSpringBootApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(TaskmanagerSpringBootApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		TypeMap<Task, TaskDto> typeMapToDto = modelMapper.createTypeMap(Task.class, TaskDto.class);

		typeMapToDto.addMappings(mapper -> {
			mapper.map(src -> src.getEmployee().getId(), TaskDto::setEmployeeId);

		});
		
	    Provider<LocalDate> localDateProvider = new AbstractProvider<LocalDate>() {
	        @Override
	        public LocalDate get() {
	        	LocalDate now= LocalDate.now();
	        	return now;
	        }
	    };

	    Converter<String, LocalDate> toDate = new AbstractConverter<String, LocalDate>() {
	        @Override
	        protected LocalDate convert(String source) {
	        	
	            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	            LocalDate localDate = LocalDate.parse(source, format);
	            return localDate;
	        }
	    };


	    modelMapper.createTypeMap(String.class, LocalDate.class);
	   
	    modelMapper.addConverter(toDate);
	  
	    modelMapper.getTypeMap(String.class, LocalDate.class).setProvider(localDateProvider);
	    
	    modelMapper.addMappings(new PropertyMap<TaskDto, Task>() {
	        @Override
	        protected void configure() {
	            when(Conditions.isNull()).skip().setRealStartTime(null);
	            when(Conditions.isNull()).skip().setRealEndTime(null);
	           
	        }
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
	
	@Bean(initMethod="start",destroyMethod="stop")
	public org.h2.tools.Server h2WebConsoleServer () throws SQLException {
	    return org.h2.tools.Server.createWebServer("-web","-webAllowOthers","-webDaemon","-webPort", "8082");
	}
	

}
