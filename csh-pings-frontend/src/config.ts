import { OidcConfiguration } from "@axa-fr/react-oidc/dist/vanilla/oidc";

const configuration: OidcConfiguration = {
  client_id: "brickwall",
  redirect_uri: `${window.location.protocol}//${window.location.hostname}${window.location.port ? `:${window.location.port}` : ''
    }/authentication/callback`,
  scope: 'openid profile email offline_access',
  authority: "https://sso.csh.rit.edu/auth/realms/csh",
  silent_redirect_uri: `${window.location.protocol}//${window.location.hostname
    }${window.location.port ? `:${window.location.port}` : ''
    }/authentication/silent_callback`,
  service_worker_only: false,
  refresh_time_before_tokens_expiration_in_second: 10,
}

export default configuration;
