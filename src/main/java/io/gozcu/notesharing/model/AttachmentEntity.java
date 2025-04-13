package io.gozcu.notesharing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class AttachmentEntity {
    @Column(name = "attachment_id")
    private Long id;

    @Column(name = "attachment_name")
    private String name;

    @Column(name = "attachment_size")
    private Integer size;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    // Constructors
    public AttachmentEntity() {
    }

    public AttachmentEntity(Long id, String name, Integer size) {
        this.id = id;
        this.name = name;
        this.size = size;
    }
}