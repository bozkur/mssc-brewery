package guru.springframework.msscbrewery.web.controller.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.msscbrewery.services.v2.BeerServiceV2;
import guru.springframework.msscbrewery.web.model.v2.BeerDtoV2;
import guru.springframework.msscbrewery.web.model.v2.BeerStyle;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;
import java.util.function.BiConsumer;

import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
@WebMvcTest(BeerControllerV2.class)
class BeerControllerV2Test {
    public static final String URL_PATH = "/api/v2/beer";
    @MockBean
    private BeerServiceV2 beerService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("should get a beer")
    void shouldGetABeer() throws Exception {

        UUID beerId = UUID.randomUUID();
        BeerDtoV2 expectedBeer = BeerDtoV2.builder().beerStyle(BeerStyle.LAGER)
                .beerName("Edelmeister Zero").id(beerId).build();
        BDDMockito.given(beerService.getBeerById(beerId)).willReturn(expectedBeer);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v2/beer/"+beerId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.beerName", Matchers.equalTo(expectedBeer.getBeerName())));
    }

    @Test

    @DisplayName("should insert a beer")
    void shouldPostABeer() throws Exception {
        BiConsumer<ArgumentCaptor<BeerDtoV2>, BeerDtoV2> consumer = (savedBeerCaptor, expectedBeer) -> {
            /*BeerDto save = beerService.save(savedBeerCaptor.capture());
            BDDMockito.given(save).willReturn(expectedBeer);*/
            when(beerService.save(savedBeerCaptor.capture())).thenReturn(expectedBeer);
        };
        testRequestBody(MockMvcRequestBuilders.post(URL_PATH),
                MockMvcResultMatchers.status().isCreated(), consumer);
    }

    private void testRequestBody(MockHttpServletRequestBuilder requestBuilder, ResultMatcher resultMatcher,
                                 BiConsumer<ArgumentCaptor<BeerDtoV2>, BeerDtoV2> consumer) throws Exception {
        UUID beerId = UUID.randomUUID();
        BeerDtoV2 expectedBeer = BeerDtoV2.builder().beerStyle(BeerStyle.LAGER)
                .beerName("Edelmeister Zero").id(beerId).build();
        ArgumentCaptor<BeerDtoV2> savedBeerCaptor = ArgumentCaptor.forClass(BeerDtoV2.class);
        consumer.accept(savedBeerCaptor, expectedBeer);

        mockMvc.perform(requestBuilder.contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(expectedBeer)))
                .andExpect(resultMatcher);
        MatcherAssert.assertThat(savedBeerCaptor.getValue(), Matchers.equalTo(expectedBeer));
    }

    @Test
    @DisplayName("should update a beer")
    void shouldPutABeer() throws Exception {
        UUID uuid = UUID.randomUUID();
        BiConsumer<ArgumentCaptor<BeerDtoV2>, BeerDtoV2> consumer = (savedBeerCaptor, expectedBeer) -> doNothing().when(beerService).update(ArgumentMatchers.eq(uuid), savedBeerCaptor.capture());
        testRequestBody(MockMvcRequestBuilders.put("/api/v2/beer/" + uuid), MockMvcResultMatchers.status().isNoContent(), consumer);
    }

    @Test
    @DisplayName("shoud delete a beer")
    void shouldDeleteABeer() throws Exception {
        UUID uuid = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v2/beer/"+ uuid))
                        .andExpect(MockMvcResultMatchers.status().isNoContent());


        verify(beerService).delete(ArgumentMatchers.eq(uuid));
    }

    @Test
    @DisplayName("Return an error when newly created beer is not valid")
    void shouldReturnErrorWhenCreatedBeerIsNotValid() throws Exception {
        BeerDtoV2 invalid = BeerDtoV2.builder().beerName("").beerStyle(BeerStyle.LAGER)
                .id(UUID.randomUUID()).build();
        mockMvc.perform(MockMvcRequestBuilders.post(URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(invalid)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.equalTo(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].fieldName", Matchers.equalTo("beerName")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].errorCode", Matchers.equalTo("NotBlank")));

    }
}