package com.musicstore.bluevelvet.api.response;

import lombok.*;

import java.util.List;

@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class FullCategoryResponse extends CategoryResponse {
    private List<CategoryResponse> children;
}
