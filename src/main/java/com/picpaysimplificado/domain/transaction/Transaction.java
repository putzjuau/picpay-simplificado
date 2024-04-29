package com.picpaysimplificado.domain.transaction;

import com.picpaysimplificado.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name="transactions")
@Getter
@Setter
@Table(name="transactions")
@AllArgsConstructor
@EqualsAndHashCode(of = "id")

public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private BigDecimal amount;
    @ManyToOne //Um usuario, varias transacoes
    @JoinColumn(name="sender_id")
    private User sender;
    @ManyToOne //muito utilizando em bancos relacionais
    @JoinColumn(name="receiver_id")
    private User receiver;
    private LocalDateTime timestamp;
}
