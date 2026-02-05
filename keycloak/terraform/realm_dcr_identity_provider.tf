resource "keycloak_oidc_identity_provider" "demo_user_idp" {
  realm                         = keycloak_realm.realm_dcr.id
  alias                         = "demo-user"
  display_name                  = "Demo User Login"
  enabled                       = true
  trust_email                   = true
  first_broker_login_flow_alias = "first broker login"

  authorization_url = "${var.keycloak_url}/realms/demo-user/protocol/openid-connect/auth"
  token_url         = "${var.keycloak_url}/realms/demo-user/protocol/openid-connect/token"
  user_info_url     = "${var.keycloak_url}/realms/demo-user/protocol/openid-connect/userinfo"
  jwks_url          = "${var.keycloak_url}/realms/demo-user/protocol/openid-connect/certs"

  client_id     = keycloak_openid_client.idp_broker_client.client_id
  client_secret = keycloak_openid_client.idp_broker_client.client_secret

  default_scopes = "openid profile email"

  login_hint = ""
}

resource "keycloak_openid_client" "idp_broker_client" {
  realm_id              = keycloak_realm.realm_user.id
  client_id             = "dcr-realm-broker"
  name                  = "DCR Realm Broker"
  enabled               = true
  access_type           = "CONFIDENTIAL"
  standard_flow_enabled = true
  valid_redirect_uris = [
    "${var.keycloak_url}/realms/demo-dcr/broker/demo-user/endpoint"
  ]
}


resource "keycloak_authentication_flow" "direct_idp_redirect" {
  realm_id    = keycloak_realm.realm_dcr.id
  alias       = "direct-idp-redirect"
  description = "Direct redirect to identity provider"
}

resource "keycloak_authentication_execution" "idp_redirector" {
  realm_id          = keycloak_realm.realm_dcr.id
  parent_flow_alias = keycloak_authentication_flow.direct_idp_redirect.alias
  authenticator     = "identity-provider-redirector"
  requirement       = "REQUIRED"
}

resource "keycloak_authentication_execution_config" "idp_redirector_config" {
  realm_id     = keycloak_realm.realm_dcr.id
  execution_id = keycloak_authentication_execution.idp_redirector.id
  alias        = "demo-user-redirector"

  config = {
    "defaultProvider" = "demo-user"
  }
}

resource "keycloak_authentication_bindings" "dcr_bindings" {
  realm_id     = keycloak_realm.realm_dcr.id
  browser_flow = keycloak_authentication_flow.direct_idp_redirect.alias
}