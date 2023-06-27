package guru.springframework.msscbrewery.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.msscbrewery.services.BeerService;
import guru.springframework.msscbrewery.web.model.BeerDto;
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
@WebMvcTest(BeerController.class)
class BeerControllerTest {
    @MockBean
    private BeerService beerService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("should get a beer")
    void shouldGetABeer() throws Exception {

        UUID beerId = UUID.randomUUID();
        BeerDto expectedBeer = BeerDto.builder().beerStyle("zero").beerName("Edelmeister Zero").id(beerId)
                                        .build();
        BDDMockito.given(beerService.getBeerById(beerId)).willReturn(expectedBeer);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beer/"+beerId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.beerName", Matchers.equalTo(expectedBeer.getBeerName())));
    }

    @Test
    @DisplayName("should insert a beer")
    void shouldPostABeer() throws Exception {
        BiConsumer<ArgumentCaptor<BeerDto>, BeerDto> consumer = (savedBeerCaptor, expectedBeer) -> {
            /*BeerDto save = beerService.save(savedBeerCaptor.capture());
            BDDMockito.given(save).willReturn(expectedBeer);*/
            when(beerService.save(savedBeerCaptor.capture())).thenReturn(expectedBeer);
        };
        testRequestBody(MockMvcRequestBuilders.post("/api/v1/beer"),
                MockMvcResultMatchers.status().isCreated(), consumer);
    }

    private void testRequestBody(MockHttpServletRequestBuilder requestBuilder, ResultMatcher resultMatcher, BiConsumer<ArgumentCaptor<BeerDto>, BeerDto> consumer) throws Exception {
        UUID beerId = UUID.randomUUID();
        BeerDto expectedBeer = BeerDto.builder().beerStyle("zero").beerName("Edelmeister Zero").id(beerId)
                .build();
        ArgumentCaptor<BeerDto> savedBeerCaptor = ArgumentCaptor.forClass(BeerDto.class);
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
        BiConsumer<ArgumentCaptor<BeerDto>, BeerDto> consumer = (savedBeerCaptor, expectedBeer) -> {
            doNothing().when(beerService).update(ArgumentMatchers.eq(uuid), savedBeerCaptor.capture());
        };
        testRequestBody(MockMvcRequestBuilders.put("/api/v1/beer/" + uuid), MockMvcResultMatchers.status().isNoContent(), consumer);
    }
}