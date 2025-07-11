package com.kshitij.IntervuLog.spec;

import jakarta.persistence.criteria.*;
import com.kshitij.IntervuLog.model.InternshipExperience;
import com.kshitij.IntervuLog.enums.ExperienceStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ExperienceSpecification {

    public static Specification<InternshipExperience> filterBy(
            Integer year, String name, List<String> company, List<String> role,
            List<String> type, Integer ctcMin, Integer ctcMax, Integer stipendMin, Integer stipendMax) {

        return (Root<InternshipExperience> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("status"), ExperienceStatus.APPROVED));

            if (year != null) {
                Expression<Integer> yearExpression = cb.function("date_part", Integer.class, cb.literal("year"), root.get("processDate"));
                predicates.add(cb.equal(yearExpression, year));
            }

            if (name != null && !name.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("fullName")), "%" + name.toLowerCase() + "%"));
            }

            if (company != null && !company.isEmpty()) {
                Join<Object, Object> companyJoin = root.join("company", JoinType.LEFT);
                predicates.add(companyJoin.get("name").in(company));
            }

            if (role != null && !role.isEmpty()) {
                predicates.add(root.get("jobProfile").in(role));
            }

            if (type != null && !type.isEmpty()) {
                predicates.add(root.get("offerType").in(type));
            }

            if (ctcMin != null) {
                predicates.add(cb.ge(root.get("ctc"), ctcMin));
            }

            if (ctcMax != null) {
                predicates.add(cb.le(root.get("ctc"), ctcMax));
            }

            if (stipendMin != null) {
                predicates.add(cb.ge(root.get("internshipStipend"), stipendMin));
            }

            if (stipendMax != null) {
                predicates.add(cb.le(root.get("internshipStipend"), stipendMax));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
