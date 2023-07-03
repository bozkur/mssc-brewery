package guru.springframework.msscbrewery.web.mappers;

import guru.springframework.msscbrewery.domain.Beer;
import guru.springframework.msscbrewery.web.model.v2.BeerDtoV2;
import org.mapstruct.Mapper;

@Mapper
public interface BeerMapper {
    BeerDtoV2 beer2BeerDto(Beer beer);

    Beer beerDto2Beer(BeerDtoV2 beerDto);
}
