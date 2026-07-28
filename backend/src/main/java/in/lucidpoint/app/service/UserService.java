package in.lucidpoint.app.service;

import in.lucidpoint.app.entity.SubscriptionTier;
import in.lucidpoint.app.entity.User;
import in.lucidpoint.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    /**
     * Manual override until Phase 4 wires up a real billing provider (ROADMAP.md §3.4/§5) —
     * an admin flips this directly rather than a payment webhook doing it.
     */
    public User updateTier(Long id, SubscriptionTier tier) {
        User user = getById(id);
        user.setSubscriptionTier(tier);
        return userRepository.save(user);
    }
}
