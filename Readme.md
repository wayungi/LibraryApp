# <span style="color:blue"> Spring project template for Service Cops company</span>
## <span style="color:red">-- This is still work in progress --</span>
 This project is a template for the company. It contains the basic structure of a Spring project, with the
necessary dependencies and configurations to start a new project.

### Dependencies
    1. Spring Boot 3.1.4
    2. Spring Data JPA
    3. Spring Security
    4. Spring Web
    5. Spring starter mail
    6. Spring quartz for scheduling tasks(cron jobs)
    7. Spring themeleaf for template engine
    8  Spring gcp for google cloud platform
    9. Spring devtools for hot reload
    10. Spring postgresql driver
    11. Spring lombok

### Configuration
#### Environment variables
The project consists of three profiles: dev, uat and prod. Each profile has its own configuration file, which is
located in the resources folder. The configuration files are named as follows: `application-{profile}.properties`.
By default, the dev profile is active. To change the active profile, you must change the value of the property in the 
`application.properties` file: `spring.profiles.active = {profile}` or edit your IDE configuration to point to the required profile.

Any configuration that is common to all profiles is located in the `application.properties` file. The configuration that is specific to each profile is located in the `application-{profile}.properties` file.
    
    NOTE: Adding a configuration to the `application-{profile}.properties` file will override the configuration in the `application.properties` file.

#### Database
The project uses PostgreSQL as the database. The database configuration is located in the `application-{profile}.properties` file.
The database configuration is as follows:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/{database_name}
spring.datasource.username={username}
spring.datasource.password={password}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=none
```
If your project uses model-database architecture, you can change the 
`spring.jpa.hibernate.ddl-auto` property to `create-drop` to create the database tables automatically or to `update` to update the database tables automatically.
With this in place, you don't need to create the tables manually.


#### Mail
The project uses the Gmail SMTP server to send emails. The mail configuration is located in the `application-{profile}.properties` file.
The mail configuration is as follows:
```properties
spring.mail.host=smtp.servicecops.com
spring.mail.port=25
spring.mail.username={username}
spring.mail.password={password}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.default-encoding=UTF-8
spring.mail.properties.protocol=smtp
spring.mail.properties.test-connection=false
```

#### Google Cloud Platform
The project uses Google Cloud Platform to store files. The configuration is located in the `application-{profile}.properties` file.
There is a bucket already created in the GCP which is used by default, you are encouraged to create your own folder in this bucket where you can store your files.
The configuration is as follows:
```properties
spring.cloud.gcp.storage.bucket={bucket_name}
spring.cloud.gcp.credentials.location={path_to_json_file}
gcp.bucket.name={name_of_your_bucket} # Please add the same name as in the file above.
```

#### Port 
This is the port you want your server to run on. The default port is 8080. You can change it in the `application-{profile}.properties` file. Or you can change it in the `application.properties` file and delete it from the profile files.
Doing this will make the port the same for all profiles.
```properties
server.port=8080
```

#### App Version
This is the version of your application. You can change it in the `application-{profile}.properties` file. Or you can change it in the `application.properties` file and delete it from the profile files.
Doing this will make the version the same for all profiles.
```properties
app.version=SC-PROJ-20230928001
```
This version is divided into four parts:
1. SC - The initials of the company.
2. PROJ - The initials of the project.
3. 20230928 - The date of the project creation.
4. 001 - The number of the release.

This will always help you to truck which version has which issue especially in the uat.

#### Moonlight
This projects provides first-hand support for moonlight as if it is part of the other dependencies. 
In the `pom.xml` you will find the following:-
```xml
<dependency>
    <groupId>com.jmsoft</groupId>
    <artifactId>Moonlight</artifactId>
    <version>1.0</version>
