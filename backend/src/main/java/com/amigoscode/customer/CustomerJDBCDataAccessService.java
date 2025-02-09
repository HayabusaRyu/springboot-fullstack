package com.amigoscode.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements  CustomerDao{

    private final JdbcTemplate jdbcTemplate;

    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }


    @Override
    public List<Customer> selectAllCustomers() {
        var sql = "select id, name, email, age from customer";

        List<Customer> customers = jdbcTemplate.query(sql, customerRowMapper);

        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Long customerId) {
        var sql = "select id, name, email, age from customer where id = ?";

        return jdbcTemplate
                .query(sql, customerRowMapper, customerId)
                .stream()
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        var sql = """
                INSERT INTO customer (name, email, age)
                VALUES (?, ?, ?)
                """;
        int update = jdbcTemplate.update(
                sql,
                customer.getName(),
                customer.getEmail(),
                customer.getAge());
        System.out.println("jdbcTemplate.update = " + update);
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        var sql = "select count(id) from customer where email = ?";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public void deleteCustomerById(Long customerId) {
        var sql = "DELETE FROM customer WHERE id = ?";

        jdbcTemplate.update(sql, customerId);
        System.out.println("jdbcTemplate.delete = " + customerId);
    }

    @Override
    public boolean existsCustomerById(Long customerId) {
        var sql = "select count(*) from customer where id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                        rs.getLong(1) > 0, customerId)
                .stream()
                .findFirst()
                .orElse(false);
    }

    @Override
    public void updateCustomer(Customer customer) {

        if(customer.getName() != null) {
            String sql = "update customer set name=? where id = ?";
            int result  = jdbcTemplate.update(sql, customer.getName(), customer.getId());
            System.out.println("update customer name result = " + result);
        }
        if(customer.getEmail() != null) {
            String sql = "update customer set email=? where id = ?";
            int result  = jdbcTemplate.update(sql, customer.getEmail(), customer.getId());
            System.out.println("update customer email result = " + result);
        }
        if(customer.getAge() != null) {
            String sql = "update customer set age=? where id = ?";
            int result  = jdbcTemplate.update(sql, customer.getAge(), customer.getId());
            System.out.println("update customer age result = " + result);
        }
    }
}
