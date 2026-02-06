resource "keycloak_oidc_identity_provider" "demo_user_idp" {
  realm        = keycloak_realm.realm_mcp_servers.id
  alias        = "demo-users"
  display_name = "Demo Users Login"
  enabled      = true
  trust_email  = true

  store_token                   = true
  add_read_token_role_on_create = true

  first_broker_login_flow_alias = "first broker login"

  authorization_url = "${var.keycloak_url}/realms/demo-users/protocol/openid-connect/auth"
  token_url         = "${var.keycloak_url}/realms/demo-users/protocol/openid-connect/token"
  user_info_url     = "${var.keycloak_url}/realms/demo-users/protocol/openid-connect/userinfo"
  jwks_url          = "${var.keycloak_url}/realms/demo-users/protocol/openid-connect/certs"

  client_id     = keycloak_openid_client.idp_broker_client.client_id
  client_secret = keycloak_openid_client.idp_broker_client.client_secret

  default_scopes = "openid profile email"

  login_hint = ""
}

resource "keycloak_openid_client" "idp_broker_client" {
  realm_id              = keycloak_realm.realm_users.id
  client_id             = "mcp-server-realm-broker"
  name                  = "MCP SERVER Realm Broker"
  enabled               = true
  access_type           = "CONFIDENTIAL"
  standard_flow_enabled = true
  valid_redirect_uris = [
    "${var.keycloak_url}/realms/demo-mcp-servers/broker/demo-users/endpoint"
  ]
}


resource "keycloak_authentication_flow" "direct_idp_redirect" {
  realm_id    = keycloak_realm.realm_mcp_servers.id
  alias       = "direct-idp-redirect"
  description = "Direct redirect to identity provider"
}

resource "keycloak_authentication_execution" "idp_redirector" {
  realm_id          = keycloak_realm.realm_mcp_servers.id
  parent_flow_alias = keycloak_authentication_flow.direct_idp_redirect.alias
  authenticator     = "identity-provider-redirector"
  requirement       = "REQUIRED"
}

resource "keycloak_authentication_execution_config" "idp_redirector_config" {
  realm_id     = keycloak_realm.realm_mcp_servers.id
  execution_id = keycloak_authentication_execution.idp_redirector.id
  alias        = "demo-user-redirector"

  config = {
    "defaultProvider" = "demo-users"
  }
}

resource "keycloak_authentication_bindings" "mcp_bindings" {
  realm_id     = keycloak_realm.realm_mcp_servers.id
  browser_flow = keycloak_authentication_flow.direct_idp_redirect.alias
}

resource "keycloak_identity_provider_token_exchange_scope_permission" "allow_exchange" {
  realm_id       = keycloak_realm.realm_mcp_servers.id
  provider_alias = keycloak_oidc_identity_provider.demo_user_idp.alias
  policy_type    = "client"
  clients = [
    keycloak_openid_client.openid_client_token_exchange.id
  ]
}