package controller;

import dto.PlaceDto;
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
    public List<PlaceDto> getPlacesByTown(@RequestParam(required = false) String town) {
        return placeService.findByTown(town);
    }

    @GetMapping("/rating")
    public List<PlaceDto> getPlacesByRating() {
        return placeService.findAllSortedByRating();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaceDto> getById(@PathVariable Long id) {
        PlaceDto response = placeService.findPlace(id);
        return ResponseEntity.ok(response);
    }
}
