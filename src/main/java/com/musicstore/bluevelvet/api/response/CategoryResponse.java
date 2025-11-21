package com.musicstore.bluevelvet.api.response;

import lombok.*;

import java.util.List;

@Getter @Setter @Builder @ToString
@NoArgsConstructor @AllArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;
    private String image;
    private Long parentId;
    private Boolean enabled;
}

