package com.amigoscode.customer;

import com.amigoscode.AbstractTestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerJDBCDataAccessServiceTest extends AbstractTestContainers {

    private CustomerJDBCDataAccessService customerJDBCDataAccessService;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();



    @BeforeEach
    void setUp() {
        customerJDBCDataAccessService = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper);
    }

    @Test
    void selectAllCustomers() {
        //Given
        Customer customer = new Customer(
            FAKER.name().fullName(),
            FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
            20
        );
        customerJDBCDataAccessService.insertCustomer(customer);
        //WHEN
        List<Customer> customers = customerJDBCDataAccessService.selectAllCustomers();

        //THEN
        assertThat(customers).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        //GIVEN
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        customerJDBCDataAccessService.insertCustomer(customer);
        Long id = customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();


        //WHEN
        Optional<Customer> actualCustomer = customerJDBCDataAccessService.selectCustomerById(id);


        //THEN
        assertThat(actualCustomer)
                .isPresent().
                hasValueSatisfying(c -> {
                    assertThat(c.getId()).isEqualTo(id);
                    assertThat(c.getName()).isEqualTo(customer.getName());
                    assertThat(c.getEmail()).isEqualTo(customer.getEmail());
                    assertThat(c.getAge()).isEqualTo(customer.getAge());
                });
    }

    @Test
    void willReturnEmptyWhenSelectCustomerById(){
        //GIVEN
        Long id = -1L;

        //WHEN
        var actual = customerJDBCDataAccessService.selectCustomerById(id);

        //THEN
        assertThat(actual).isEmpty();
    }

    @Test
    void insertCustomer() {
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        int age = FAKER.hashCode();

        //GIVEN
        Customer customer = new Customer(
                name,
                email,
                age
        );

        //WHEN
        customerJDBCDataAccessService.insertCustomer(customer);

        //THEN
        assertThat(customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .findFirst()
                .orElseThrow());
    }

    @Test
    void existsPersonWithEmail() {
        //GIVEN
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        customerJDBCDataAccessService.insertCustomer(customer);
        //WHEN
        boolean actual = customerJDBCDataAccessService.existsPersonWithEmail(email);

        //THEN
        assertThat(actual).isTrue();
    }

    @Test
    void returnFalseWhenExistsPersonWithEmail() {
        //GIVEN
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        //WHEN
        boolean actual = customerJDBCDataAccessService.existsPersonWithEmail(email);

        //THEN
        assertThat(actual).isFalse();
    }

    @Test
    void deleteCustomerById() {
        //GIVEN
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        customerJDBCDataAccessService.insertCustomer(customer);
        Long id = customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //WHEN
        customerJDBCDataAccessService.deleteCustomerById(id);


        //THEN
        assertThat(customerJDBCDataAccessService.selectCustomerById(id))
                .isNotPresent();
    }

    @Test
    void existsCustomerById() {
        //GIVEN
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        customerJDBCDataAccessService.insertCustomer(customer);

        Long id = customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //WHEN
        boolean actual = customerJDBCDataAccessService.existsCustomerById(id);

        //THEN
        assertThat(actual).isTrue();
    }

    @Test
    void returnFalseWhenExistsCustomerById(){
        //GIVEN
        Long id = -1L;

        //WHEN
        boolean actual = customerJDBCDataAccessService.existsCustomerById(id);

        //THEN
        assertThat(actual).isFalse();
    }

    @Test
    void updateCustomerName() {
        //GIVEN
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        customerJDBCDataAccessService.insertCustomer(customer);
        Long id = customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();


        String newName = FAKER.name().fullName();
        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(id);
        updatedCustomer.setName(newName);

        //WHEN
        customerJDBCDataAccessService.updateCustomer(updatedCustomer);

        //THEN
        assertThat(customerJDBCDataAccessService.selectCustomerById(id))
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c.getId()).isEqualTo(id);
                    assertThat(c.getName()).isEqualTo(newName);
                    assertThat(c.getEmail()).isEqualTo(customer.getEmail());
                    assertThat(c.getAge()).isEqualTo(customer.getAge());
                });
    }

    @Test
    void updateCustomerEmail() {
        //GIVEN
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        customerJDBCDataAccessService.insertCustomer(customer);
        Long id = customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        String newEmail = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(id);
        updatedCustomer.setEmail(newEmail);

        //WHEN
        customerJDBCDataAccessService.updateCustomer(updatedCustomer);

        //THEN
        assertThat(customerJDBCDataAccessService.selectCustomerById(id))
                .isPresent().hasValueSatisfying(c -> {
                    assertThat(c.getId()).isEqualTo(id);
                    assertThat(c.getName()).isEqualTo(customer.getName());
                    assertThat(c.getEmail()).isEqualTo(newEmail);
                    assertThat(c.getAge()).isEqualTo(customer.getAge());
                });
    }

    @Test
    void updateCustomerAge() {
        //GIVEN
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        customerJDBCDataAccessService.insertCustomer(customer);
        Long id = customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        int newAge = FAKER.hashCode();

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(id);
        updatedCustomer.setAge(newAge);

        //WHEN
        customerJDBCDataAccessService.updateCustomer(updatedCustomer);

        //THEN
        assertThat(customerJDBCDataAccessService.selectCustomerById(id))
                .isPresent().hasValueSatisfying(c -> {
                    assertThat(c.getId()).isEqualTo(id);
                    assertThat(c.getName()).isEqualTo(customer.getName());
                    assertThat(c.getEmail()).isEqualTo(customer.getEmail());
                    assertThat(c.getAge()).isEqualTo(newAge);
                });
    }


    @Test
    void updateCustomerAllProperties() {
        //GIVEN
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        customerJDBCDataAccessService.insertCustomer(customer);
        Long id = customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        String newEmail = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(id);
        updatedCustomer.setName(FAKER.name().fullName());
        updatedCustomer.setEmail(newEmail);
        updatedCustomer.setAge(FAKER.hashCode());

        //WHEN
        customerJDBCDataAccessService.updateCustomer(updatedCustomer);

        //THEN
        assertThat(customerJDBCDataAccessService.selectCustomerById(id))
                .isPresent()
                .hasValue(updatedCustomer);
    }

    @Test
    void willNotUpdateCustomerWhenNothingChange() {
        //GIVEN
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        customerJDBCDataAccessService.insertCustomer(customer);
        Long id = customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(id);


        //WHEN
        customerJDBCDataAccessService.updateCustomer(updatedCustomer);

        //THEN
        assertThat(customerJDBCDataAccessService.selectCustomerById(id))
                .isPresent().hasValueSatisfying(c -> {
                    assertThat(c.getId()).isEqualTo(id);
                    assertThat(c.getName()).isEqualTo(customer.getName());
                    assertThat(c.getEmail()).isEqualTo(customer.getEmail());
                    assertThat(c.getAge()).isEqualTo(customer.getAge());
                });
    }
}