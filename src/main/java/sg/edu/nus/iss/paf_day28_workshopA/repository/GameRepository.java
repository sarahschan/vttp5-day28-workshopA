package sg.edu.nus.iss.paf_day28_workshopA.repository;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;

@Repository
public class GameRepository {
    
    @Autowired
    MongoTemplate mongoTemplate;


    // db.games.aggregate([
    //     { $match: { gid: 1 } },
    //     { $lookup: {
    //         from: 'comments',
    //         foreignField: 'gid',
    //         localField: 'gid',
    //         as: 'reviews' 
    //     }},
    //     { $unwind: '$reviews'},
    //     { $group: {
    //         _id: '$gid',
    //         game_id: {$first: '$gid'},
    //         name: {$first: '$name'},
    //         year: {$first: '$year'},
    //         rank: {$first: '$ranking'},
    //         average: {$avg: '$reviews.rating'},
    //         users_rated: {$first: '$users_rated'},
    //         url: {$first: '$url'},
    //         thumbnail: {$first: '$image'},
    //         reviews: {
    //             $push: {
    //                 review_id: '$reviews.c_id',
    //                 user: '$reviews.user',
    //                 rating: '$reviews.rating',
    //                 text: '$reviews.c_text'
    //             }
    //         }
    //     }},
    //     { $project: {
    //         _id: 0
    //     }}
    // ])
    public List<Document> getReviewsByGameId(int gameId) {

        Criteria criteria = Criteria.where("gid").is(gameId);
        MatchOperation matchGID = Aggregation.match(criteria);

        LookupOperation lookupComments = Aggregation.lookup("comments", "gid", "gid", "reviews");

        UnwindOperation unwindReviews = Aggregation.unwind("reviews");

        GroupOperation groupOperation = Aggregation.group("gid")
            .first("gid").as("game_id")
            .first("name").as("name")
            .first("year").as("year")
            .first("ranking").as("rank")
            .avg("reviews.rating").as("average")
            .first("users_rated").as("users_rated")
            .first("url").as("url")
            .first("image").as("thumbnail")
            .push(
                new BasicDBObject()
                .append("review_id", "$reviews.c_id")
                .append("user", "$reviews.user")
                .append("rating", "$reviews.rating")
                .append("text", "$reviews.c_text"))
                .as("comments");

        ProjectionOperation removeId = Aggregation.project()
            .andExclude("_id");

        Aggregation pipeline = Aggregation.newAggregation(matchGID, lookupComments, unwindReviews, groupOperation, removeId);

        return mongoTemplate.aggregate(pipeline, "games", Document.class).getMappedResults();

    }


    // db.games.aggregate([
    //     { $lookup: {
    //         from: 'comments',
    //         foreignField: 'gid',
    //         localField: 'gid',
    //         as: 'reviews'
    //     }},
    //     { $unwind: '$reviews' },
    //     { $sort: {'reviews.rating': -1}},
    //     { $group: {
    //         _id: '$gid',
    //         game_id: {$first: '$gid'},
    //         name: {$first: '$name'},
    //         rating: {$first: '$reviews.rating'},
    //         user: {$first: '$reviews.user' },
    //         comment: {$first: '$reviews.c_text'},
    //         review_id: {$first: '$reviews.c_id'}
    //     }}
    // ])
    public List<Document> getHighestReviews() {

        LookupOperation lookupComments = Aggregation.lookup("comments", "gid", "gid", "reviews");

        UnwindOperation unwindReviews = Aggregation.unwind("reviews");

        SortOperation sortByRating = Aggregation.sort(Sort.Direction.DESC, "$reviews.rating");

        GroupOperation groupOperation = Aggregation.group("gid")
            .first("gid").as("game_id")
            .first("name").as("name")
            .first("$reviews.rating").as("rating")
            .first("$reviews.user").as("user")
            .first("$reviews.c_text").as("comment")
            .first("$reviews.c_id").as("review_id");

        ProjectionOperation removeId = Aggregation.project().andExclude("_id");

        Aggregation pipeline = Aggregation.newAggregation(lookupComments, unwindReviews, sortByRating, groupOperation, removeId);

        return mongoTemplate.aggregate(pipeline, "games", Document.class).getMappedResults();
    }


    // db.games.aggregate([
    //     { $lookup: {
    //         from: 'comments',
    //         foreignField: 'gid',
    //         localField: 'gid',
    //         as: 'reviews'
    //     }},
    //     { $unwind: '$reviews' },
    //     { $sort: {'reviews.rating': 1}},
    //     { $group: {
    //         _id: '$gid',
    //         game_id: {$first: '$gid'},
    //         name: {$first: '$name'},
    //         rating: {$first: '$reviews.rating'},
    //         user: {$first: '$reviews.user' },
    //         comment: {$first: '$reviews.c_text'},
    //         review_id: {$first: '$reviews.c_id'}
    //     }}
    // ])
    public List<Document> getLowestReviews() {

        LookupOperation lookupComments = Aggregation.lookup("comments", "gid", "gid", "reviews");

        UnwindOperation unwindReviews = Aggregation.unwind("reviews");

        SortOperation sortByRating = Aggregation.sort(Sort.Direction.ASC, "$reviews.rating");

        GroupOperation groupOperation = Aggregation.group("gid")
            .first("gid").as("game_id")
            .first("name").as("name")
            .first("$reviews.rating").as("rating")
            .first("$reviews.user").as("user")
            .first("$reviews.c_text").as("comment")
            .first("$reviews.c_id").as("review_id");

        ProjectionOperation removeId = Aggregation.project().andExclude("_id");

        Aggregation pipeline = Aggregation.newAggregation(lookupComments, unwindReviews, sortByRating, groupOperation, removeId);

        return mongoTemplate.aggregate(pipeline, "games", Document.class).getMappedResults();
    }

}
