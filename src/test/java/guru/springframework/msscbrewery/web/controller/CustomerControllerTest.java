package guru.springframework.msscbrewery.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.msscbrewery.services.CustomerService;
import guru.springframework.msscbrewery.web.model.CustomerDto;
import net.bytebuddy.utility.RandomString;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @MockBean
    private CustomerService customerService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Insert a new customer")
    void shouldInsertACustomer() throws Exception {
        CustomerDto expectedCustomer = CustomerDto.builder()
                .id(UUID.randomUUID()).name("New customer").build();
        when(customerService.save(expectedCustomer)).thenReturn(expectedCustomer);
        performPost(expectedCustomer)
                .andExpect(MockMvcResultMatchers.status().isCreated());
        ArgumentCaptor<CustomerDto> actualCaptor = ArgumentCaptor.forClass(CustomerDto.class);
        verify(customerService).save(actualCaptor.capture());
        assertThat(actualCaptor.getValue(), Matchers.equalTo(expectedCustomer));
    }

    private ResultActions performPost(CustomerDto expectedCustomer) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(expectedCustomer)));
    }

    @Test
    @DisplayName("Update a customer record")
    void shouldHandlePut() throws Exception {
        UUID uuid = UUID.randomUUID();
        CustomerDto expectedCustomer = CustomerDto.builder().id(uuid).name("New name").build();
        doNothing().when(customerService).update(uuid, expectedCustomer);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/customer/"+uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(expectedCustomer)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        ArgumentCaptor<CustomerDto> actualCaptor = ArgumentCaptor.forClass(CustomerDto.class);
        verify(customerService).update(ArgumentMatchers.eq(uuid), actualCaptor.capture());
        assertThat(actualCaptor.getValue(), Matchers.equalTo(expectedCustomer));
    }

    @Test
    @DisplayName("Delete a customer record")
    void shouldDeleteACustomer() throws Exception {
        UUID uuid = UUID.randomUUID();
        doNothing().when(customerService).delete(uuid);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/customer/"+uuid))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        ArgumentCaptor<UUID> uuidCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(customerService).delete(uuidCaptor.capture());
        assertThat(uuidCaptor.getValue(), Matchers.equalTo(uuid));
    }

    @Test
    @DisplayName("Create customer returns bad request when name field is blank")
    void shouldReturnBadRequestWhenNameIsBlank() throws Exception {
        CustomerDto invalidCustomer = CustomerDto.builder().id(UUID.randomUUID()).name("").build();
        performPost(invalidCustomer)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.equalTo(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].fieldName", Matchers.equalTo("name")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].fieldName", Matchers.equalTo("name")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].errorCode", Matchers.anyOf(Matchers.equalTo("NotBlank"), Matchers.equalTo("Size"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].errorCode", Matchers.anyOf(Matchers.equalTo("NotBlank"), Matchers.equalTo("Size"))));
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 101})
    @DisplayName("Create a customer returns bad request when name field is invalid")
    void shouldReturnBadRequestResponseWhenNameFieldSizeIsInvalid(int size) throws Exception {
        String invalidName = createRandomString(size);
        CustomerDto invalidCustomer = CustomerDto.builder().id(UUID.randomUUID()).name(invalidName).build();
        performPost(invalidCustomer)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.equalTo(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].fieldName", Matchers.equalTo("name")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].errorCode", Matchers.equalTo("Size")));
    }

    private static String createRandomString(int i) {
        return RandomString.make(i);
    }
}