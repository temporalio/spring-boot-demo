# Temporal Cloud Connection Setup

This guide explains how to connect the Spring Boot demo to Temporal Cloud instead of a local Temporal server.

## Prerequisites

1. **Temporal Cloud Account** - Sign up at [cloud.temporal.io](https://cloud.temporal.io/)
2. **Namespace** - Create a namespace in Temporal Cloud
3. **mTLS Certificates** - Download client certificates for your namespace

## Obtaining mTLS Certificates

### From Temporal Cloud UI:

1. Log in to [Temporal Cloud](https://cloud.temporal.io/)
2. Navigate to your namespace
3. Go to **Settings** → **Certificates**
4. Click **Generate Certificate** (or use existing)
5. Download the certificate files:
   - `client.pem` - Client certificate
   - `client.key` - Private key

### Store Certificates Securely:

```bash
# Create a directory for certificates (outside the project)
mkdir -p ~/.temporal/certs

# Move your downloaded certificates
mv ~/Downloads/client.pem ~/.temporal/certs/
mv ~/Downloads/client.key ~/.temporal/certs/

# Set proper permissions
chmod 600 ~/.temporal/certs/client.key
chmod 644 ~/.temporal/certs/client.pem
```

## Configuration Options

### Option 1: Using Environment Variables (Recommended)

Set the following environment variables:

```bash
export TEMPORAL_ADDRESS="your-namespace.tmprl.cloud:7233"
export TEMPORAL_NAMESPACE="your-namespace.account-id"
export TEMPORAL_CERT_PATH="$HOME/.temporal/certs/client.pem"
export TEMPORAL_KEY_PATH="$HOME/.temporal/certs/client.key"
```

Then run the application with the `cloud` profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=cloud
```

### Option 2: Using a .env File

Create a `.env` file in the project root (DO NOT commit this file):

```bash
# .env file
TEMPORAL_ADDRESS=your-namespace.tmprl.cloud:7233
TEMPORAL_NAMESPACE=your-namespace.account-id
TEMPORAL_CERT_PATH=/Users/yourname/.temporal/certs/client.pem
TEMPORAL_KEY_PATH=/Users/yourname/.temporal/certs/client.key
```

Add `.env` to your `.gitignore`:

```bash
echo ".env" >> .gitignore
```

Load the environment and run:

```bash
# Load environment variables
set -a
source .env
set +a

# Run with cloud profile
mvn spring-boot:run -Dspring-boot.run.profiles=cloud
```

### Option 3: Direct Configuration

Edit `application-cloud.yml` to hardcode values (NOT recommended for production):

```yaml
spring:
  temporal:
    connection:
      target: your-namespace.tmprl.cloud:7233
      target.namespace: your-namespace.account-id
      mtls:
        enabled: true
        cert-chain: /path/to/client.pem
        private-key: /path/to/client.key
```

## Running the Application

### With Cloud Profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=cloud
```

### With Environment Variables (no profile needed):

If you've set environment variables, you can also run without the cloud profile:

```bash
# The default application.yml now supports environment variables
mvn spring-boot:run
```

The application will use the environment variables if set, otherwise fall back to local defaults.

## Verifying the Connection

When the application starts, you should see log messages like:

```
Created WorkflowServiceStubs for channel: ManagedChannelOrphanWrapper{
  delegate=ManagedChannelImpl{
    logId=1,
    target=your-namespace.tmprl.cloud:7233
  }
}
```

If you see connection errors, verify:

1. **Namespace address is correct** - Should end with `.tmprl.cloud:7233`
2. **Namespace format** - Should be `namespace.account-id`
3. **Certificates are valid** - Not expired and match your namespace
4. **Certificate paths are correct** - Files exist and are readable
5. **Port 7233 is accessible** - Check firewall/network settings

## Example: Complete Setup

Here's a complete example using a fictional namespace:

```bash
# 1. Set environment variables
export TEMPORAL_ADDRESS="my-namespace.a2b3c.tmprl.cloud:7233"
export TEMPORAL_NAMESPACE="my-namespace.a2b3c"
export TEMPORAL_CERT_PATH="$HOME/.temporal/certs/client.pem"
export TEMPORAL_KEY_PATH="$HOME/.temporal/certs/client.key"

# 2. Verify certificates exist
ls -l $TEMPORAL_CERT_PATH
ls -l $TEMPORAL_KEY_PATH

# 3. Run the application
mvn spring-boot:run -Dspring-boot.run.profiles=cloud

# 4. Access the web UI
open http://localhost:3030
```

## Switching Between Local and Cloud

### For Local Development:

```bash
# Don't set TEMPORAL_* env vars, or unset them
unset TEMPORAL_ADDRESS TEMPORAL_NAMESPACE TEMPORAL_CERT_PATH TEMPORAL_KEY_PATH

# Run without cloud profile
mvn spring-boot:run
```

### For Cloud:

```bash
# Set TEMPORAL_* env vars (see above)

# Run with cloud profile
mvn spring-boot:run -Dspring-boot.run.profiles=cloud
```

## Security Best Practices

1. **Never commit certificates to git**
   - Add `*.pem`, `*.key`, `.env` to `.gitignore`

2. **Store certificates outside the project**
   - Use `~/.temporal/certs/` or similar

3. **Use environment variables**
   - Don't hardcode credentials in configuration files

4. **Rotate certificates regularly**
   - Download new certificates from Temporal Cloud periodically

5. **Use different namespaces for dev/staging/prod**
   - Configure via environment variables per environment

## Troubleshooting

### Error: "UNAVAILABLE: io exception"

**Cause:** Cannot connect to Temporal Cloud
**Solutions:**
- Verify `TEMPORAL_ADDRESS` is correct
- Check network/firewall allows port 7233
- Verify certificates are not expired

### Error: "PERMISSION_DENIED"

**Cause:** mTLS authentication failed
**Solutions:**
- Verify certificate files exist and are readable
- Check certificate is valid for the namespace
- Ensure certificate hasn't expired
- Regenerate certificates in Temporal Cloud if needed

### Error: "NOT_FOUND: Namespace not found"

**Cause:** Incorrect namespace name
**Solutions:**
- Verify `TEMPORAL_NAMESPACE` matches exactly in Temporal Cloud
- Format should be `namespace.account-id`
- Check for typos or extra spaces

### Application starts but workflows don't execute

**Cause:** Task queue mismatch
**Solutions:**
- Verify the task queue name in `application.yml` matches what you're using
- Default is `DemoTaskQueue`
- Check worker is registered and polling

## Additional Resources

- [Temporal Cloud Documentation](https://docs.temporal.io/cloud)
- [Temporal Java SDK - mTLS Configuration](https://docs.temporal.io/dev-guide/java/security#mtls)
- [Spring Boot Temporal Integration](https://github.com/temporalio/sdk-java/tree/master/temporal-spring-boot-autoconfigure-alpha)
