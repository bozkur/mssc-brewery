package guru.springframework.msscbrewery.domain;

import guru.springframework.msscbrewery.web.model.v2.BeerStyle;
import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Beer {
    private UUID id;
    private String beerName;
    private BeerStyle beerStyle;
    private Long upc;

    private Timestamp createdDate;
    private Timestamp lastUpdateDate;
}
