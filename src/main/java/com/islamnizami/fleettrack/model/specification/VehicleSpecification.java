package com.islamnizami.fleettrack.model.specification;


import com.islamnizami.fleettrack.model.entity.Vehicle;
import com.islamnizami.fleettrack.model.enums.VehicleStatus;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public final class VehicleSpecification {

    private VehicleSpecification() {}

    public static Specification<Vehicle> hasStatus(VehicleStatus status){
        return (root, query, cb) ->
                status == null ? cb.conjunction()
                : cb.equal(root.get("status"),status);
    }

    public static Specification<Vehicle> hasModelYear(Integer modelYear){
        return (root, query, cb) ->
                modelYear == null ? cb.conjunction()
                : cb.equal(root.get("modelYear"), modelYear);
    }

    public static Specification<Vehicle> hasDriver(Long driverId){
        return (root, query, cb) ->
                driverId == null ? cb.conjunction()
                : cb.equal(root.get("driver").get("id"), driverId);
    }

    public static Specification<Vehicle> matchesText(String search){
        return (root,query,cb) -> {
            if(search == null || search.isBlank()){
                return cb.conjunction();
            }

            String pattern = "%" + search.toLowerCase(Locale.ROOT) + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("make")), pattern),
                    cb.like(cb.lower(root.get("model")), pattern),
                    cb.like(cb.lower(root.get("licensePlate")), pattern)
            );
        };
    }

    public static Specification<Vehicle> fetchDriver(){
        return (root, query, cb) -> {
            if (query != null && !Long.class.equals(query.getResultType())
                    && !long.class.equals(query.getResultType())) {
                root.fetch("driver", JoinType.LEFT);
            }
            return cb.conjunction();
        };
    }

}
