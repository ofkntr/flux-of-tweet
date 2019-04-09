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
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @JsonProperty("id_str")
    private String twitterUserId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("profile_image_url")
    private String profileImageUrl;

    @JsonProperty("profile_image_url_https")
    private String profileImageUrlHttps;

    @JsonProperty("screen_name")
    private String screenName;

    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT, pattern = "EEE MMM dd HH:mm:ss ZZZZZ yyyy", timezone = "CET")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date createdAt;

    @JsonProperty("location")
    private String location;

}
