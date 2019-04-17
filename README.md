# Pizza Factory Article Db

## Configuration

Start jboss-cli and enter the following commands:

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
    principalsQuery="SELECT password from appuser WHERE loginId=?", \
    rolesQuery="SELECT ar.name, 'Roles' FROM approle ar, appuser_approle auar, appuser au WHERE ar.id = auar.roles_id AND au.id = auar.AppUser_id AND au.loginId = ?", \
    hashAlgorithm=SHA-256, \
    hashEncoding=base64, \
	hashUserPassword=true, \
	hashStorePassword=false, \
}}])
```