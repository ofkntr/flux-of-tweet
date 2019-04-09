package com.interview.fluxoftweet.repository;


import com.interview.fluxoftweet.model.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {

    Optional<Tweet> findByTweetId(String tweetId);

    @Query(value = "select t from Tweet t join t.user u group by t.user order by u.createdAt asc")
    Optional<List<Tweet>> groupByUserTweetAssendingOrderByCreatedAt();

    @Query(value = "select t from Tweet t order by t.createdAt asc")
    Optional<List<Tweet>> getTweetsAssendingOrderByCreatedAt();

}
