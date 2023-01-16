package com.albpintado.elemenst.config;

import com.albpintado.elemenst.user.User;
import com.albpintado.elemenst.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/*
Service in charge of retrieving user from the repository
that matches the email sent.
 */
@Service
public class IUserDetailsService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
    User user = this.userRepository.findOneByUserName(userName)
            .orElseThrow(() -> new UsernameNotFoundException("The user with user name " + userName + " does not exist."));

    return new IUserDetails(user);
  }
}
