package com.pvt152.StudentLoppet.controller;

import com.pvt152.StudentLoppet.dto.UniversityScoreDTO;
import com.pvt152.StudentLoppet.model.University;
import com.pvt152.StudentLoppet.service.ActivityService;
import com.pvt152.StudentLoppet.service.LeaderboardService;
import com.pvt152.StudentLoppet.service.UniversityService;
import com.pvt152.StudentLoppet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = "/api/universities")
public class UniversityController {

    @Autowired
    private UniversityService universityService;

    @GetMapping(path = "/representation")
    public @ResponseBody Map<String, String> getUniversityRepresentations() {
        Comparator<String> caseInsensitiveOrder = String.CASE_INSENSITIVE_ORDER;

        Map<String, String> universityMap = Stream.of(University.values())
                .collect(Collectors.toMap(
                        University::name,
                        University::getDisplayName,
                        (oldValue, newValue) -> oldValue,
                        () -> new TreeMap<>(caseInsensitiveOrder)));

        return universityMap;
    }

    @GetMapping(path = "/scoreboard")
    public @ResponseBody ResponseEntity<List<UniversityScoreDTO>> getUniversityLeaderboard() {
        List<UniversityScoreDTO> scores = universityService.calculateUniversityScores();
        return ResponseEntity.ok(scores);
    }

    @GetMapping("/universitiesByUserCount")
    public ResponseEntity<Map<String, Integer>> getUniversitiesByUserCount() {
        Map<String, Integer> universityUserCounts = universityService.countUsersByUniversity();
        return ResponseEntity.ok(universityUserCounts);
    }

    @GetMapping("/universitiesByDistance")
    public ResponseEntity<Map<String, Double>> getUniversitiesByDistance() {
        Map<String, Double> universityDistances = universityService.sumDistanceByUniversity();
        return ResponseEntity.ok(universityDistances);
    }

    @GetMapping(path = "/universityRank/{university}")
    public ResponseEntity<?> getUniversityRank(@PathVariable University university) {
        try {
            int rank = universityService.getUniversityRank(university);
            return ResponseEntity.ok(rank);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}