package com.example.bitnbuild.service.inter;


import com.example.bitnbuild.dto.LoginRequest;
import com.example.bitnbuild.dto.Response;
import com.example.bitnbuild.entity.User;

public interface UserService {

    Response login(LoginRequest loginRequest);

    Response register(User user);

    Response getAllUsers();



    Response deleteUser(String userid);

    Response getUserById(String userid);

    Response getMyInfo(String email);


    User getCurrentUser();

    User findByEmail(String username);

    User findById(Long uploaderId);
}
