package sg.edu.nus.iss.paf_day28_workshopA.service;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import sg.edu.nus.iss.paf_day28_workshopA.repository.GameRepository;

@Service
public class GameService {
    
    @Autowired
    GameRepository gameRepository;

    public Optional<Document> getReviewsByGameId(int gameId){
        
        List<Document> results = gameRepository.getReviewsByGameId(gameId);

        if (results.size() <= 0) {
            return Optional.empty();
        }

        Document reviews = results.get(0)
            .append("timestamp", LocalDateTime.now().toString());

        return Optional.of(reviews);
    }


    public JsonObject getHighestReviews(){

        List<Document> highestReviews = gameRepository.getHighestReviews();

        JsonArrayBuilder gamesArrayBuilder = Json.createArrayBuilder();

        for (Document d : highestReviews){
            String jsonString = d.toJson();
            JsonObject gamesObject = Json.createReader(new StringReader(jsonString)).readObject();
            gamesArrayBuilder.add(gamesObject);
        }

        JsonObject result = Json.createObjectBuilder()
            .add("rating", "highest")
            .add("games", gamesArrayBuilder.build())
            .add("timestamp", LocalDateTime.now().toString())
            .build();

        return result;

    }


    public JsonObject getLowestReviews(){

        List<Document> highestReviews = gameRepository.getLowestReviews();

        JsonArrayBuilder gamesArrayBuilder = Json.createArrayBuilder();

        for (Document d : highestReviews){
            String jsonString = d.toJson();
            JsonObject gamesObject = Json.createReader(new StringReader(jsonString)).readObject();
            gamesArrayBuilder.add(gamesObject);
        }

        JsonObject result = Json.createObjectBuilder()
            .add("rating", "lowest")
            .add("games", gamesArrayBuilder.build())
            .add("timestamp", LocalDateTime.now().toString())
            .build();

        return result;

    }
}
