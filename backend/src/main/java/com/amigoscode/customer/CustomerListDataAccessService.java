package com.amigoscode.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("list")
public class CustomerListDataAccessService implements CustomerDao{


    //db
    private static List<Customer> customers;

    static{
        customers = new ArrayList<>();
        Customer alex = new Customer(
                1L,
                "Alex",
                "@alex.com",
                15
        );
        customers.add(alex);

        Customer jamila = new Customer(
                2L,
                "Jamila",
                "@jamila.com",
                14
        );
        customers.add(jamila);
    }

    @Override
    public List<Customer> selectAllCustomers() {
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Long customerId) {
        return customers
                .stream()
                .filter(customer -> customer.getId().equals(customerId))
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        customers.add(customer);
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        return customers.stream().anyMatch(
                c -> c.getEmail().equals(email)
        );
    }

    @Override
    public void deleteCustomerById(Long customerId) {
        customers.removeIf(customer -> customer.getId().equals(customerId));
    }

    @Override
    public boolean existsCustomerById(Long customerId) {
        return customers.stream().anyMatch(customer -> customer.getId().equals(customerId));
    }

    @Override
    public void updateCustomer(Customer customer) {
        deleteCustomerById(customer.getId().longValue());
        insertCustomer(customer);
    }

}
