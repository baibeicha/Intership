package com.intership.userservice;

import com.intership.userservice.model.dto.CardDto;
import com.intership.userservice.model.dto.UserDto;
import com.intership.userservice.service.CardService;
import com.intership.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserCardIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private CardService cardService;

    @Test
    void createUserAndCard_shouldWorkEndToEnd() {
        UserDto userDto = new UserDto();
        userDto.setName("Integration");
        userDto.setSurname("Tester");
        userDto.setEmail("int.test@example.com");

        UserDto createdUser = userService.createUser(userDto);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isNotNull();

        CardDto cardDto = new CardDto();
        cardDto.setUserId(createdUser.getId());
        cardDto.setNumber("1111-2222-3333-4444");
        cardDto.setHolder("Integration Tester");

        CardDto createdCard = cardService.createCard(cardDto);

        assertThat(createdCard).isNotNull();
        assertThat(createdCard.getUserId()).isEqualTo(createdUser.getId());

        UserDto userWithCards = userService.findById(createdUser.getId());
        assertThat(userWithCards).isNotNull();
        assertThat(userWithCards.getCards()).isNotEmpty();
    }
}
