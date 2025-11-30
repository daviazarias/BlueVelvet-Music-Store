package com.musicstore.bluevelvet.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity @Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@Table(schema= "bluevelvet", name = "category")
public class Category implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String image;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> children;

    @Column(name="is_root")
    private Boolean isRoot;

    private Boolean enabled;
}
