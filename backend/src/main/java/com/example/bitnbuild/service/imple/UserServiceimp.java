package com.example.bitnbuild.service.imple;


import com.example.bitnbuild.dto.LoginRequest;
import com.example.bitnbuild.dto.Response;
import com.example.bitnbuild.dto.UserDto;
import com.example.bitnbuild.entity.User;
import com.example.bitnbuild.exception.OurException;
import com.example.bitnbuild.repo.UserRepository;
import com.example.bitnbuild.service.inter.UserService;
import com.example.bitnbuild.utils.JWTUtils;
import com.example.bitnbuild.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceimp implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public Response login(LoginRequest loginRequest) {

        Response response=new Response();

        try
        {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));

            var user=userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(()-> new OurException("User not found"));

            var token=jwtUtils.generateToken(user);
            response.setStatuscode(200);
            response.setToken(token);
            response.setRole(user.getRole());
            response.setExpirationTime("7 Days");
            response.setMessage("Successfull");
        }
        catch (OurException e)
        {
            response.setStatuscode(400);
            response.setMessage(e.getMessage());
        }
        catch (Exception e)
        {
            response.setStatuscode(500);
            response.setMessage("Error occured during user registration"+e.getMessage());
        }


        return response;
    }

    @Override
    public Response register(User user) {
      Response response=new Response();
      try
      {
          if(user.getRole()==null || user.getRole().isBlank())
          {
              user.setRole("USER");
          }
          if(userRepository.existsByEmail(user.getEmail()))
          {
              throw  new OurException(user.getEmail()+"Already exists");
          }
          user.setPassword(passwordEncoder.encode(user.getPassword()));
          User saveduser=userRepository.save(user);
          UserDto userDto= Utils.mapUserEntityToUserDto(saveduser);
          response.setStatuscode(200);
          response.setUser(userDto);
      }
      catch (OurException e)
      {
        response.setStatuscode(400);
        response.setMessage(e.getMessage());
      }
      catch (Exception e)
      {
          response.setStatuscode(500);
          response.setMessage("Error occured during user registration"+e.getMessage());
      }
      return response;
    }

    @Override
    public Response getAllUsers() {
        Response response=new Response();

        try
        {
            List<User> userList=userRepository.findAll();
            List<UserDto>userDtoList= Utils.mapUserListEntityToUserListDTO(userList);
            response.setStatuscode(200);
            response.setMessage("Successfull");
            response.setUserlist(userDtoList);
        }
        catch (Exception e)
        {
            response.setStatuscode(500);
            response.setMessage("Error getting all users"+e.getMessage());
        }

        return response;
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found")); // Handle the case where the user is not found
    }

    @Override
    public Response deleteUser(String userid) {

        Response response=new Response();
        try
        {
            userRepository.findById(Long.valueOf(userid)).orElseThrow(()-> new OurException("User Not found"));
            userRepository.deleteById(Long.valueOf(userid));
            response.setStatuscode(200);
            response.setMessage("Successfull");

        }
        catch (OurException e)
        {
            response.setStatuscode(400);
            response.setMessage(e.getMessage());
        }
        catch (Exception e)
        {
            response.setStatuscode(500);
            response.setMessage("Error occured during user registration"+e.getMessage());
        }
        return response;
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }
    @Override
    public Response getUserById(String userid) {
        Response response=new Response();
        try
        {
            User user=userRepository.findById(Long.valueOf(userid)).orElseThrow(()-> new OurException("User Not found"));
            UserDto userDto=Utils.mapUserEntityToUserDto(user);
            response.setStatuscode(200);
            response.setMessage("Successfull");
            response.setUser(userDto);
        }
        catch (OurException e)
        {
            response.setStatuscode(400);
            response.setMessage(e.getMessage());
        }
        catch (Exception e)
        {
            response.setStatuscode(500);
            response.setMessage("Error occured during user registration"+e.getMessage());
        }
        return response;
    }

    @Override
    public Response getMyInfo(String email) {
        Response response=new Response();
        try
        {
            User user=userRepository.findByEmail(email).orElseThrow(()-> new OurException("User Not found"));

            response.setStatuscode(200);
            response.setMessage("Successfull");

        }
        catch (OurException e)
        {
            response.setStatuscode(400);
            response.setMessage(e.getMessage());
        }
        catch (Exception e)
        {
            response.setStatuscode(500);
            response.setMessage("Error occured during user registration"+e.getMessage());
        }
        return response;
    }

}
