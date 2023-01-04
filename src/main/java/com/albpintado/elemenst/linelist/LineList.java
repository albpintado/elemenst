package com.albpintado.elemenst.linelist;

import com.albpintado.elemenst.lineitem.LineItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;

@Entity
public class LineList {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "name", nullable = false)
    @Size(min = 1, max = 75, message = "Name value must be between {min} and {max} characters long")
    private String name;

    @OneToMany(mappedBy = "lineList", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<LineItem> lineItemList;

    public LineList() {
    }

    public LineList(LocalDate creationDate,
            @Size(min = 1, message = "{validation.name.size.too_short}") @Size(max = 25, message = "{validation.name.size.too_long}") String name,
            List<LineItem> lineItemList) {
        this.creationDate = creationDate;
        this.name = name;
        this.lineItemList = lineItemList;
    }

    public LineList(Long id, LocalDate creationDate,
            @Size(min = 1, message = "{validation.name.size.too_short}") @Size(max = 25, message = "{validation.name.size.too_long}") String name,
            List<LineItem> lineItemList) {
        this.id = id;
        this.creationDate = creationDate;
        this.name = name;
        this.lineItemList = lineItemList;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LineItem> getLineItemList() {
        return lineItemList;
    }

    public void setLineItemList(List<LineItem> lineItemList) {
        this.lineItemList = lineItemList;
    }
}
