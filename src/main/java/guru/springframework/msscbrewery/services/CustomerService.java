package guru.springframework.msscbrewery.services;

import guru.springframework.msscbrewery.web.model.CustomerDto;

import java.util.UUID;

public interface CustomerService {
    CustomerDto getCustomerById(UUID cutomerId);

    CustomerDto save(CustomerDto customer);

    void update(UUID customerId, CustomerDto customerDto);

    void delete(UUID customerId);
}
