package com.picpaysimplificado.service;

import com.picpaysimplificado.domain.transaction.Transaction;
import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.dtos.TransactionDTO;
import com.picpaysimplificado.respositories.TransactionReposirty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TransactionService {
    @Autowired
    private TransactionReposirty repository;
    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate; // classe oferecida pelo Spring para fazer comunicacao entre servicos em http

    @Autowired
    private NotificationService notificationService;
    public Transaction createTransaction(TransactionDTO transaction) throws Exception { //esse tipo é os dados que vou receber no payload
        User sender = this.userService.findUserById(transaction.senderId());
        User receiver = this.userService.findUserById(transaction.receiverId());

        userService.validateTransaction(sender, transaction.value());
    boolean isAuthorized = this.authorizeTransactrion(sender, transaction.value());
        if (!isAuthorized){
            throw new Exception("Transação não autorizada!");
        }

        Transaction newtransaction = new Transaction();
        newtransaction.setAmount(transaction.value());
        newtransaction.setSender(sender);
        newtransaction.setReceiver(receiver);
        newtransaction.setTimestamp(LocalDateTime.now());

        sender.setBalance(sender.getBalance().subtract(transaction.value()));
        receiver.setBalance(receiver.getBalance().add(transaction.value()));

        //salvando no repositorio a nova transacao, salvando o usuario e salvando o recebedor
        this.repository.save(newtransaction);
        this.userService.saveUser(sender);
        this.userService.saveUser(receiver);

        this.notificationService.sendNotification(sender, "transação realizada com sucesso");
        this.notificationService.sendNotification(receiver, "transação recebida com sucesso");

    return newtransaction;
    }

    public boolean authorizeTransactrion(User sender, BigDecimal value) {
        //chamando o servico de terceiro para validar a transacao
        ResponseEntity<Map> authorizationResponse = restTemplate.getForEntity("https://run.mocky.io/v3/5794d450-d2e2-4412-8131-73d0293ac1cc", Map.class);

        if (authorizationResponse.getStatusCode() == HttpStatus.OK) {
            String message =(String) authorizationResponse.getBody().get("message");

            return "Autorizado".equalsIgnoreCase(message); //retornara verdadeiro ou falso
        } else return false;

    }
}