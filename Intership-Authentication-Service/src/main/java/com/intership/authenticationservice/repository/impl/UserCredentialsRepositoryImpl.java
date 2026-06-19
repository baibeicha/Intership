package com.intership.authenticationservice.repository.impl;

import com.intership.authenticationservice.model.entity.UserCredentials;
import com.intership.authenticationservice.repository.UserCredentialsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserCredentialsRepositoryImpl implements UserCredentialsRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<UserCredentials> userCredentialsRowMapper = (rs, rowNum) -> {
        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setId(rs.getLong("id"));
        userCredentials.setUserId(rs.getLong("user_id"));
        userCredentials.setUsername(rs.getString("username"));
        userCredentials.setPassword(rs.getString("password"));
        return userCredentials;
    };

    @Override
    public Optional<UserCredentials> findByUsername(String username) {
        try {
            UserCredentials userCredentials = jdbcTemplate.queryForObject(
                    SQL.SELECT,
                    userCredentialsRowMapper, username
            );
            return Optional.ofNullable(userCredentials);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public UserCredentials save(UserCredentials userCredentials) {
        if (userCredentials.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(SQL.INSERT, new String[]{"id"});
                ps.setLong(1, userCredentials.getUserId());
                ps.setString(2, userCredentials.getUsername());
                ps.setString(3, userCredentials.getPassword());
                return ps;
            }, keyHolder);

            Long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();
            userCredentials.setId(generatedId);
        } else {
            jdbcTemplate.update(
                    SQL.UPDATE,
                    userCredentials.getUsername(),
                    userCredentials.getPassword(),
                    userCredentials.getId()
            );
        }

        return userCredentials;
    }

    @Override
    public void deleteByUsername(String username) {
        jdbcTemplate.update(SQL.DELETE, username);
    }

    @Override
    public boolean existsByUsername(String username) {
        Boolean exists = jdbcTemplate.queryForObject(SQL.EXISTS, Boolean.class, username);
        return Boolean.TRUE.equals(exists);
    }

    private final static class SQL {
        public static String SELECT = "SELECT * FROM user_credentials WHERE username = ?";
        public static String INSERT = "INSERT INTO user_credentials (user_id, username, password) VALUES (?, ?, ?)";
        public static String UPDATE = "UPDATE user_credentials SET username = ?, password = ? WHERE id = ?";
        public static String EXISTS = "SELECT EXISTS(SELECT 1 FROM user_credentials WHERE username = ?)";
        public static String DELETE = "DELETE FROM user_credentials WHERE username = ?";
    }
}
