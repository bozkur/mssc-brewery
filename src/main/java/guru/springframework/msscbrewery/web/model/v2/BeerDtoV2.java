package guru.springframework.msscbrewery.web.model.v2;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class BeerDtoV2 {
    private UUID id;
    private String beerName;
    private BeerStyle beerStyle;
    private Long upc;
}