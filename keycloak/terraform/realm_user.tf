resource "keycloak_realm" "realm_user" {
  realm                = "demo-user"
  enabled              = true
  display_name         = "Demo USER"
  registration_allowed = false
}

resource "keycloak_user" "demo_user" {
  realm_id = keycloak_realm.realm_user.id
  username = "testuser"
  enabled  = true

  email          = "testuser@example.com"
  email_verified = true
  first_name     = "Test"
  last_name      = "User"

  initial_password {
    value     = "changeme123"
    temporary = false
  }
}