package com.amigoscode.customer;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerRowMapperTest {


    @Test
    void mapRow() throws SQLException {
        //GIVEN
        CustomerRowMapper customerRowMapper = new CustomerRowMapper();
        ResultSet resultSetMock = mock(ResultSet.class);
        when(resultSetMock.getLong("id")).thenReturn(10L);
        when(resultSetMock.getString("name")).thenReturn("jamila");
        when(resultSetMock.getString("email")).thenReturn("jamila@gmail.com");
        when(resultSetMock.getInt("age")).thenReturn(19);

        //WHEN
        Customer actual = customerRowMapper.mapRow(resultSetMock, 1);

        //THEN
        Customer expected = new Customer(
                10L, "jamila", "jamila@gmail.com", 19
        );

        assertThat(actual).isEqualTo(expected);
    }
}