package com.intership.userservice.controller;

import com.intership.userservice.model.dto.CardDto;
import com.intership.userservice.model.dto.UpdateCardDto;
import com.intership.userservice.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CardDto createCard(@Valid @RequestBody CardDto cardDto) {
        return cardService.createCard(cardDto);
    }

    @GetMapping("/{id}")
    public CardDto getCardById(@PathVariable Long id) {
        return cardService.findById(id);
    }

    @GetMapping
    public List<CardDto> getCardsByIds(@RequestParam("ids") List<Long> ids) {
        return cardService.findAllByIds(ids);
    }

    @GetMapping("/user/{userId}")
    public List<CardDto> getCardsByUserId(@PathVariable Long userId) {
        return cardService.findAllByUserId(userId);
    }

    @PutMapping("/{id}")
    public CardDto updateCard(@PathVariable Long id, @Valid @RequestBody UpdateCardDto updateRequest) {
        return cardService.updateById(id, updateRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable Long id) {
        cardService.deleteById(id);
    }
}