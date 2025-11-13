package com.trafficcontrol.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Roles")
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;
}
