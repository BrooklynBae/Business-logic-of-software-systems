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

    @GetMapping("/town/{town}")
    public List<PlaceResponse> getPlacesByTown(@RequestParam(required = false) String town) {
        return placeService.findByTown(town);
    }

    @GetMapping("/rating")
    public List<PlaceResponse> getPlacesByRating() {
        return placeService.findAllSortedByRating();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaceResponse> getById(@PathVariable Long id) {
        PlaceResponse response = placeService.findPlace(id);
        return ResponseEntity.ok(response);
    }
}
