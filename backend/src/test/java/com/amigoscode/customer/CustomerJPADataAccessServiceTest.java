package com.amigoscode.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService customerJPADataAccessService;

    private AutoCloseable autoCloseable;

    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        customerJPADataAccessService = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {
        //GIVEN

        //WHEN
        customerJPADataAccessService.selectAllCustomers();

        //THEN
        verify(customerRepository).findAll();
    }

    @Test
    void selectCustomerById() {
        //GIVEN
        Long id = 1L;

        //WHEN
        customerJPADataAccessService.selectCustomerById(id);

        //THEN
        verify(customerRepository).findById(id.intValue());
    }

    @Test
    void insertCustomer() {
        //GIVEN
        Customer customer = new Customer(
                1L, "Ali", "ali@gmail.com", 12);

        //WHEN
        customerJPADataAccessService.insertCustomer(customer);

        //THEN
        verify(customerRepository).save(customer);
    }

    @Test
    void existsPersonWithEmail() {
        //GIVEN
        String email = "test@test.com";
        //WHEN
        customerJPADataAccessService.existsPersonWithEmail(email);

        //THEN
        verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void deleteCustomerById() {
        //GIVEN
        Long id = 1L;
        //WHEN
        customerJPADataAccessService.deleteCustomerById(id);

        //THEN
        verify(customerRepository).deleteById(id.intValue());
    }

    @Test
    void existsCustomerById() {
        //GIVEN
        Long id = 1L;
        //WHEN
        customerJPADataAccessService.existsCustomerById(id);

        //THEN
        verify(customerRepository).existsCustomerById(id);
    }

    @Test
    void updateCustomer() {
        //GIVEN
        Customer customer = new Customer(
                1L, "Ali", "ali@gmail.com", 12);
        //WHEN
        customerJPADataAccessService.updateCustomer(customer);

        //THEN
        verify(customerRepository).save(customer);
    }
}