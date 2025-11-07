package com.example.basicbookstoreprojectnew.validation;

import com.example.basicbookstoreprojectnew.exception.ForbiddenActionException;

public class AccessValidator {

    public static void validateOwnership(Long ownerId, Long currentUserId, String entity) {
        if (!ownerId.equals(currentUserId)) {
            throw new ForbiddenActionException("You don`t have access to this: " + entity + "!");
        }
    }
}

