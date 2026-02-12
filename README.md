# Secure MCP Server with Keycloak

A demonstration project showing how to secure a Spring Boot MCP (Model Context Protocol) server using Keycloak with OAuth 2.0 Dynamic Client Registration and Token Exchange.

## Overview

This project implements a secure architecture for MCP servers that allows AI clients (like Claude) to authenticate and call downstream services. It uses two advanced OAuth 2.0 patterns:

- **Dynamic Client Registration (DCR)**: Allows MCP clients to register themselves programmatically
- **Token Exchange**: Enables the MCP server to exchange tokens when calling downstream services

### Architecture

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   AI Client     │────▶│   MCP Server    │────▶│   Temperature   │◀────│    Keycloak     │
│   (Claude)      │     │   (Spring Boot) │     │   API           │     │   (OAuth 2.0)   │
└─────────────────┘     └─────────────────┘     └─────────────────┘     └─────────────────┘
        │                       │                                               │
        │                       │                                               │
        └───────────────────────┴──────── Token Exchange ──────────────────────┘
```

The setup consists of:
- **MCP Server**: Exposes MCP tools to AI clients, handles OAuth 2.0 authentication
- **Temperature API**: A resource server that returns temperature readings
- **Keycloak**: Identity and access management with two realms:
    - `demo-mcp-servers`: For MCP client registrations (DCR)
    - `demo-users`: Contains users and protects the Temperature API
- **Traefik**: Reverse proxy that routes traffic

## Prerequisites

- Docker and Docker Compose
- Java 25+ (for building the applications)
- Maven (or use the included Maven wrapper)
- ngrok (or similar tunneling service) for exposing services to the internet
- Terraform (optional, for automated Keycloak configuration)

## Quick Start

### Step 1: Set Up a Public URL

Since MCP clients like Claude need to reach your services over the internet, you need a public URL. Using ngrok:

```bash
ngrok http 80
```

Note your ngrok URL (e.g., `https://xxxx-xx-xxx-xx-xxx.ngrok-free.app`).

### Step 2: Update Configuration

Update the ngrok domain in the following files:

**docker-compose.yaml** - Replace all occurrences of `04f7-81-240-46-212.ngrok-free.app` with your ngrok domain:

```yaml
# Line 17: Keycloak hostname
KC_HOSTNAME: your-ngrok-domain.ngrok-free.app

# Line 32: MCP Server issuer URI
ISSUER_URI: https://your-ngrok-domain.ngrok-free.app/keycloak/realms/demo-mcp-servers

# Line 33: Temperature API URL
TEMPERATURE_API: https://your-ngrok-domain.ngrok-free.app/api

# Line 51: Temperature API issuer URI
ISSUER_URI: https://your-ngrok-domain.ngrok-free.app/keycloak/realms/demo-users
```

**keycloak/terraform/variables.tf** - Update the Keycloak URL:

```hcl
variable "keycloak_url" {
  type    = string
  default = "https://your-ngrok-domain.ngrok-free.app/keycloak"
}
```

### Step 3: Build Docker Images

Build the MCP Server:

```bash
cd mcp-server
chmod +x build-docker.sh
./build-docker.sh
cd ..
```

Build the Temperature API:

```bash
cd temperature-api
chmod +x build-docker.sh
./build-docker.sh
cd ..
```

### Step 4: Start the Services (First Run)

Start only Keycloak first to configure it:

```bash
docker compose up keycloak traefik -d
```

Wait for Keycloak to be fully started (check logs with `docker compose logs -f keycloak`).

### Step 5: Configure Keycloak

You have two options:

#### Option A: Using Terraform (Recommended)

```bash
cd keycloak/terraform
terraform init
terraform apply
cd ../..
```

Then, configure Dynamic Client Registration manually in Keycloak. See the [Configure DCR](https://blog.nicholasmeyers.be/posts/2026-02-12-secure-your-spring-boot-mcp-server-with-keycloak/#configure-the-dynamic-client-registration).

#### Option B: Manual Configuration

Follow the complete manual setup guide in the [blog post](https://blog.nicholasmeyers.be/posts/2026-02-12-secure-your-spring-boot-mcp-server-with-keycloak).

### Step 6: Update Token Exchange Secret

After Terraform creates the `token-exchange` client, get its secret from Keycloak:

1. Go to Keycloak Admin Console: `https://your-ngrok-domain.ngrok-free.app/keycloak/admin`
2. Login with `admin` / `admin`
3. Select realm `demo-mcp-servers`
4. Go to Clients → `token-exchange` → Credentials
5. Copy the Client Secret
6. Update `docker-compose.yaml`:

```yaml
TOKEN_EXCHANGE_CLIENT_SECRET: <your-client-secret>
```

### Step 7: Start All Services

```bash
docker compose down
docker compose up -d
```

### Step 8: Connect from Claude

In Claude, add a new MCP server connection:

```
https://your-ngrok-domain.ngrok-free.app/mcp
```

## Project Structure

```
secure-mcp-server/
├── docker-compose.yaml          # Docker Compose configuration
├── mcp-server/                  # Spring Boot MCP Server
│   ├── build-docker.sh          # Script to build Docker image
│   ├── pom.xml                  # Maven dependencies
│   └── src/                     # Source code
├── temperature-api/             # Spring Boot Temperature Resource Server
│   ├── build-docker.sh          # Script to build Docker image
│   ├── pom.xml                  # Maven dependencies
│   └── src/                     # Source code
└── keycloak/
    └── terraform/               # Terraform configuration for Keycloak
        ├── provider.tf
        ├── variables.tf
        ├── realm_mcp_server.tf
        ├── realm_user.tf
        ├── realm_mcp_identity_provider.tf
        ├── client_registration_token_exchange.tf
        └── client_registration_resource_server.tf
```

## Default Credentials

| Service | Username | Password |
|---------|----------|----------|
| Keycloak Admin | admin | admin |
| Demo User | testuser | changeme123 |

## Endpoints

| Service | URL |
|---------|-----|
| Keycloak Admin | `https://your-domain/keycloak/admin` |
| MCP Server | `https://your-domain/mcp` |
| Temperature API | `https://your-domain/api/temperature` |

## How It Works

1. **Client Registration**: When Claude connects to the MCP server, it discovers the OAuth configuration and registers itself via DCR in the `demo-mcp-servers` realm.

2. **User Authentication**: The user is redirected to Keycloak for authentication. The `demo-mcp-servers` realm federates to `demo-users` via an Identity Provider, so users log in with their existing credentials.

3. **Token Exchange**: When the MCP server needs to call the Temperature API, it exchanges the token from `demo-mcp-servers` for a token from `demo-users` that the API will accept.

4. **API Call**: The MCP server calls the Temperature API with the exchanged token.


## Resources

- [Blog Post: Secure your Spring Boot MCP Server with Keycloak](https://blog.nicholasmeyers.be/posts/2026-02-12-secure-your-spring-boot-mcp-server-with-keycloak)
- [Model Context Protocol Documentation](https://modelcontextprotocol.io/)
- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [RFC 7591 - Dynamic Client Registration](https://datatracker.ietf.org/doc/html/rfc7591)
- [RFC 8693 - Token Exchange](https://datatracker.ietf.org/doc/html/rfc8693)

## Security Notice

This is a demonstration project. For production use:

- Use strong, unique passwords
- Properly secure your Keycloak instance
- Use proper TLS certificates (not ngrok)
- Review and restrict client scopes appropriately
- Implement proper audience validation in all resource servers