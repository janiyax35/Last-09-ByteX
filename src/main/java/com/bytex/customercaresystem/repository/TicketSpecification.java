package com.bytex.customercaresystem.repository;

import com.bytex.customercaresystem.model.Ticket;
import com.bytex.customercaresystem.model.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

public class TicketSpecification {

    public static Specification<Ticket> findByCriteria(String keyword, User customer, User assignedTo) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (customer != null) {
                predicates.add(criteriaBuilder.equal(root.get("customer"), customer));
            }

            if (assignedTo != null) {
                predicates.add(criteriaBuilder.equal(root.get("assignedTo"), assignedTo));
            }

            if (keyword != null && !keyword.trim().isEmpty()) {
                Predicate searchPredicate;
                try {
                    Long ticketId = Long.parseLong(keyword);
                    searchPredicate = criteriaBuilder.equal(root.get("ticketId"), ticketId);
                } catch (NumberFormatException e) {
                    String pattern = "%" + keyword.toLowerCase() + "%";
                    searchPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("subject")), pattern);
                }
                predicates.add(searchPredicate);
            }

            query.orderBy(criteriaBuilder.desc(root.get("updatedAt")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
