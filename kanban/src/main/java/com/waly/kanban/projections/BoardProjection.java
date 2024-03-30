package com.waly.kanban.projections;

public interface BoardProjection {

    Long getId();
    String getTitle();
    Integer getTotalCards();
}
