package com.islamnizami.fleettrack.model.specification;

import com.islamnizami.fleettrack.model.entity.Driver;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public final class DriverSpecification {

    private DriverSpecification() {}

    public static Specification<Driver> hasActive(Boolean active) {
        return (root, query, cb) ->
                active == null ? cb.conjunction()
                        : cb.equal(root.get("active"), active);
    }

    public static Specification<Driver> matchesText(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return cb.conjunction();
            }
            String pattern = "%" + search.toLowerCase(Locale.ROOT) + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("firstName")), pattern),
                    cb.like(cb.lower(root.get("lastName")), pattern),
                    cb.like(cb.lower(root.get("email")), pattern),
                    cb.like(cb.lower(root.get("licenseNumber")), pattern)
            );
        };
    }
}
