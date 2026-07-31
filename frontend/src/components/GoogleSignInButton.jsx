import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID;

// Module-level, not component state: google.accounts.id is a global singleton, so it should
// only ever be initialized once regardless of how many times this component mounts (React 18
// StrictMode deliberately double-invokes effects in dev, which was calling initialize() twice
// and triggering the SDK's own "called multiple times" warning).
let googleSdkInitialized = false;

// Exported so pages can skip rendering an "or" divider above a button that isn't there.
export const isGoogleSignInEnabled = Boolean(CLIENT_ID);

/**
 * Renders nothing at all if VITE_GOOGLE_CLIENT_ID isn't set (e.g. before the Google Cloud
 * OAuth setup is done) — a visible but non-functional button is worse than no button.
 */
export default function GoogleSignInButton() {
  const buttonRef = useRef(null);
  const [error, setError] = useState("");
  const { loginWithGoogle } = useAuth();
  const navigate = useNavigate();

  // The Google SDK callback is registered once (see the effect below) but needs to call
  // whatever the *current* loginWithGoogle/navigate are — this ref sidesteps re-initializing
  // the whole SDK/button just because those function identities change across renders.
  const handlerRef = useRef();
  handlerRef.current = async (response) => {
    try {
      await loginWithGoogle(response.credential);
      navigate("/dashboard");
    } catch (err) {
      setError(err.response?.data?.error || "Google sign-in failed.");
    }
  };

  useEffect(() => {
    if (!CLIENT_ID) return;

    function render() {
      if (!googleSdkInitialized) {
        window.google.accounts.id.initialize({
          client_id: CLIENT_ID,
          callback: (response) => handlerRef.current(response),
        });
        googleSdkInitialized = true;
      }
      window.google.accounts.id.renderButton(buttonRef.current, {
        theme: "outline",
        size: "large",
        width: 320,
      });
    }

    if (window.google?.accounts?.id) {
      render();
      return;
    }

    const script = document.createElement("script");
    script.src = "https://accounts.google.com/gsi/client";
    script.async = true;
    script.onload = render;
    document.body.appendChild(script);
  }, []);

  if (!CLIENT_ID) return null;

  return (
    <div className="google-signin">
      {error && <div className="error-banner">{error}</div>}
      <div ref={buttonRef}></div>
    </div>
  );
}
