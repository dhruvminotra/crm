package com.veyora.crm.repository;

import com.veyora.crm.constant.RoleType;
import com.veyora.crm.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * The user-dashboard search behind tf-main UserBean.searchDeskUsers /
     * UserSearchQueryVO: filter by roletype, optional activated flag and
     * optional name-or-email prefix (uemail), oldest first.
     */
    @Query("select u from User u where (:roleType is null or u.roleType = :roleType) "
            + "and (:activated is null or u.activated = :activated) "
            + "and (:prefix is null or lower(u.name) like :prefix or lower(u.email) like :prefix) "
            + "order by u.userId")
    List<User> searchUsers(@Param("roleType") RoleType roleType,
                           @Param("activated") String activated,
                           @Param("prefix") String prefix);
}
