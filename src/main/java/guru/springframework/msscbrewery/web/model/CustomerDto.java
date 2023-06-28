package guru.springframework.msscbrewery.web.model;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CustomerDto {

    private UUID id;
    private String name;
}
