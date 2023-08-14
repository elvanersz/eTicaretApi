package com.elvan.eticaret.service;

import com.elvan.eticaret.dto.CreateUserRequest;
import com.elvan.eticaret.dto.UpdateUserRequest;
import com.elvan.eticaret.dto.UserDto;
import com.elvan.eticaret.dto.UserDtoConverter;
import com.elvan.eticaret.exception.UserIsNotActiveException;
import com.elvan.eticaret.exception.UserNotFoundException;
import com.elvan.eticaret.model.Users;
import com.elvan.eticaret.repository.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UsersRepository usersRepository;
    private final UserDtoConverter userDtoConverter;

    public UserService(UsersRepository usersRepository, UserDtoConverter userDtoConverter) {
        this.usersRepository = usersRepository;
        this.userDtoConverter = userDtoConverter;
    }


    public List<UserDto> getAllUsers(){
        return userDtoConverter.convert(usersRepository.findAll());
    }

    public UserDto getUserByMail(String mail) {
        Users users = findUserByMail(mail);
        return userDtoConverter.convert(users);
    }

    public UserDto createUser(final CreateUserRequest createUserRequest) {
        Users users = new Users(null,
                createUserRequest.getMail(),
                createUserRequest.getFirstName(),
                createUserRequest.getLastName(),
                createUserRequest.getMiddleName(),
                false);

        return userDtoConverter.convert(usersRepository.save(users));
    }

    public UserDto updateUser(final String mail, final UpdateUserRequest updateUserRequest) {
        Users users = findUserByMail(mail);

        if(!users.getActive()){
            logger.warn(String.format("The user wanted update is not active!, user mail: %s", mail));
            throw new UserIsNotActiveException();
        }

        Users updateUsers = new Users(users.getId(),
                users.getMail(),
                updateUserRequest.getFirstName(),
                updateUserRequest.getLastName(),
                updateUserRequest.getMiddleName(),
                users.getActive());

        return userDtoConverter.convert(usersRepository.save(updateUsers));
    }

    public void deactivateUser(final Long id) {
        changeActivateUser(id, false);
    }

    public void activateUser(final Long id) {
        changeActivateUser(id, true);
    }

    private void changeActivateUser(final Long id, Boolean isActive){
        Users users = findUserById(id);
        Users updateUsers = new Users(users.getId(),
                users.getMail(),
                users.getFirstName(),
                users.getLastName(),
                users.getMiddleName(),
                isActive);

        usersRepository.save(updateUsers);
    }

    public void deleteUser(final Long id) {
        findUserById(id);

        usersRepository.deleteById(id);
    }

    private Users findUserByMail(String mail){
        return usersRepository.findByMail(mail)
                .orElseThrow(() -> new UserNotFoundException("User couldn't be found by following mail: " + mail));
    }

    protected Users findUserById(Long id){
        return usersRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User couldn't be found by following id: " + id));
    }
}