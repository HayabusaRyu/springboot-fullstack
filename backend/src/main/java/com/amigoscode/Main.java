package com.amigoscode;


import com.amigoscode.customer.Customer;
import com.amigoscode.customer.CustomerRepository;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository){
        Faker faker = new Faker();
        Name name = faker.name();
        return args -> {
            Customer customer = new Customer(
                    name.firstName(),
                    name.fullName() + "@gmail.com",
                    faker.number().numberBetween(7, 80)
            );

            customerRepository.save(customer);
        };
    }
}
