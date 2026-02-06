resource "keycloak_openid_client" "openid_client" {
  realm_id      = keycloak_realm.realm_users.id
  client_id     = "temperature_resource_server"
  client_secret = "2a01dc12-a628-465c-85b4-49a1990c2189"

  name    = "temperature resource server"
  enabled = true

  access_type = "CONFIDENTIAL"
}
