package com.musicstore.bluevelvet.api.request;

import lombok.*;

@Getter @Setter @Builder @ToString
@NoArgsConstructor @AllArgsConstructor
public class CategoryRequest {
    private String name;
    private String image;
    private Long parentId;
    private Boolean enabled;
}
