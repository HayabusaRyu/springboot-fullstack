package com.amigoscode.customer;

import com.amigoscode.exception.DuplicateResourceException;
import com.amigoscode.exception.NoDataChangeException;
import com.amigoscode.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;

    private CustomerService customerService;


    @BeforeEach
    void setUp() {
        customerService = new CustomerService(customerDao);
    }

    @Test
    void getAllCustomers() {
        //WHEN
        customerService.getAllCustomers();
        //THEN
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        //GIVEN
        Long id = 10L;
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com",  19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        //WHEN
        Customer actual = customerService.getCustomer(id);

        //THEN
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willThrowWhenGetCustomerReturnEmptyOptional() {
        //GIVEN
        Long id = 10L;
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        //THEN
        assertThatThrownBy(() -> customerService.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));
    }

    @Test
    void addCustomer() {
        //GIVEN
        String email = "alex@gmail.com";

        when(customerDao.existsPersonWithEmail(email)).thenReturn(false);

        CustomerRigistrationRequest request = new CustomerRigistrationRequest(
                "Alex", "alex@gmail.com",  19
        );

        //WHEN
        customerService.addCustomer(request);

        //THEN
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void willThrowExceptionWhenEmailExistWhileAddingCustomer() {
        //GIVEN
        String email = "alex@gmail.com";

        when(customerDao.existsPersonWithEmail(email)).thenReturn(true);

        CustomerRigistrationRequest request = new CustomerRigistrationRequest(
                "Alex", "alex@gmail.com",  19
        );

        //WHEN
        assertThatThrownBy(() -> customerService.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        //THEN
        verify(customerDao, never()).insertCustomer(any());
    }



    @Test
    void deleteCustomer() {
        //GIVEN
        Long id = 10L;

        when(customerDao.existsCustomerById(id)).thenReturn(true);

        //WHEN
        customerService.deleteCustomer(id);

        //THEN
        verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void willTrhowExceptionWhenCustomerIdDontExistWhileDeletingCustomer() {
        //GIVEN
        Long id = 10L;

        when(customerDao.existsCustomerById(id)).thenReturn(false);

        //WHEN
        assertThatThrownBy(() -> customerService.deleteCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("id does not exist");

        //THEN
        verify(customerDao, never()).deleteCustomerById(any());
    }

    @Test
    void canUpdateAllCustomersProperties() {
        //GIVEN
        Long id = 10L;
        String email = "julie@gmail.com";
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com",  19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        when(customerDao.existsPersonWithEmail(email)).thenReturn(false);

        CustomerRigistrationRequest request = new CustomerRigistrationRequest(
                "Julie", "julie@gmail.com", 20
        );

        //WHEN
        customerService.updateCustomer(id, request);

        //THEN
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void canOnlyUpdateCustomerName() {
        //GIVEN
        Long id = 10L;
        String email = "julie@gmail.com";
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com",  19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerRigistrationRequest request = new CustomerRigistrationRequest(
                "Julie", null, null
        );

        //WHEN
        customerService.updateCustomer(id, request);

        //THEN
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void canOnlyUpdateCustomerEmail() {
        //GIVEN
        Long id = 10L;
        String email = "julie@gmail.com";
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com",  19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        when(customerDao.existsPersonWithEmail(email)).thenReturn(false);

        CustomerRigistrationRequest request = new CustomerRigistrationRequest(
                null, email, null
        );

        //WHEN
        customerService.updateCustomer(id, request);

        //THEN
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void canOnlyUpdateCustomerAge() {
        //GIVEN
        Long id = 10L;
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com",  19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerRigistrationRequest request = new CustomerRigistrationRequest(
                null, null, 22
        );

        //WHEN
        customerService.updateCustomer(id, request);

        //THEN
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void willThrowExceptionWhenTryingToUpdateCustomerEmailWhenAlreadyTaken() {
        //GIVEN
        Long id = 10L;
        String email = "julie@gmail.com";
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com",  19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        when(customerDao.existsPersonWithEmail(email)).thenReturn(true);

        CustomerRigistrationRequest request = new CustomerRigistrationRequest(
                null, email, null
        );

        //WHEN
        assertThatThrownBy(() -> customerService.updateCustomer(id, request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        //THEN
        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void willThrowExceptionWhenCustomerUpdateHasNoChanges() {
        //GIVEN
        Long id = 10L;
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com",  19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerRigistrationRequest request = new CustomerRigistrationRequest(
                "Alex", "alex@gmail.com", 19
        );

        //WHEN
        assertThatThrownBy(() -> customerService.updateCustomer(id, request))
                .isInstanceOf(NoDataChangeException.class)
                .hasMessage("no data changes founded");

        //THEN
        verify(customerDao, never()).updateCustomer(any());
    }
}