package com.amigoscode.customer;

import com.amigoscode.exception.DuplicateResourceException;
import com.amigoscode.exception.NoDataChangeException;
import com.amigoscode.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }


    public List<Customer> getAllCustomers(){
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomer(Long customerId){

        return customerDao.selectCustomerById(customerId)
                .orElseThrow(
                () -> new ResourceNotFoundException(
                        "customer with id [%s] not found".formatted(customerId))
        );
    }

    public void addCustomer(CustomerRigistrationRequest request){
        String email = request.email();
        if(customerDao.existsPersonWithEmail(email)){
            throw new DuplicateResourceException("email already taken");
        }

        Customer customer = new Customer(
                request.name(),
                request.email(),
                request.age()
        );
        customerDao.insertCustomer(customer);
    }

    public void deleteCustomer(Long customerId){
        if(!customerDao.existsCustomerById(customerId)){
            throw new ResourceNotFoundException("id does not exist");
        }
        customerDao.deleteCustomerById(customerId);
    }

    public void updateCustomer(Long customerId, CustomerRigistrationRequest request){
        Customer customer = getCustomer(customerId);

        boolean changes = false;

        if(request.name() != null && !request.name().equals(customer.getName())){
            customer.setName(request.name());
            changes = true;
        }
        if(request.email() != null && !request.email().equals(customer.getEmail())){
            if(customerDao.existsPersonWithEmail(request.email())){
                throw new DuplicateResourceException("email already taken");
            }

            customer.setEmail(request.email());
            changes = true;
        }
        if(request.age() != null && !request.age().equals(customer.getAge())){
            customer.setAge(request.age());
            changes = true;
        }
        if(!changes){
            throw new NoDataChangeException("no data changes founded");
        }

        customerDao.updateCustomer(customer);
    }

}
