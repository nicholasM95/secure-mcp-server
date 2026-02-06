resource "keycloak_realm" "realm_mcp_servers" {
  realm                = "demo-mcp-servers"
  enabled              = true
  display_name         = "Demo MCP Servers"
  registration_allowed = false
}

resource "keycloak_openid_client_scope" "openid" {
  realm_id    = keycloak_realm.realm_mcp_servers.id
  name        = "openid"
  description = "OpenID"
}


resource "keycloak_realm_optional_client_scopes" "optional_scopes" {
  realm_id = keycloak_realm.realm_mcp_servers.id

  optional_scopes = [
    "address",
    "phone",
    "offline_access",
    "microprofile-jwt",
    "organization",
    keycloak_openid_client_scope.openid.name
  ]
}