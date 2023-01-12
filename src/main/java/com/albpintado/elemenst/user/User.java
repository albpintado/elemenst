package com.albpintado.elemenst.user;

import com.albpintado.elemenst.linelist.LineList;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_name", nullable = false, unique = true)
    @Size(min = 1, max = 25, message = "User name value must be between {min} and {max} characters long")
    private String userName;

    @Column(name = "password", nullable = false)
    @JsonIgnore
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<LineList> lineLists;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<LineList> getLineLists() {
        return lineLists;
    }

    public void setLineLists(List<LineList> lineLists) {
        this.lineLists = lineLists;
    }
}
