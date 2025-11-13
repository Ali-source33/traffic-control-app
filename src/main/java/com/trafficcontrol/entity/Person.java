package com.trafficcontrol.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "persons")
@Data
@NoArgsConstructor
@JsonPropertyOrder({
    "id", 
    "address", 
    "birthDate",
    "firstName",
    "lastName",
    "tcKimlik",
    "hasOutstandingFines",
    "isWanted",
    "searchLogs"
})
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "birthDate")
    @JsonProperty("birthDate")
    private LocalDate birthDate;

    @Column(name = "firstName")
    @JsonProperty("firstName")
    private String firstName;

    @Column(name = "lastName")
    @JsonProperty("lastName")
    private String lastName;

    @Column(name = "address")
    private String address;

    @Column(name = "tcKimlik")
    @JsonProperty("tcKimlik")
    private String tcKimlik;

    @Column(name = "hasOutstandingFines")
    @JsonProperty("hasOutstandingFines")
    private Boolean hasOutstandingFines;

    @Column(name = "isWanted")
    @JsonProperty("isWanted")
    private Boolean isWanted;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<SearchLog> searchLogs;
}
