package guru.springframework.msscbrewery.services;

import guru.springframework.msscbrewery.web.model.CustomerDto;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomerServiceImpl implements CustomerService{
    @Override
    public CustomerDto getCustomerById(UUID customerId) {
        return CustomerDto.builder().id(customerId).name("Constant name").build();
    }

    @Override
    public CustomerDto save(CustomerDto customer) {
        return CustomerDto.builder().name(customer.getName()).id(UUID.randomUUID()).build();
    }

    @Override
    public void update(UUID customerId, CustomerDto customerDto) {
        //TODO:
    }

    @Override
    public void delete(UUID customerId) {
        //TODO:
    }
}
