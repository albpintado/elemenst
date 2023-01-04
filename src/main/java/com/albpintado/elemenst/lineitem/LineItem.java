package com.albpintado.elemenst.lineitem;

import com.albpintado.elemenst.linelist.LineList;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

@Entity
public class LineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;

    @ManyToOne
    @JoinColumn(name = "line_list_id", nullable = false)
    private LineList lineList;

    @Column(name = "content", nullable = false)
    @Size(min = 1, max = 75, message = "Content value must be between {min} and {max} characters long")
    private String content;

    @Column(name = "is_completed", nullable = false, columnDefinition = "boolean default false")
    private boolean isCompleted;

    @Column(name = "is_pinned", nullable = false, columnDefinition = "boolean default false")
    private boolean isPinned;

    public LineItem() {
    }

    public LineItem(LocalDate creationDate, LineList lineList,
            @Size(min = 1, message = "{validation.name.size.too_short}") @Size(max = 25, message = "{validation.name.size.too_long}") String content) {
        this.creationDate = creationDate;
        this.lineList = lineList;
        this.content = content;
    }

    public LineItem(Long id, LocalDate creationDate, LineList lineList,
            @Size(min = 1, message = "{validation.name.size.too_short}") @Size(max = 25, message = "{validation.name.size.too_long}") String content,
            boolean isCompleted, boolean isPinned) {
        this.id = id;
        this.creationDate = creationDate;
        this.lineList = lineList;
        this.content = content;
        this.isCompleted = isCompleted;
        this.isPinned = isPinned;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LineList getLineList() {
        return lineList;
    }

    public void setLineList(LineList lineList) {
        this.lineList = lineList;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean getIsCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean getIsPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }
}
