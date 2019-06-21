# Ticket Master ENTERPRISE Edition
Issue-Tracking-Application built with Java EE

## 1. Wildfly-Configuration

1. Start Wildfly Server (`%WILDFLY-HOME%/bin/standalone.sh or .bat`)
2. Start jboss-cli (`%WILDFLY-HOME%/bin/jboss-cli.sh or .bat`)
3. Connect to Wildfly Server via command `connect`
4. Enter the following commands:
`### MySQL Driver
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
  password="61fni@19", \
)
```

### Security Subsystem
```
./subsystem=security/security-domain=ticketsDomain/:add

./subsystem=security/security-domain=ticketsDomain/authentication=classic:add( \
  login-modules=[{code=Database, flag=Required, module-options={ \
    dsJndiName="java:jboss/datasources/ticketsDS", \
    principalsQuery="SELECT password from User WHERE login_id=?", \
    rolesQuery="SELECT r.name, 'Roles' FROM Role r, User_Role ur, User u WHERE r.id = ur.roles_id AND u.id = ur.User_id AND u.login_id = ?", \
    hashAlgorithm=SHA-256, \
    hashEncoding=base64, \
	hashUserPassword=true, \
	hashStorePassword=false, \
}}])
```

## 2. Database-Configuration

After setting up wildfly, you need to configure your MySQL Server according to the previous wildfly-configuration!

1. Create new database-schema called `tickets`
2. Create new user called `inf16` with password `61fni@19`

Make sure the user has all necessary rights to access the database-schema!