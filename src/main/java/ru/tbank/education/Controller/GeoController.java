package ru.tbank.education.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.tbank.education.DTO.GeoPlaceResponse;
import ru.tbank.education.Service.GeoService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/geo")
public class GeoController {

    private final GeoService geoService;

    // GET /api/geo/search?q=Berlin
    @GetMapping("/search")
    public List<GeoPlaceResponse> search(@RequestParam String q,
                                         @RequestParam(required = false) Integer limit) {
        return geoService.search(q, limit);
    }
}