</dependency>
```
Which implies, you are free to start using moonlight in your project as you see fit.

### Security
The project ships in with the default authentication system based on OAuth1.0 with JWT as the transfer protocol. No sessions are maintained in the app.

### HTTP
All endpoints to this project are `POST` requests unless otherwise.
All endpoints point to the central controller that is located in the `api` package.
All endpoints as `JSON` and their definition is as follows:- 
```json
{
  "SERVICE": "{SERVICE_NAME}",
  "ACTION": "{ACTION_NAME}",
  //  the rest of the request data in the format of:-
  "KEY": "VALUE" // this can be as many as you want.
}
```

If the endpoint supports searching, then the object `SEARCH` is required in the request.
```json
{
  "SERVICE": "{SERVICE_NAME}",
  "ACTION": "{ACTION_NAME}",
  "SEARCH": {
    "KEY": "VALUE" // this can be as many as you want.
  },
//  the rest of the request data in the format of:-
  "KEY": "VALUE" // this can be as many as you want.
}
```
The search object can carry an empty object but  **MUST** be defined.

### Multipart data.
This architecture does not support Multipart data however, **_all files must sent as `base64` encodings strings_**


## PROJECT STRUCTURE
The following is the structure of the project. 

1. utils
This holds all the helpers for the project. These include file uploads, mail services, request responses, AES utilities and many more.

2. models
    i.   enums
    ii.  database(actual models to the database)
    iii. customs(custom field mappings from the database)
    iv.  jpahelpers
3. api
4. config
5. services (where all your kungfu happens)
6. repositories

### Repositories
All repository must/should extend from `JetRepository` not `JpaRepository` as `JetRepository` itself extends `JpaRepository`. This custom repository, on top of all the other features from other repositories, gives you an opportunity to 
refresh your data from the database directly. This case is handy when you have data that is auto-generated at
the database level through say `triggers` or something like that.

Usage:

```java
import com.servicecops.project.models.jpahelpers.repository.JetRepository;

//        declaration.
@Repository
public interface AccountsRepository extends JetRepository<AccountsModel, Long> {
}

    //....
//       auto-wiring it 
    private final AccountsRepository accountsRepository;
    // saving 
    AccountsModal saved = accountsRepository.save(someModal);
        accountsRepository.refresh();
// here accessing 'saved' from here onwards will consist of fresh data from the db including the auto-generated one.
```

### Handling Stored procedures
All your procedures should leave in the folder called `procedures` as independent SQL files. 
This folder should reside in the root of your project, and most probably, you will find it created for you.
If you have some stored procedures that you want to call from the Java Side,
Create for them a method in your repository like this.

```java
@Repository
public interface ReadingCycleRepository extends JetRepository<AuthorityReadingCycleModel, Long> {
    @Query(value = "select * from cycle_engine_front()", nativeQuery = true)
    String runCycle();

    @Query(value = "SELECT * FROM billing_engine_front(:customerNumber, :billingCycle)", nativeQuery = true)
    BillModel billACustomerByCycle(@Param("customerNumber") String customer_number, @Param("billingCycle") String cycle);
}
```
In the above, my first procedure is called ``cycle_engine_front()`` and it takes no parameters but returns a string.

The second procedure is called `billing_engine_front(:customerNumber, :billingCycle)` which takes on parameters and returns database model/table called `BillModel`.

### Handling exceptions in stored procedures/functions from your java side.
If your procedures raise some exceptions, there is currently no directly way of accessing these exceptions from the java side, HOWEVER, there is a simple hack to handle this.

But first, 

If your functions/stored procedures are nested and you're raising exceptions in internal functions that are called by a major 
procedure/function, in that major function, first catch all the exceptions as follows:- 

This is not a very correct procedure, but you should get the point.
```postgresql
create or replace function my_func(param1 varchar, param2 varchar) returns any
    language plpgsql
as
$$
    BEGIN
        begin
--      put all your logic here
--      including calling internal procedures that are raising exceptions
        exception when others then
            RAISE EXCEPTION ' % ', SQLERRM;
        end;
    return any;
    END;
$$;
```

As you can see, my major function catches all the exceptions raised by child functions and re-raises them all.

Back to your Java side, this can now be handled like:-
```java
        try {
//   attempting to call a procedure that actually raises the exception
            returnData = readingcycleRepository
                    .billACustomerByCycle(customerNumber, billingCycle);
        } catch (Exception e){
//    this is where we hack the solution to the above problem.
            String error= e.getCause().getCause().getLocalizedMessage();
            String[] str = StringUtils.split(error, "\n");
            String cleanError = str[0].trim();
            throw new IllegalStateException(cleanError);
        }
```
