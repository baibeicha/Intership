package com.intership.userservice;

import com.intership.userservice.exception.CardNotFoundException;
import com.intership.userservice.mapper.CardDtoMapper;
import com.intership.userservice.model.dto.CardDto;
import com.intership.userservice.model.dto.UpdateCardDto;
import com.intership.userservice.model.entity.Card;
import com.intership.userservice.model.entity.User;
import com.intership.userservice.repository.CardRepository;
import com.intership.userservice.service.UserService;
import com.intership.userservice.service.impl.CardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardDtoMapper cardMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private CardServiceImpl cardService;

    private Card card;
    private CardDto cardDto;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        card = new Card();
        card.setId(1L);
        card.setNumber("1234-5678-9012-3456");
        card.setHolder("John Doe");
        card.setExpirationDate("12/30");
        card.setUser(user);

        cardDto = new CardDto();
        cardDto.setUserId(user.getId());
        cardDto.setNumber(card.getNumber());
        cardDto.setHolder(card.getHolder());
        cardDto.setExpirationDate(card.getExpirationDate());
    }

    @Test
    void createCard_shouldSetUserAndSave() {
        when(cardMapper.toEntity(any(CardDto.class))).thenReturn(card);
        when(userService.getById(user.getId())).thenReturn(user);
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toDto(any(Card.class))).thenReturn(cardDto);

        CardDto result = cardService.createCard(cardDto);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(user.getId());
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void findById_whenNotFound_shouldThrow() {
        when(cardRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardService.findById(100L));
    }

    @ParameterizedTest
    @MethodSource("provideIdLists")
    void findAllByIds_shouldReturnDtos_forVariousLists(List<Long> ids) {
        List<Card> repoResult = ids.isEmpty() ? List.of() : List.of(card);
        List<CardDto> mapperResult = ids.isEmpty() ? List.of() : List.of(cardDto);

        when(cardRepository.findAllByIdIn(ids)).thenReturn(repoResult);
        when(cardMapper.toDtos(repoResult)).thenReturn(mapperResult);

        List<CardDto> result = cardService.findAllByIds(ids);

        if (ids.isEmpty()) {
            assertThat(result).isEmpty();
        } else {
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getNumber()).isEqualTo(card.getNumber());
        }
    }

    static Stream<List<Long>> provideIdLists() {
        return Stream.of(
                List.of(1L),
                List.of(1L, 2L),
                List.of()
        );
    }

    @Test
    void updateById_shouldCallUpdateThenReturnDto() {
        UpdateCardDto update = new UpdateCardDto();
        update.setNumber("9999-8888-7777-6666");
        update.setHolder("New Holder");
        update.setExpirationDate("01/29");

        Card updated = new Card();
        updated.setId(1L);
        updated.setNumber(update.getNumber());
        updated.setHolder(update.getHolder());
        updated.setExpirationDate(update.getExpirationDate());
        updated.setUser(user);

        doNothing().when(cardRepository).updateById(
                eq(1L), eq(update.getNumber()), eq(update.getHolder()), eq(update.getExpirationDate()));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(updated));
        when(cardMapper.toDto(updated)).thenReturn(cardDto);

        CardDto result = cardService.updateById(1L, update);

        assertThat(result).isNotNull();
        verify(cardRepository).updateById(
                eq(1L), eq(update.getNumber()), eq(update.getHolder()), eq(update.getExpirationDate()));
    }

    @Test
    void updateById_whenCardMissingAfterUpdate_shouldThrowCardNotFound() {
        UpdateCardDto update = new UpdateCardDto();
        update.setNumber("0000-0000-0000-0000");
        update.setHolder("Ghost");
        update.setExpirationDate("01/30");

        doNothing().when(cardRepository).updateById(
                eq(1L), eq(update.getNumber()), eq(update.getHolder()), eq(update.getExpirationDate()));
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardService.updateById(1L, update));

        verify(cardRepository).updateById(
                eq(1L), eq(update.getNumber()), eq(update.getHolder()), eq(update.getExpirationDate()));
    }

    @Test
    void deleteById_shouldCallRepository() {
        doNothing().when(cardRepository).deleteById(1L);

        cardService.deleteById(1L);

        verify(cardRepository).deleteById(1L);
    }
}
