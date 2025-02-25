package org.ably.it_support.core.security;

import lombok.RequiredArgsConstructor;
import org.ably.it_support.user.AppUser;
import org.ably.it_support.user.UserService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;



@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final UserService userService;



    public AppUser getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
            authentication instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();
        String username = null;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal; // Some authentication types store the username directly
        }

        if (username == null) {
            throw new IllegalStateException("Unable to retrieve logged-in username");
        }

        return userService.findByEmail(username);

    }
}

