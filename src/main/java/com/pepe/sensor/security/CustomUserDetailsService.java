package com.pepe.sensor.security;

import com.pepe.sensor.persistence.Person;
import com.pepe.sensor.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final PersonRepository personRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    public CustomUserDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DisabledException {
        Person user = personRepository.findByUsername(username);

        if (user == null) {
            logger.info("Trying to log in but user not found: " + username);
            throw new UsernameNotFoundException("User " + username + " not found!");
        }
        if (!user.isActivated()) {
            logger.info("Trying to log in but user not activated: " + user.getUsername() + " - " + user.getEmail());
            throw new DisabledException("User " + username + " not activated!");
        }

        UserDetails userDetails = User.withUsername(user.getUsername()).password(user.getPassword()).roles(user.getRole()).build();
        return userDetails;
    }
}
