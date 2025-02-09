package com.amigoscode.journey;

import com.amigoscode.customer.Customer;
import com.amigoscode.customer.CustomerRigistrationRequest;
import com.amigoscode.customer.CustomerUpdateRequest;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIT {

    @Autowired
    private WebTestClient client;

    private static final Random RAND = new Random();

    private static final String URI = "/api/v1/customers/";

    @Test
    void canRegisterCustomer() {
        //Create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();

        String firstName = fakerName.firstName() + "toto";
        String email = firstName + "-" + UUID.randomUUID() + "@amigoscode.com";
        int age = RAND.nextInt(1,100);

        CustomerRigistrationRequest request = new CustomerRigistrationRequest(
                firstName, email, age
        );

        //send a post request
        client.post()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRigistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //get all customers
        List<Customer> allCustomers = client.get()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {})
                .returnResult()
                .getResponseBody();

        //make sure that customer is present
        Customer expectedCustomer = new Customer(
                firstName, email, age
        );

        assertThat(allCustomers).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);

        Long id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        expectedCustomer.setId(id);

        //get customer by id
        client.get()
                .uri(URI + "{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {})
                .isEqualTo(expectedCustomer);
    }

    @Test
    void canDeleteCustomer() {
        //Create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();

        String firstName = fakerName.firstName()+ "toto";
        String email = firstName + "-" + UUID.randomUUID() + "@amigoscode.com";
        int age = RAND.nextInt(1,100);

        CustomerRigistrationRequest request = new CustomerRigistrationRequest(
                firstName, email, age
        );

        //send a post request
        client.post()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRigistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //get all customers
        List<Customer> allCustomers = client.get()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {})
                .returnResult()
                .getResponseBody();


        Long id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        //delete customer
        client.delete()
                .uri(URI + "{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        //get customer by id
        client.get()
                .uri(URI + "{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();

    }

    @Test
    void canUpdateCustomer() {
        //Create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();

        String firstName = fakerName.firstName() + "toto";
        String email = firstName + "-" + UUID.randomUUID() + "@amigoscode.com";
        int age = RAND.nextInt(1,100);

        CustomerRigistrationRequest request = new CustomerRigistrationRequest(
                firstName, email, age
        );

        //send a post request
        client.post()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRigistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //get all customers
        List<Customer> allCustomers = client.get()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {})
                .returnResult()
                .getResponseBody();


        Long id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        
        //update customer
        String newName = firstName + faker.name().firstName();
        String newEmail = email + fakerName.lastName() + "-" + UUID.randomUUID() + "@amigoscode.com";
        int newAge = age + 1 ;


        //updateName
        CustomerUpdateRequest updateNameRequest = new CustomerUpdateRequest(
                newName, null, null
        );
        client.put()
                .uri(URI + "{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateNameRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();


        //get customer by id
        Customer updatedCustomer = client.get()
                .uri(URI + "{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        Customer customer = new Customer(
                id, newName, email, age
        );

        assertThat(updatedCustomer).isEqualTo(customer);
    }

}
