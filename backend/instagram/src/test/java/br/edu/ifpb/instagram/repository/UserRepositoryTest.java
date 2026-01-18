package br.edu.ifpb.instagram.repository;

import br.edu.ifpb.instagram.model.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserByUsername() {
        UserEntity user = new UserEntity();
        user.setFullName("Lucas");
        user.setUsername("lucas123");
        user.setEmail("lucas@email.com");
        user.setEncryptedPassword("123");

        userRepository.save(user);

        assertTrue(userRepository.findByUsername("lucas123").isPresent());
    }

    @Test
    void shouldDeleteUser() {
        UserEntity user = new UserEntity();
        user.setFullName("Teste");
        user.setUsername("teste123");
        user.setEmail("teste@email.com");
        user.setEncryptedPassword("123");

        UserEntity saved = userRepository.save(user);
        userRepository.delete(saved);

        assertTrue(userRepository.findById(saved.getId()).isEmpty());
    }
}
