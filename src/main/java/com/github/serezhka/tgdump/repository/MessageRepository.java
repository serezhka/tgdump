package com.github.serezhka.tgdump.repository;

import com.github.serezhka.tgdump.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Sergei Fedorov (serezhka@xakep.ru)
 */
@Repository
public interface MessageRepository extends JpaRepository<Chat, Integer> {

}
