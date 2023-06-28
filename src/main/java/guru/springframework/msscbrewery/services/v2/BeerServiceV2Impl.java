package guru.springframework.msscbrewery.services.v2;

import guru.springframework.msscbrewery.web.model.BeerDto;
import guru.springframework.msscbrewery.web.model.v2.BeerDtoV2;
import guru.springframework.msscbrewery.web.model.v2.BeerStyle;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BeerServiceV2Impl implements BeerServiceV2 {
    @Override
    public BeerDtoV2 getBeerById(UUID beerId) {
        return BeerDtoV2.builder().beerName("Efes")
                .beerStyle(BeerStyle.LAGER).id(beerId).build();
    }

    @Override
    public BeerDtoV2 save(BeerDtoV2 beerDto) {
        return BeerDtoV2.builder()
                .beerName(beerDto.getBeerName())
                .beerStyle(beerDto.getBeerStyle())
                .id(UUID.randomUUID()).build();
    }

    @Override
    public void update(UUID beerId, BeerDtoV2 beerDto) {
        //Todo
    }

    @Override
    public void delete(UUID beerId) {
        //Todo
    }
}
