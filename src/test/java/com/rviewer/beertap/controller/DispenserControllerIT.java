package com.rviewer.beertap.controller;

import com.rviewer.beertap.BeertapApplication;
import com.rviewer.beertap.BeertapApplicationTests;
import com.rviewer.beertap.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = BeertapApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Testcontainers(disabledWithoutDocker = true)
public class DispenserControllerIT {

    @Container
    private static final MySQLContainer mySQLContainer = BeertapApplicationTests.getMysqlContainer();

    @DynamicPropertySource
    private static void setupProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @LocalServerPort
    private int port = 8080;

    private final String endpointUrl = "http://localhost:"+port+"/dispenser";
    TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void testSimpleLifeCycle() {

        final Float givenFlowVolume = 7.7f;
        final Float givenElapsedSeconds = 25.09f;
        final Date givenEndDate = new Date();
        final Date givenBeginDate = this.datePlusSeconds(givenEndDate, -givenElapsedSeconds);

        final Float expectedMoneySpent = BeertapApplicationTests.beerPrice * givenElapsedSeconds * givenFlowVolume;
        final Float expectedMargin = 0.01f;

        final CreateDispenserRequest createDispenserRequest= new CreateDispenserRequest();
        createDispenserRequest.setFlowVolume(givenFlowVolume);
        final HttpEntity<CreateDispenserRequest> createDispenserEntity = new HttpEntity<>(createDispenserRequest);

        final ResponseEntity<CreateDispenserResponse> createdResponse = restTemplate.exchange(
                this.endpointUrl,
                HttpMethod.POST,
                createDispenserEntity,
                CreateDispenserResponse.class);

        assertEquals(HttpStatus.CREATED, createdResponse.getStatusCode());
        assertEquals(givenFlowVolume, createdResponse.getBody().getFlowVolume());
        final String dispenserId = createdResponse.getBody().getDispenserId();
        final String dispenserUrl = endpointUrl + "/" + dispenserId;

        final ChangeDispenserStatusRequest openDispenserRequest = new ChangeDispenserStatusRequest();
        openDispenserRequest.setRequestedStatus(BeerDispenserStatus.open);
        openDispenserRequest.setRequestedChangeTime(givenBeginDate);
        final HttpEntity<ChangeDispenserStatusRequest> openDispenserEntity = new HttpEntity<>(openDispenserRequest);

        final ResponseEntity<Void> openResponse = restTemplate.exchange(
                dispenserUrl,
                HttpMethod.PUT,
                openDispenserEntity,
                Void.class);

        assertEquals(HttpStatus.ACCEPTED, openResponse.getStatusCode());

        final ChangeDispenserStatusRequest closeDispenserRequest = new ChangeDispenserStatusRequest();
        closeDispenserRequest.setRequestedStatus(BeerDispenserStatus.closed);
        closeDispenserRequest.setRequestedChangeTime(givenEndDate);
        final HttpEntity<ChangeDispenserStatusRequest> closeDispenserEntity = new HttpEntity<>(closeDispenserRequest);

        final ResponseEntity<Void> closeResponse = restTemplate.exchange(
                dispenserUrl,
                HttpMethod.PUT,
                closeDispenserEntity,
                Void.class);

        assertEquals(HttpStatus.ACCEPTED, closeResponse.getStatusCode());

        final ResponseEntity<MoneySpentResponse> moneySpentResponse = restTemplate.exchange(
                dispenserUrl,
                HttpMethod.GET,
                null,
                MoneySpentResponse.class
        );

        assertEquals(HttpStatus.OK, moneySpentResponse.getStatusCode());
        final Float actualMargin = Math.abs(moneySpentResponse.getBody().getTotalMoneySpent() - expectedMoneySpent);
        assertTrue(actualMargin < expectedMargin);
    }

    private Date datePlusSeconds(Date date, Float seconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MILLISECOND, (int) Math.round(Math.floor(seconds * 1000f)));
        return cal.getTime();
    }

}