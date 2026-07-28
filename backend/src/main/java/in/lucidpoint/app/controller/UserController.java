package in.lucidpoint.app.controller;

import in.lucidpoint.app.dto.UpdateTierRequest;
import in.lucidpoint.app.entity.User;
import in.lucidpoint.app.security.UserPrincipal;
import in.lucidpoint.app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * The entitlement scaffold (ROADMAP.md §3.4). No premium feature exists yet —
 * this exists so every feature built from here on can gate on subscription tier
 * from day one instead of retrofitting the check later.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public User me(@AuthenticationPrincipal UserPrincipal principal) {
        return userService.getById(principal.getId());
    }

    @PatchMapping("/{id}/tier")
    @PreAuthorize("hasRole('ADMIN')")
    public User updateTier(@PathVariable Long id, @Valid @RequestBody UpdateTierRequest request) {
        return userService.updateTier(id, request.getTier());
    }

    // Proves the TIER_PREMIUM guard actually enforces (see UserPrincipal.getAuthorities()).
    // Delete once a real premium feature exists and demonstrates the same pattern.
    @GetMapping("/me/premium-check")
    @PreAuthorize("hasAuthority('TIER_PREMIUM')")
    public String premiumCheck() {
        return "You have premium access.";
    }
}
