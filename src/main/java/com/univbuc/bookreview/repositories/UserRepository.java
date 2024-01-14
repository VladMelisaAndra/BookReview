package com.univbuc.bookreview.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.univbuc.bookreview.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
