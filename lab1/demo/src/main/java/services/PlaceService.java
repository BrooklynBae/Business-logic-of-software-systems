package services;

import data.repository.PlaceRepository;
import data.tables.Place;
import dto.PlaceDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaceService {

    private final PlaceRepository placeRepository;

    public PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    private PlaceDto toResponse(Place place) {
        return new PlaceDto(
                place.getId(),
                place.getName(),
                place.getTown(),
                place.getDescription(),
                place.getPlaceType(),
                place.getRating(),
                place.getOwner()
        );
    }
    public List<PlaceDto> findByTown(String town) {
        return placeRepository.findByTownIgnoreCase(town).stream()
                .map(place -> toResponse(place))
                .toList();
    }

    public List<PlaceDto> findAllSortedByRating() {
        return placeRepository.findByRatingOrderByRatingDesc()
                .stream()
                .map(place -> toResponse(place))
                .toList();
    }

    public PlaceDto findPlace(long id) {
        return null;
    }
}
