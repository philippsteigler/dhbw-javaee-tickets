# Ticket System DB

## Configuration

1. Start Wildfly Server (%WildflyHome%/bin/standalone.sh or .bat)
2. Start jboss-cli (%WildflyHome%/bin/jboss-cli.sh or .bat)
3. Connect to Wildfly Server via command "connect"
4. Enter the following commands:

### MySQL Driver
```
/subsystem=datasources/jdbc-driver=mysql/:add( \
  driver-module-name=com.mysql, \
  driver-name=mysql, \
  driver-class-name=com.mysql.jdbc.Driver, \
  xa-datasource-class=com.mysql.jdbc.jdbc2.optional.MysqlXADataSource)
```

### Data Source
```
/subsystem=datasources/data-source=ticketsDS/:add( \
  jndi-name=java:jboss/datasources/ticketsDS, \
  connection-url=jdbc:mysql://localhost:3306/tickets, \
  driver-name=mysql, \
  driver-class=com.mysql.jdbc.Driver, \
  enabled=true, \
  jta=true, \
  use-java-context=true, \
  user-name="inf16", \
  password="61fni", \
)
```

### Security Subsystem
```
./subsystem=security/security-domain=ticketsDomain/:add

./subsystem=security/security-domain=ticketsDomain/authentication=classic:add( \
  login-modules=[{code=Database, flag=Required, module-options={ \
    dsJndiName="java:jboss/datasources/ticketsDS", \
    principalsQuery="SELECT password from user WHERE login_id=?", \
    rolesQuery="SELECT r.name, 'Roles' FROM role r, user_role ur, user u WHERE r.id = ur.roles_id AND u.id = ur.User_id AND u.login_id = ?", \
    hashAlgorithm=SHA-256, \
    hashEncoding=base64, \
	hashUserPassword=true, \
	hashStorePassword=false, \
}}])
```
