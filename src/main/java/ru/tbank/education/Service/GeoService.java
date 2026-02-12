package ru.tbank.education.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.tbank.education.Config.GeoProperties;
import ru.tbank.education.DTO.GeoPlaceResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeoService {

    private final RestClient nominatimRestClient;
    private final GeoProperties props;

    // структура ответа Nominatim (нужны только эти поля)
    public record NominatimItem(String lat, String lon, String display_name) {}

    public List<GeoPlaceResponse> search(String q, Integer limit) {
        int lim = (limit == null || limit <= 0) ? props.getDefaultLimit() : limit;

        List<NominatimItem> items = nominatimRestClient.get()
                .uri(uri -> uri
                        .path("/search")
                        .queryParam("q", q)
                        .queryParam("format", "jsonv2")
                        .queryParam("limit", lim)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<NominatimItem>>() {});

        if (items == null) return List.of();

        return items.stream()
                .map(i -> GeoPlaceResponse.builder()
                        .displayName(i.display_name())
                        .lat(Double.parseDouble(i.lat()))
                        .lon(Double.parseDouble(i.lon()))
                        .build())
                .toList();
    }
}