package guru.springframework.msscbrewery.web.controller.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.msscbrewery.services.v2.BeerServiceV2;
import guru.springframework.msscbrewery.web.model.BeerDto;
import guru.springframework.msscbrewery.web.model.v2.BeerDtoV2;
import guru.springframework.msscbrewery.web.model.v2.BeerStyle;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.function.BiConsumer;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.snippet.Attributes.key;


@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(BeerControllerV2.class)
class BeerControllerV2Test {
    public static final String URL_PATH = "/api/v2/beer";
    @MockBean
    private BeerServiceV2 beerService;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation)
                        .uris().withHost("dev.springframework.guru").withPort(80).withScheme("https")).build();
    }

    @Test
    @DisplayName("should get a beer")
    void shouldGetABeer() throws Exception {

        UUID beerId = UUID.randomUUID();
        BeerDtoV2 expectedBeer = BeerDtoV2.builder().beerStyle(BeerStyle.LAGER)
                .beerName("Edelmeister Zero")
                .id(beerId)
                .createdDate(OffsetDateTime.now())
                .lastUpdateDate(OffsetDateTime.now())
                .upc(1L)
                .build();
        BDDMockito.given(beerService.getBeerById(beerId)).willReturn(expectedBeer);
        mockMvc.perform(get("/api/v2/beer/{beerId}", beerId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.beerName", Matchers.equalTo(expectedBeer.getBeerName())))
                .andDo(document("v2/beer-get",
                                RequestDocumentation.pathParameters(parameterWithName("beerId").description("UUID of the desired beer to get.")),
                                PayloadDocumentation.responseFields(
                                        fieldWithPath("id").description("Id of the beer."),
                                        fieldWithPath("createdDate").description("Creation date for the beer record"),
                                        fieldWithPath("lastUpdateDate").description("Last modification date for the beer record"),
                                        fieldWithPath("beerName").description("Name of the beer"),
                                        fieldWithPath("beerStyle").description("Style of the beer"),
                                        fieldWithPath("upc").description("UPC number of the beer")
                                )
                        ));
    }

    @Test

    @DisplayName("should insert a beer")
    void shouldPostABeer() throws Exception {
        BiConsumer<ArgumentCaptor<BeerDtoV2>, BeerDtoV2> consumer = (savedBeerCaptor, expectedBeer) -> {
            /*BeerDto save = beerService.save(savedBeerCaptor.capture());
            BDDMockito.given(save).willReturn(expectedBeer);*/
            when(beerService.save(savedBeerCaptor.capture())).thenReturn(expectedBeer);
        };
        ConstrainedFields fields = new ConstrainedFields(BeerDto.class);
        ResultHandler documentHandler = document("v2/beer-new", requestFields(
                fields.withPath("id").ignored(),
                fields.withPath("createdDate").ignored(),
                fields.withPath("lastUpdateDate").ignored(),
                fields.withPath("beerName").description("Name of the beer"),
                fields.withPath("beerStyle").description("Style of the beer"),
                fields.withPath("upc").description("UPC number of the beer")
        ));
        testRequestBody(post(URL_PATH),
                MockMvcResultMatchers.status().isCreated(), consumer, documentHandler);
    }

    private void testRequestBody(MockHttpServletRequestBuilder requestBuilder, ResultMatcher resultMatcher, BiConsumer<ArgumentCaptor<BeerDtoV2>, BeerDtoV2> consumer, ResultHandler documentHandler) throws Exception {
        UUID beerId = UUID.randomUUID();
        BeerDtoV2 expectedBeer = BeerDtoV2.builder().beerStyle(BeerStyle.LAGER)
                .beerName("Edelmeister Zero").id(beerId).upc(1L)
                .build();
        ArgumentCaptor<BeerDtoV2> savedBeerCaptor = ArgumentCaptor.forClass(BeerDtoV2.class);
        consumer.accept(savedBeerCaptor, expectedBeer);

        ResultActions resultActions = mockMvc.perform(requestBuilder.contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(expectedBeer)))
                .andExpect(resultMatcher);
        if (documentHandler != null) {
            resultActions.andDo(documentHandler);
        }
        MatcherAssert.assertThat(savedBeerCaptor.getValue(), Matchers.equalTo(expectedBeer));
    }

    @Test
    @DisplayName("should update a beer")
    void shouldPutABeer() throws Exception {
        UUID uuid = UUID.randomUUID();
        BiConsumer<ArgumentCaptor<BeerDtoV2>, BeerDtoV2> consumer = (savedBeerCaptor, expectedBeer) -> doNothing().when(beerService).update(ArgumentMatchers.eq(uuid), savedBeerCaptor.capture());
        testRequestBody(put("/api/v2/beer/" + uuid), MockMvcResultMatchers.status().isNoContent(), consumer, null);
    }

    @Test
    @DisplayName("shoud delete a beer")
    void shouldDeleteABeer() throws Exception {
        UUID uuid = UUID.randomUUID();

        mockMvc.perform(delete("/api/v2/beer/"+ uuid))
                        .andExpect(MockMvcResultMatchers.status().isNoContent());


        verify(beerService).delete(ArgumentMatchers.eq(uuid));
    }

    @Test
    @DisplayName("Return an error when newly created beer is not valid")
    void shouldReturnErrorWhenCreatedBeerIsNotValid() throws Exception {
        BeerDtoV2 invalid = BeerDtoV2.builder().beerName("").beerStyle(BeerStyle.LAGER)
                .id(UUID.randomUUID()).build();
        mockMvc.perform(post(URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(invalid)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.equalTo(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].fieldName", Matchers.equalTo("beerName")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].errorCode", Matchers.equalTo("NotBlank")));

    }

    private static class ConstrainedFields {

        private final ConstraintDescriptions constraintDescriptions;

        ConstrainedFields(Class<?> input) {
            this.constraintDescriptions = new ConstraintDescriptions(input);
        }

        private FieldDescriptor withPath(String path) {
            return fieldWithPath(path).attributes(key("constraints").value(StringUtils
                    .collectionToDelimitedString(this.constraintDescriptions
                            .descriptionsForProperty(path), ". ")));
        }
    }
}