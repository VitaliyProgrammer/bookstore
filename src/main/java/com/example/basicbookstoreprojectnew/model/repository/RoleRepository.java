package com.example.basicbookstoreprojectnew.model.repository;

import com.example.basicbookstoreprojectnew.model.Role;
import com.example.basicbookstoreprojectnew.model.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(RoleName roleName);
}
