package guru.springframework.msscbrewery.web.controller;

import guru.springframework.msscbrewery.services.CustomerService;
import guru.springframework.msscbrewery.web.model.CustomerDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/v1/customer")
@RestController
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDto> getCustomer(@PathVariable UUID customerId) {
       return    new ResponseEntity<>(customerService.getCustomerById(customerId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> handlePost(@RequestBody CustomerDto customer) {
        CustomerDto saved = customerService.save(customer);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Location", "/api/v1/customer/"+saved.getId());
        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping({"/{customerId}"})
    public void handlePut(@PathVariable UUID customerId, @RequestBody CustomerDto customerDto) {
        customerService.update(customerId, customerDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping({"/{customerId}"})
    public void handleDelete(@PathVariable UUID customerId) {
        customerService.delete(customerId);
    }
}
