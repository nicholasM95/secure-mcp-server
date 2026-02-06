resource "keycloak_realm" "realm_users" {
  realm                = "demo-users"
  enabled              = true
  display_name         = "Demo USERS"
  registration_allowed = false
}

resource "keycloak_user" "demo_user" {
  realm_id = keycloak_realm.realm_users.id
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