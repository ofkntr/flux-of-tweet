package com.interview.fluxoftweet.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Builder
@Table(name = "tweets")
public class Tweet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @JsonProperty("id_str")
    private String tweetId;

    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT, pattern = "EEE MMM dd HH:mm:ss ZZZZZ yyyy", timezone = "CET")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date createdAt;

    @JsonProperty("text")
    private String text;

    @JsonProperty("user")
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

}
