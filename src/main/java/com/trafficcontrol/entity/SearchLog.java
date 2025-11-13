package com.trafficcontrol.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "SearchLogs")
@Data
public class SearchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "performed_by", nullable = false)
    @JsonBackReference
    @JsonIgnore
    private User performedBy;

    @ManyToOne
    @JoinColumn(name = "person_id")
    @JsonBackReference
    private Person person;

    @Column(name = "searchType")
    private String searchType;

    @Column(name = "searchValue")
    private String searchValue;

    @Column(name = "resultSummary")
    private String resultSummary;

    @Column(name = "performedAt")
    private LocalDateTime performedAt = LocalDateTime.now();

    @JsonProperty("performedByUsername")
    public String getPerformedByUsername() {
        return performedBy != null ? performedBy.getUsername() : null;
    }
}
