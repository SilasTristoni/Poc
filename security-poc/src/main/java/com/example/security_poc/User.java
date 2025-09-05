package com.example.security_poc;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users") // Renomeia a tabela para "users"
@Data // Lombok: gera getters, setters, toString, etc.
@NoArgsConstructor // Lombok: gera um construtor sem argumentos
public class User {

    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}