package guru.springframework.msscbrewery.web.controller;

import guru.springframework.msscbrewery.services.CustomerService;
import guru.springframework.msscbrewery.web.model.CustomerDto;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public ResponseEntity<Void> handlePost(@Valid @RequestBody CustomerDto customer) {
        CustomerDto saved = customerService.save(customer);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Location", "/api/v1/customer/"+saved.getId());
        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping({"/{customerId}"})
    public void handlePut(@PathVariable UUID customerId, @Valid @RequestBody CustomerDto customerDto) {
        customerService.update(customerId, customerDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping({"/{customerId}"})
    public void handleDelete(@PathVariable UUID customerId) {
        customerService.delete(customerId);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<FieldErrorSummary>> handleValidationError(MethodArgumentNotValidException ex) {
        List<FieldErrorSummary> responseBody = ex.getFieldErrors().stream().map(FieldErrorSummary::new).collect(Collectors.toList());
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @NoArgsConstructor
    @Getter
    public static final class FieldErrorSummary implements Serializable {
        private  String fieldName;
        private  String errorCode;

        private FieldErrorSummary(FieldError fieldError) {
            this.fieldName = fieldError.getField();
            this.errorCode = fieldError.getCode();
        }
    }
}
