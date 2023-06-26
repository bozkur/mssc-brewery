package guru.springframework.msscbrewery.services;

import guru.springframework.msscbrewery.web.model.BeerDto;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BeerServiceImpl implements BeerService{
    @Override
    public BeerDto getBeerById(UUID beerId) {
        return BeerDto.builder().beerName("Efes").beerStyle("Dark").id(beerId).build();
    }

    @Override
    public BeerDto save(BeerDto beerDto) {
        return BeerDto.builder()
                .beerName(beerDto.getBeerName())
                .beerStyle(beerDto.getBeerStyle())
                .id(UUID.randomUUID()).build();
    }

    @Override
    public void update(UUID beerId, BeerDto beerDto) {
        //Todo
    }
}
