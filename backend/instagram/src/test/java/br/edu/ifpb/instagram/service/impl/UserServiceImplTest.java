package br.edu.ifpb.instagram.service.impl;

import br.edu.ifpb.instagram.exception.FieldAlreadyExistsException;
import br.edu.ifpb.instagram.model.dto.UserDto;
import br.edu.ifpb.instagram.model.entity.UserEntity;
import br.edu.ifpb.instagram.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity existingUser;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        existingUser = new UserEntity();
        existingUser.setId(1L);
        existingUser.setFullName("Paulo Pereira");
        existingUser.setUsername("paulo");
        existingUser.setEmail("paulo@email.com");
        existingUser.setEncryptedPassword("encrypted123");
    }

    // ==== CREATE USER ====
    @Test
    void shouldCreateUserSuccessfully() {
        UserDto dto = new UserDto(null, "Lucas", "lucas", "lucas@email.com", "123456", null);

        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(userRepository.existsByUsername(dto.username())).thenReturn(false);
        when(passwordEncoder.encode(dto.password())).thenReturn("encrypted123");
        when(userRepository.save(any())).thenAnswer(inv -> {
            UserEntity u = inv.getArgument(0);
            u.setId(2L);
            return u;
        });

        UserDto result = userService.createUser(dto);

        assertNotNull(result);
        assertEquals(2L, result.id());
        assertEquals("Lucas", result.fullName());
        assertEquals("lucas", result.username());

        verify(userRepository, times(1)).save(any());
        verify(passwordEncoder, times(1)).encode(dto.password());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        UserDto dto = new UserDto(null, "Lucas", "lucas", existingUser.getEmail(), "123456", null);

        when(userRepository.existsByEmail(dto.email())).thenReturn(true);

        FieldAlreadyExistsException ex = assertThrows(FieldAlreadyExistsException.class,
                () -> userService.createUser(dto));
        assertEquals("E-email already in use.", ex.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        UserDto dto = new UserDto(null, "Lucas", existingUser.getUsername(), "lucas@email.com", "123456", null);

        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(userRepository.existsByUsername(dto.username())).thenReturn(true);

        FieldAlreadyExistsException ex = assertThrows(FieldAlreadyExistsException.class,
                () -> userService.createUser(dto));
        assertEquals("Username already in use.", ex.getMessage());

        verify(userRepository, never()).save(any());
    }

    // ==== FIND BY ID ====
    @Test
    void shouldReturnUserDtoWhenUserExists() {
        when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));

        UserDto dto = userService.findById(existingUser.getId());

        assertAll("UserDto",
                () -> assertEquals(existingUser.getId(), dto.id()),
                () -> assertEquals(existingUser.getFullName(), dto.fullName()),
                () -> assertEquals(existingUser.getUsername(), dto.username()),
                () -> assertEquals(existingUser.getEmail(), dto.email())
        );
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        Long invalidId = 99L;
        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.findById(invalidId));
        assertEquals("User not found with id: " + invalidId, ex.getMessage());
    }

    // ==== UPDATE USER ====
    @Test
    void shouldUpdateUserSuccessfully() {
        UserDto dto = new UserDto(1L, "Paulo Updated", "paulo", "paulo@email.com", "newpass", null);

        when(userRepository.findById(dto.id())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(dto.password())).thenReturn("newEncrypted");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserDto result = userService.updateUser(dto);

        assertEquals("Paulo Updated", result.fullName());
        verify(passwordEncoder, times(1)).encode(dto.password());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void shouldUpdateUserWithoutChangingPassword() {
        UserDto dto = new UserDto(1L, "Paulo Updated", "paulo", "paulo@email.com", null, null);

        when(userRepository.findById(dto.id())).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserDto result = userService.updateUser(dto);

        assertEquals("Paulo Updated", result.fullName());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void shouldThrowExceptionOnUpdateIfUserNotFound() {
        UserDto dto = new UserDto(99L, "Lucas", "lucas", "lucas@email.com", "123456", null);
        when(userRepository.findById(dto.id())).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.updateUser(dto));
        assertEquals("User not found with id: " + dto.id(), ex.getMessage());
    }

    @Test
    void shouldThrowExceptionOnUpdateIfDtoNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(null));
        assertEquals("UserDto or UserDto.id must not be null", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionOnUpdateIfIdNull() {
        UserDto dto = new UserDto(null, "Lucas", "lucas", "lucas@email.com", "123456", null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(dto));
        assertEquals("UserDto or UserDto.id must not be null", ex.getMessage());
    }

    // ==== DELETE USER ====
    @Test
    void shouldDeleteUserSuccessfully() {
        when(userRepository.existsById(existingUser.getId())).thenReturn(true);
        doNothing().when(userRepository).deleteById(existingUser.getId());

        assertDoesNotThrow(() -> userService.deleteUser(existingUser.getId()));

        verify(userRepository, times(1)).deleteById(existingUser.getId());
    }

    @Test
    void shouldThrowExceptionOnDeleteIfUserNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.deleteUser(99L));
        assertEquals("User not found with id: 99", ex.getMessage());
    }

    // ==== FIND ALL ====
    @Test
    void shouldReturnAllUsers() {
        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        user2.setFullName("Lucas");
        user2.setUsername("lucas");
        user2.setEmail("lucas@email.com");

        when(userRepository.findAll()).thenReturn(Arrays.asList(existingUser, user2));

        List<UserDto> users = userService.findAll();

        assertEquals(2, users.size());
        assertEquals("Paulo Pereira", users.get(0).fullName());
        assertEquals("Lucas", users.get(1).fullName());
    }
}
