package guru.springframework.msscbrewery.domain;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Customer {
    private UUID id;
    private String name;
}
