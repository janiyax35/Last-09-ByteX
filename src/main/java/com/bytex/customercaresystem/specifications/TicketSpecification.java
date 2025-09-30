package com.bytex.customercaresystem.specifications;

import com.bytex.customercaresystem.models.Ticket;
import com.bytex.customercaresystem.models.enums.TicketStatus;
import org.springframework.data.jpa.domain.Specification;

public class TicketSpecification {

    public static Specification<Ticket> hasKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String likePattern = "%" + keyword.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("subject")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern)
            );
        };
    }

    public static Specification<Ticket> hasStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null || status.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            try {
                TicketStatus ticketStatus = TicketStatus.valueOf(status.toUpperCase());
                return criteriaBuilder.equal(root.get("status"), ticketStatus);
            } catch (IllegalArgumentException e) {
                // If status is invalid, return a specification that finds nothing
                return criteriaBuilder.disjunction();
            }
        };
    }
}