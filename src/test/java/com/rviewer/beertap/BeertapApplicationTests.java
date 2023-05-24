package com.rviewer.beertap;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.Properties;


@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
public
class BeertapApplicationTests {


	public static final Float beerPrice;

	private static final Boolean isRunningOnAppleSilicon;

	static {
		try {
			Resource resource = new ClassPathResource("/application.properties");
			Properties props = PropertiesLoaderUtils.loadProperties(resource);
			isRunningOnAppleSilicon = props.get("com.rviewer.beertap.isRunningOnAppleSilicon").equals("true");
			beerPrice = (Float.parseFloat((String) props.get("com.rviewer.beertap.beerPrice")));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static MySQLContainer getMysqlContainer() {
		return (
				isRunningOnAppleSilicon ?
						new MySQLContainer<>(DockerImageName.parse("arm64v8/mysql:8-oracle").asCompatibleSubstituteFor("mysql"))
						:
						new MySQLContainer<>("mysql:8.0.30")
		).withDatabaseName("rviewer")
				.withUsername("rviewer")
				.withPassword("rviewer");
	}
	@Container
	private static final MySQLContainer mySQLContainer = getMysqlContainer();

	@DynamicPropertySource
	private static void setupProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
		registry.add("spring.datasource.username", mySQLContainer::getUsername);
		registry.add("spring.datasource.password", mySQLContainer::getPassword);
	}



	@Test
	void contextLoads() {
	}


	@Test
	void testMySQLContainerIsRunning() {
		Assert.assertTrue(mySQLContainer.isRunning());
	}

	@Test
	void contextVariableLoads() throws IOException {
		Resource resource = new ClassPathResource("/application.properties");
		Properties props = PropertiesLoaderUtils.loadProperties(resource);
		Assert.assertNotNull(props.get("com.rviewer.beertap.beerPrice"));
	}
}
