package com.blps_lab1.demo.services;

import com.blps_lab1.demo.data.repository.PlaceRepository;
import com.blps_lab1.demo.data.tables.Place;
import com.blps_lab1.demo.dto.CreatePlaceRequest;
import com.blps_lab1.demo.dto.PlaceDto;
import com.blps_lab1.demo.exception.NotFoundException;
import com.blps_lab1.demo.services.api.IOwnerService;
import com.blps_lab1.demo.services.api.IPlaceService;
import com.blps_lab1.demo.services.api.IReservationService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PlaceService implements IPlaceService {

    private final PlaceRepository placeRepository;
    private final IOwnerService ownerService;
    private final IReservationService reservationService;

    public PlaceService(PlaceRepository placeRepository, IOwnerService ownerService, @Lazy IReservationService reservationService) {
        this.placeRepository = placeRepository;
        this.ownerService = ownerService;
        this.reservationService = reservationService;
    }

    private PlaceDto toResponse(Place place) {
        return PlaceDto.builder()
                .id(place.getId())
                .name(place.getName())
                .town(place.getTown())
                .description(place.getDescription())
                .placeType(place.getPlaceType())
                .rating(place.getRating())
                .owner(place.getOwner())
                .petsAllowed(place.getPetsAllowed())
                .availableDates(reservationService.findAllReservedDates(place.getId()))
                .build();
    }

    @Override
    @PreAuthorize("permitAll()")
    public List<PlaceDto> findByTown(String town) {
        if (town == null || town.isBlank()) {
            throw new IllegalArgumentException("Town name cannot be empty");
        }
        return placeRepository.findByTownIgnoreCase(town).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @PreAuthorize("permitAll()")
    public List<PlaceDto> findAllSortedByRating() {
        return placeRepository.findAllByOrderByRatingDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @PreAuthorize("permitAll()")
    public PlaceDto findPlace(long id) {
        return toResponse(findEntityById(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasAuthority('PERM_MANAGE_OWN_PLACES')")
    public PlaceDto create(CreatePlaceRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body cannot be null");
        }
        if (request.getOwnerId() == null) {
            throw new IllegalArgumentException("Owner ID cannot be null");
        }

        Place place = new Place();
        place.setTown(request.getTown());
        place.setName(request.getName());
        place.setDescription(request.getDescription());
        place.setPlaceType(request.getPlaceType());
        place.setPricePerNight(request.getPricePerNight());
        place.setMaxGuests(request.getMaxGuests());
        place.setRating(request.getRating());
        place.setPetsAllowed(request.getPetsAllowed() == null || request.getPetsAllowed());
        place.setOwner(ownerService.findEntityById(request.getOwnerId()));

        Place saved = placeRepository.save(place);
        return toResponse(saved);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasAuthority('PERM_MANAGE_USERS') or " + "@placeRepository.findById(#a0).orElse(null)?.getOwner()?.getLogin() == authentication.name")
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        findEntityById(id);
        placeRepository.deleteById(id);
    }

    @Override
    public Place findEntityById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        return placeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Place not found with id = " + id));
    }
}
