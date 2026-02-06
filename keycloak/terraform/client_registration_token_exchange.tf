resource "keycloak_openid_client" "openid_client_token_exchange" {
  realm_id      = keycloak_realm.realm_mcp_servers.id
  client_id     = "token-exchange"
  client_secret = "e37af147-ebef-4723-be20-0ba92bc25b08"

  name    = "token exchange"
  enabled = true

  access_type = "CONFIDENTIAL"

  standard_token_exchange_enabled = true
}
