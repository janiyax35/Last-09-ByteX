package com.bytex.customercaresystem.repository;

import com.bytex.customercaresystem.model.Ticket;
import com.bytex.customercaresystem.model.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class TicketSpecification {

    public static Specification<Ticket> searchByKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return criteriaBuilder.conjunction(); // Returns an always-true predicate
            }

            String pattern = "%" + keyword.toLowerCase() + "%";

            Join<Ticket, User> customerJoin = root.join("customer");
            Join<Ticket, User> assignedToJoin = root.join("assignedTo", jakarta.persistence.criteria.JoinType.LEFT);

            Predicate p1 = criteriaBuilder.like(criteriaBuilder.lower(root.get("subject")), pattern);
            Predicate p2 = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern);
            Predicate p3 = criteriaBuilder.like(criteriaBuilder.lower(customerJoin.get("fullName")), pattern);
            Predicate p4 = criteriaBuilder.like(criteriaBuilder.lower(assignedToJoin.get("fullName")), pattern);

            return criteriaBuilder.or(p1, p2, p3, p4);
        };
    }
}
