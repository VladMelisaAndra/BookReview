package com.univbuc.bookreview.repositories.security;

import com.univbuc.bookreview.domain.security.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
}