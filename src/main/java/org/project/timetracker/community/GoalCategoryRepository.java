package org.project.timetracker.community;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoalCategoryRepository extends JpaRepository<GoalCategory, Long> {

    Optional<GoalCategory> findByTitle(String title);
}

