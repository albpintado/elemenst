package com.albpintado.elemenst.linelist;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LineListRepository extends JpaRepository<LineList, Long> {
    public List<LineList> findAllByUserId(Long id, Sort sort);
}
