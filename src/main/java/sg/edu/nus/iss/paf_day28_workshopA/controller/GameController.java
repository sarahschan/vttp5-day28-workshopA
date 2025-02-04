package sg.edu.nus.iss.paf_day28_workshopA.controller;

import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import sg.edu.nus.iss.paf_day28_workshopA.service.GameService;

@RestController
@RequestMapping("")
public class GameController {

    @Autowired
    GameService gameService;

    @GetMapping(path = "/game/{gameId}/reviews", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getReviewsByGameId(@PathVariable int gameId) {
        
        Optional<Document> review = gameService.getReviewsByGameId(gameId);

        if (review.isEmpty()) {
            JsonObject errorMsg = Json.createObjectBuilder()
                .add("error", "Game ID not found: %s".formatted(gameId))
                .build();
            return ResponseEntity.status(404).body(errorMsg.toString());
        }

        return ResponseEntity.status(200).body(review.get().toJson());

    }


    @GetMapping(path = "/games/highest", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getHighestReviews(){
        
        JsonObject result = gameService.getHighestReviews();

        return ResponseEntity.status(200).body(result.toString());
    }


    @GetMapping(path = "/games/lowest", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getLowestReviews(){
        
        JsonObject result = gameService.getLowestReviews();

        return ResponseEntity.status(200).body(result.toString());

    }

}
