package services;

import data.repository.PlaceRepository;
import data.tables.Place;
import dto.PlaceResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaceService {

    private final PlaceRepository placeRepository;

    public PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    private PlaceResponse toResponse(Place place) {
        return new PlaceResponse(
                place.getId(),
                place.getName(),
                place.getTown(),
                place.getDescription(),
                place.getPlaceType(),
                place.getRating(),
                place.getOwner()
        );
    }
    public List<PlaceResponse> findByTown(String town) {
        return placeRepository.findByTownIgnoreCase(town).stream()
                .map(place -> toResponse(place))
                .toList();
    }

    public List<PlaceResponse> findAllSortedByRating() {
        return placeRepository.findByRatingOrderByRatingDesc()
                .stream()
                .map(place -> toResponse(place))
                .toList();
    }

    public PlaceResponse findPlace(long id) {
        return null;
    }
}
