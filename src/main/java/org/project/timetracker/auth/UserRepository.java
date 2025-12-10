package org.project.timetracker.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByLoginId(String loginId);

    Optional<User> findByLoginId(String loginId);

    List<User> findAllByGoalCategoryId(Long goalCategoryId);

    List<User> findAllByClassifiedGoal(String classifiedGoal);
}
