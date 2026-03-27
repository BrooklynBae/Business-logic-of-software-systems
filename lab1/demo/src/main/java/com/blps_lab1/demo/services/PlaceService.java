package com.blps_lab1.demo.services;

import com.blps_lab1.demo.data.repository.OwnerRepository;
import com.blps_lab1.demo.data.repository.PlaceRepository;
import com.blps_lab1.demo.data.repository.ReservationRepository;
import com.blps_lab1.demo.data.tables.Owner;
import com.blps_lab1.demo.data.tables.Place;
import com.blps_lab1.demo.data.tables.Reservation;
import com.blps_lab1.demo.dto.CreatePlaceRequest;
import com.blps_lab1.demo.dto.DateDto;
import com.blps_lab1.demo.dto.PlaceDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final ReservationRepository reservationRepository;
    private final OwnerRepository ownerRepository;

    public PlaceService(PlaceRepository placeRepository, ReservationRepository reservationRepository, OwnerRepository ownerRepository) {
        this.placeRepository = placeRepository;
        this.reservationRepository = reservationRepository;
        this.ownerRepository = ownerRepository;
    }

    private DateDto toDateDto(Reservation reservation) {
        return new DateDto(
                reservation.getArrival(),
                reservation.getDeparture()
        );
    }

    public List<DateDto> findAllReservedDates(Long placeId) {
        return reservationRepository.findByPlaceId(placeId).stream()
                .map(reservation -> toDateDto(reservation))
                .toList();
    }

    private PlaceDto toResponse(Place place) {
        return new PlaceDto(
                place.getId(),
                place.getName(),
                place.getTown(),
                place.getDescription(),
                place.getPlaceType(),
                place.getRating(),
                place.getOwner(),
                findAllReservedDates(place.getId())
        );
    }
    public List<PlaceDto> findByTown(String town) {
        return placeRepository.findByTownIgnoreCase(town).stream()
                .map(place -> toResponse(place))
                .toList();
    }

    public List<PlaceDto> findAllSortedByRating() {
        return placeRepository.findAllByOrderByRatingDesc()
                .stream()
                .map(place -> toResponse(place))
                .toList();
    }

    public PlaceDto findPlace(long id) {
        return toResponse(placeRepository.getById(id));
    }

    public PlaceDto create(CreatePlaceRequest request) {
        Owner owner = ownerRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Owner not found with id = " + request.getOwnerId()));

        Place place = new Place();
        place.setTown(request.getTown());
        place.setName(request.getName());
        place.setDescription(request.getDescription());
        place.setPlaceType(request.getPlaceType());
        place.setPricePerNight(request.getPricePerNight());
        place.setMaxGuests(request.getMaxGuests());
        place.setRating(request.getRating());
        place.setOwner(owner);

        Place saved = placeRepository.save(place);
        return toResponse(saved);
    }

    public void delete(Long id) {
        if (!placeRepository.existsById(id)) {
            throw new RuntimeException("Place not found with id = " + id);
        }
        placeRepository.deleteById(id);
    }
}
