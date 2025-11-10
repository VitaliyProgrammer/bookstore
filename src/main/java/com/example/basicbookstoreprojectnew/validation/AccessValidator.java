package com.example.basicbookstoreprojectnew.validation;

import com.example.basicbookstoreprojectnew.exception.ForbiddenActionException;
import com.example.basicbookstoreprojectnew.model.RoleName;
import com.example.basicbookstoreprojectnew.model.User;

public class AccessValidator {

    public static void validateOwnership(User user, Long ownerId) {

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getRoleName() == RoleName.ADMIN);

        if (!isAdmin && !user.getId().equals(ownerId)) {
            throw new ForbiddenActionException("You don't have access to this order!");
        }
    }
}

