package com.blps_lab1.demo.services.api;

import com.blps_lab1.demo.data.tables.Place;
import com.blps_lab1.demo.dto.CreatePlaceRequest;
import com.blps_lab1.demo.dto.PlaceDto;

import java.util.List;

public interface IPlaceService {
    List<PlaceDto> findByTown(String town);
    List<PlaceDto> findAllSortedByRating();
    PlaceDto findPlace(long id);
    PlaceDto create(CreatePlaceRequest request);
    void delete(Long id);
    Place findEntityById(Long id);
}
