package controller;

import data.tables.Place;
import dto.PlaceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.PlaceService;

import java.util.List;

@RestController
@RequestMapping("/places")
public class MainController {
    private final PlaceService placeService;

    public MainController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping
    public List<Place> getPlacesByTown(@RequestParam(required = false) String town) {
        return placeService.findAllPlaces(town);
    }

    @GetMapping
    public List<Place> getPlacesByRating(@RequestParam(required = false) double rating) {
        return placeService.findAllPlaces(rating);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaceResponse> getById(@PathVariable Long id) {
        PlaceResponse response = placeService.findPlace(id);
        return ResponseEntity.ok(response);
    }
}
