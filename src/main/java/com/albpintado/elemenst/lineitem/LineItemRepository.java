package com.albpintado.elemenst.lineitem;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LineItemRepository extends JpaRepository<LineItem, Long> {
    public List<LineItem> findAllByLineListId(Long id, Sort sort);
}
