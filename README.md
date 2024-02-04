# Monkey

**v1.0.0**

Verifier system to check the validity of the email inserted by the user. This system works with 
<a href="https://james.apache.org/download.cgi">James Apache</a> service as SMTP server

## Implementation

Add the JitPack repository to your build file

### Gradle

- Add it in your root build.gradle at the end of repositories

  #### Gradle (Short)

    ```gradle
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
    ```

  #### Gradle (Kotlin)

    ```gradle
    repositories {
        ...
        maven("https://jitpack.io")
    }
    ```

- Add the dependency

  #### Gradle (Short)

    ```gradle
    dependencies {
        implementation 'com.github.N7ghtm4r3:Monkey:1.0.0'
    }
    ```

  #### Gradle (Kotlin)

    ```gradle
    dependencies {
        implementation("com.github.N7ghtm4r3:Monkey:1.0.0")
    }
    ```

### Maven

- Add it in your root build.gradle at the end of repositories

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
- Add the dependency

```xml
<dependency>
    <groupId>com.github.N7ghtm4r3</groupId>
  <artifactId>Monkey</artifactId>
  <version>1.0.0</version>
</dependency>
```

## 🛠 Skills
- Java

## Usages

### SMTP server configuration

#### Linux based

You need to download the Linux <a href="https://github.com/N7ghtm4r3/Monkey/tree/main/james/linux">zip folder</a> and then extract it.

To launch the **SMTP** service you need to run the shell script in:

``` bash
james-server-spring-app-3.8.0
    |-- bin
        |-- run.sh
```

Then **at the first launch** you will need to add a domain and a user to use **SMTP** server and the **Monkey**'s service:

``` bash
# Pathname of the script
james-server-spring-app-3.8.0
    |-- bin
        |-- james-cli.sh
        
# Add a domain
./james-cli.sh adddomain your_domain.any_first_level_domain # e.g -> monkey.tecknobit

# Add a user        
./james-cli.sh adduser user@your_domain.any_first_level_domain <user_password> # e.g user@monkey.tecknobit root
```

#### Windows based

You need to download the Windows <a href="https://github.com/N7ghtm4r3/Monkey/tree/main/james/windows">zip folder</a> and then extract it.

To launch the **SMTP** service you need to run the shell script in:

``` bash
james-server-spring-app-3.8.0
    |-- bin
        |-- run.bat
```

Then **at the first launch** you will need to add a domain and a user to use **SMTP** server and the **Monkey**'s service:

``` bash
# Pathname of the script
james-server-spring-app-3.8.0
    |-- bin
        |-- james-cli.bat
        
# Add a domain
.\james-cli.bat AddDomain your_domain.any_first_level_domain # e.g -> monkey.tecknobit

# Add a user        
.\james-cli.bat AddUser user@your_domain.any_first_level_domain <user_password> # e.g user@monkey.tecknobit root
```

### Monkey workflow

#### Init the verifier

``` java
MonkeyVerifier monkeyVerifier = new MonkeyVerifier(
        "host_where_running_smtp_service", 
        smtp_port, // Default value: 25
        // NUMBERS, CHARACTERS or ALPHANUMERIC
        VerificationCodeType, 
        // FIVE_MINUTES, FIFTEEN_MINUTES, THIRTY_MINUTES, ONE_HOUR or ALWAYS_KEEP_VALID (is the dafault value)
        KeepEmailValid, 
        "user@monkey.tecknobit", 
        "root"
);
```

#### Plain verification email 

``` java
monkeyVerifier.sendPlainVerificationEmail(
        "from_text", 
        "email_subject", 
        "email_body",
        "first_recipient@monkey.tecknobit", "second_recipient@external.domain"
);
```

#### Verification email with the default Monkey template

``` java
// The colors scheme
MonkeyColorsScheme monkeyColorsScheme = new MonkeyColorsScheme(
        "the_primary_color_value",
        "the_secondary_color_value",
        "the_tertiary_color_value",
        "the_texts_color_value"
);

// The logo details
MonkeyLogo monkeyLogo = new MonkeyLogo(
        "link_to_open_when_the_logo_is_clicked",
        "url_of_the_logo_to_insert"
);

// The texts to use in the template
MonkeyTextTemplate monkeyTextTemplate = new MonkeyTextTemplate(
        "the_title_of_the_email_to_insert",
        "the_description_of_the_email_to_insert",
        "tag_for_the_text_of_the_email_footer_to_insert",
        "tag_for_the_reasons_text_why_the_email_was_sent"
);

// The template object to create the complete template
MonkeyTemplate monkeyTemplate = new MonkeyTemplate(
        monkeyColorsScheme,
        monkeyLogo,
        monkeyTextTemplate
);

// Send the verification email
monkeyVerifier.sendDefaultTemplateVerificationEmail(
        "from_text",
        "email_subject",
        monkeyTemplate,
        "first_recipient@monkey.tecknobit", "second_recipient@external.domain"
);
```

#### Verification email with a custom template

``` java
monkeyVerifier.sendPlainVerificationEmail(
        "pathname_of_the_template",
        "from_text", 
        "email_subject", 
        "email_body",
        "first_recipient@monkey.tecknobit", "second_recipient@external.domain"
);
```

#### Verify the code sent by the user

``` java
monkeyVerifier.verifyCodeSent(
        "email_of_the_user_to_check",
        "verification_code_sent_by_the_user",
        new MonkeyVerificationActions() {
            @Override
            public void onSuccess() {
                // The verification codes match -> ON_SUCCESS_WORKFLOW         
            }

            @Override
            public void onFailure() {
                // The verification codes don't match or 
                // the email is expired -> ON_FAILURE_WORKFLOW    
            }
        }
);
```

## Authors

- [@N7ghtm4r3](https://www.github.com/N7ghtm4r3)

## Support

If you need help using the library or encounter any problems or bugs, please contact us via the following links:

- Support via <a href="mailto:infotecknobitcompany@gmail.com">email</a>
- Support via <a href="https://github.com/N7ghtm4r3/Monkey/issues/new">GitHub</a>

Thank you for your help! 

## Badges

[![](https://img.shields.io/badge/Google_Play-414141?style=for-the-badge&logo=google-play&logoColor=white)](https://play.google.com/store/apps/developer?id=Tecknobit)
[![Twitter](https://img.shields.io/badge/Twitter-1DA1F2?style=for-the-badge&logo=twitter&logoColor=white)](https://twitter.com/tecknobit)

[![](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.oracle.com/java/)

[![](https://jitpack.io/v/N7ghtm4r3/Monkey.svg)](https://jitpack.io/#N7ghtm4r3/Monkey)

## Donations

If you want support project and developer

| Crypto                                                                                              | Address                                        | Network  |
|-----------------------------------------------------------------------------------------------------|------------------------------------------------|----------|
| ![](https://img.shields.io/badge/Bitcoin-000000?style=for-the-badge&logo=bitcoin&logoColor=white)   | **3H3jyCzcRmnxroHthuXh22GXXSmizin2yp**         | Bitcoin  |
| ![](https://img.shields.io/badge/Ethereum-3C3C3D?style=for-the-badge&logo=Ethereum&logoColor=white) | **0x1b45bc41efeb3ed655b078f95086f25fc83345c4** | Ethereum |

If you want support project and developer
with <a href="https://www.paypal.com/donate/?hosted_button_id=5QMN5UQH7LDT4">PayPal</a>

Copyright © 2024 Tecknobit
