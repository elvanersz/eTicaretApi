package com.elvan.eticaret.service;

import com.elvan.eticaret.TestSupport;
import com.elvan.eticaret.dto.CreateUserRequest;
import com.elvan.eticaret.dto.UpdateUserRequest;
import com.elvan.eticaret.dto.UserDto;
import com.elvan.eticaret.dto.UserDtoConverter;
import com.elvan.eticaret.exception.UserIsNotActiveException;
import com.elvan.eticaret.exception.UserNotFoundException;
import com.elvan.eticaret.model.Users;
import com.elvan.eticaret.repository.UsersRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest extends TestSupport {

    private UserDtoConverter converter;
    private UsersRepository repository;

    private UserService userService;

    @Before
    public void setUp(){
        converter = Mockito.mock(UserDtoConverter.class);
        repository = Mockito.mock(UsersRepository.class);

        userService = new UserService(repository, converter);
    }

    @Test
    public void testGetAllUsers_itShouldReturnUserDtoList(){
        List<Users> userList = generateUsers();
        List<UserDto> userDtoList = generateUserDtoList(userList);

        when(repository.findAll()).thenReturn(userList);
        when(converter.convert(userList)).thenReturn(userDtoList);

        List<UserDto> result = userService.getAllUsers();

        Assert.assertEquals(userDtoList, result);
        verify(repository).findAll();
        verify(converter).convert(userList);
    }

    @Test
    public void testGetUserByMail_whenUserMailExist_itShouldReturnUserDto(){
        String mail = "mail@ersoz.net";
        Users user = generateUser(mail);
        UserDto userDto = generateUserDto(mail);

        when(repository.findByMail(mail)).thenReturn(Optional.of(user));
        when(converter.convert(user)).thenReturn(userDto);

        UserDto result = userService.getUserByMail(mail);

        Assert.assertEquals(userDto, result);
        verify(repository).findByMail(mail);
        verify(converter).convert(user);
    }

    @Test
    public void testGetUserByMail_whenUserMailDoesNotExist_itShouldThrowUserNotFoundException(){
        String mail = "mail@ersoz.net";

        when(repository.findByMail(mail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                userService.getUserByMail(mail)
        );

        verify(repository).findByMail(mail);
        Mockito.verifyNoInteractions(converter);
    }

    @Test
    public void testCreateUserItShouldReturnCreatedUserDto(){
        String mail = "mail@ersoz.net";

        CreateUserRequest request = new CreateUserRequest(mail, "firstName", "lastName", "");
        Users user = new Users(mail, "firstName", "lastName", "", false);
        Users savedUser = new Users(1L, mail, "firstName", "lastName", "", false);
        UserDto userDto = new UserDto(mail,"firstName", "lastName", "");

        when(repository.save(user)).thenReturn(savedUser);
        when(converter.convert(savedUser)).thenReturn(userDto);

        UserDto result = userService.createUser(request);

        assertEquals(result, userDto);

        verify(repository).save(user);
        verify(converter).convert(savedUser);
    }

    @Test
    public void testUpdateUser_whenUserMailExistAndUserIsActive_ItShouldReturnUpdateUserDto(){
        String mail = "mail@ersoz.net";

        UpdateUserRequest request = new UpdateUserRequest("firstName2", "lastName2", "middleName");
        Users user = new Users(1L, mail, "firstName", "lastName", "", true);
        Users updateUser = new Users(1L, mail, "firstName2", "lastName2", "middleName", true);
        Users savedUser = new Users(1L, mail, "firstName2", "lastName2", "middleName", true);
        UserDto userDto = new UserDto(mail,"firstName2", "lastName2", "middleName");

        when(repository.findByMail(mail)).thenReturn(Optional.of(user));
        when(repository.save(updateUser)).thenReturn(savedUser);
        when(converter.convert(savedUser)).thenReturn(userDto);

        UserDto result = userService.updateUser(mail, request);

        assertEquals(result, userDto);

        verify(repository).findByMail(mail);
        verify(repository).save(updateUser);
        verify(converter).convert(savedUser);
    }

    @Test
    public void testUpdateUser_whenUserMailDoesNotExist_ItShouldThrowUserNotFoundException(){
        String mail = "mail@ersoz.net";

        UpdateUserRequest request = new UpdateUserRequest("firstName2", "lastName2", "middleName");


        when(repository.findByMail(mail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                userService.updateUser(mail, request)
        );

        verify(repository).findByMail(mail);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(converter);
    }

    @Test
    public void testUpdateUser_whenUserMailExistButUserIsNotActive_ItShouldThrowUserIsNotActiveException(){
        String mail = "mail@ersoz.net";

        UpdateUserRequest request = new UpdateUserRequest("firstName2", "lastName2", "middleName");
        Users user = new Users(1L, mail, "firstName", "lastName", "", false);

        when(repository.findByMail(mail)).thenReturn(Optional.of(user));

        assertThrows(UserIsNotActiveException.class, () ->
                userService.updateUser(mail, request)
        );

        verify(repository).findByMail(mail);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(converter);
    }

    @Test
    public void testDeactivateUser_whenUserIdExist_ItShouldUpdateUserByActiveFalse(){
        String mail = "mail@ersoz.net";

        Users user = new Users(userId, mail, "firstName", "lastName", "", false);
        Users savedUser = new Users(userId, mail, "firstName", "lastName", "", false);

        when(repository.findById(userId)).thenReturn(Optional.of(user));

        userService.deactivateUser(userId);

        verify(repository).findById(userId);
        verify(repository).save(savedUser);
    }

    @Test
    public void testDeactivateUser_whenUserIdDoesNotExist_ItShouldThrowUserNotFoundException(){

        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                userService.deactivateUser(userId)
        );

        verify(repository).findById(userId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void testActivateUser_whenUserIdExist_ItShouldUpdateUserByActiveTrue(){

        String mail = "mail@ersoz.net";

        Users user = new Users(userId, mail, "firstName", "lastName", "", true);
        Users savedUser = new Users(userId, mail, "firstName", "lastName", "", true);

        when(repository.findById(userId)).thenReturn(Optional.of(user));

        userService.activateUser(userId);

        verify(repository).findById(userId);
        verify(repository).save(savedUser);
    }

    @Test
    public void testActivateUser_whenUserIdDoesNotExist_ItShouldThrowUserNotFoundException(){

        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                userService.activateUser(userId)
        );

        verify(repository).findById(userId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void testDeleteUser_whenUserIdExist_ItShouldDeleteUser(){

        String mail = "mail@ersoz.net";

        Users user = new Users(userId, mail, "firstName", "lastName", "", true);

        when(repository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(repository).findById(userId);
        verify(repository).deleteById(userId);
    }

    @Test
    public void testDeleteUser_whenUserIdDoesNotExist_ItShouldThrowUserNotFoundException(){

        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                userService.deleteUser(userId)
        );

        verify(repository).findById(userId);
        verifyNoMoreInteractions(repository);
    }
}