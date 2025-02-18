package am.com.blockchain.wallet.config;

import lombok.extern.slf4j.Slf4j;
import am.com.blockchain.wallet.model.User;
import am.com.blockchain.wallet.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class DatabaseInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> {
            userRepository.save(new User("satosha",
                    "$2a$12$wBFC8U9dAuNdptU0Lku5UeQrE1U8TUAwxPmceTAl5yCJx9G3Je9Uu",
                    "15ZbsZw8zhSBToqBkAvdQzBjWeAg43htBf",
                    "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEp8v7DUU5W3tJjOSdWmjCVuf4pBuiGJdUIMWAmh9YNIrjY+3KXXn8qhtvxkLYywT/BHSKY+LEUTndAijyqc+gqw==",
                    "MIGNAgEAMBAGByqGSM49AgEGBSuBBAAKBHYwdAIBAQQg9MH2rX6OmFWS90WFyeZCwfgFvtw0Zw53wa/PfdAm/OmgBwYFK4EEAAqhRANCAASny/sNRTlbe0mM5J1aaMJW5/ikG6IYl1QgxYCaH1g0iuNj7cpdefyqG2/GQtjLBP8EdIpj4sRROd0CKPKpz6Cr",
                    "password1"));
            userRepository.save(new User("amx",
                    "$2a$12$POCV62ZkqVCJ5eoWLQ7GYeR9viEo1GLn5qNI2Eg9CyW2eQe66sFGW",
                    "1CQJ8AiLCqKWX3ESF32ouSfnaMh8RVp6bN",
                    "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEipST6EZTf3BNOfN2+K0+n" +
                            "+ERM8SDzpBMWnSphwF5B9Puj9bI8rIHCpxVvcIb7jkbJ42DACJ1IIEc92z6/xGGTw==",
                    "MIGNAgEAMBAGByqGSM49AgEGBSuBBAAKBHYwdAIBAQQglazbLLTp4GUKth6TYjWfE+xDZz" +
                            "+6AOoBcO6SKtIiDYegBwYFK4EEAAqhRANCAASKlJPoRlN" +
                            "/cE0583b4rT6f4REzxIPOkExadKmHAXkH0" +
                            "+6P1sjysgcKnFW9whvuORsnjYMAInUggRz3bPr/EYZP",
                    "password2"));
            userRepository.save(new User("miner1",
                    "$2a$12$CQutVf1AiINvFZ8V1QiHo..qk9iuv/V7x3fSXy25Fag8ZH5PBR40C",
                    "1MSt353yTmKFzZbjetUvTFDhQ7kJZKzPLo",
                    "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEX54uwKmGOMpasVkkMYIITa" +
                            "/TKzHVyZKDsjYv6UMQuQbRB141jKUQOF7uBUo/505td4n5+oZZxhog7RtuCt1qew==",
                    "MIGNAgEAMBAGByqGSM49AgEGBSuBBAAKBHYwdAIBAQQgA4qRLgQ2eormSIW8QEo+Y02JPV0MM" +
                            "+EQXORndMXN6DOgBwYFK4EEAAqhRANCAARfni7AqYY4ylqxWSQxgghNr9MrMdXJkoOyNi/pQxC5BtEHXjWMpRA4Xu4FSj/nTm13ifn6hlnGGiDtG24K3Wp7",
                    "password3"));
            userRepository.save(new User("user4",
                    "$2a$12$oFjWQ6eqBHroTMCrnRGd9.OAj6I9xrou0qQC7rRqf9v77WXE7MxtK",
                    "17Spjz7rs8b66sgAiBMQof2A44mxenJAEo",
                    "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEMO8Ry6Ix6nI+jtDo99NYBEhRR" +
                            "/pW2NJyb6wHptQJAZBGljvqnxSLMDFl1vNZjaMG0NVr+ZA+lgOjCh82GEXwEQ==",
                    "MIGNAgEAMBAGByqGSM49AgEGBSuBBAAKBHYwdAIBAQQgIoL/7ButsoZiMWhvSGFpeqJT+qzkMnn" +
                            "/svgzZ1qe4w6gBwYFK4EEAAqhRANCAAQw7xHLojHqcj6O0Oj301gESFFH" +
                            "+lbY0nJvrAem1AkBkEaWO+qfFIswMWXW81mNowbQ1Wv5kD6WA6MKHzYYRfAR",
                    "password4"));
            userRepository.save(new User("user5",
                    "$2a$12$cLENuYWp2RLuXYripLLXB.2ceLXGzjjblAtDAp7lvJ4fakikeOPEO",
                    "16LSyhKaF9sNMaVFLXCrweMFDWFk3Sw1vm",
                    "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEkMIO7H1vSAL6h95mJjpqaGq2ym8UYP0vStC9SWK" +
                            "/ICbs6rNgM9yJrfPvwODIVLPTIiJqa8DCJ7Z2QfVP+HIdyA==",
                    "MIGNAgEAMBAGByqGSM49AgEGBSuBBAAKBHYwdAIBAQQg6r3/LM" +
                            "/J1pBms6bsslisyOzGzUSqK3evhCh8cvGswNigBwYFK4EEAAqhRANCAASQwg7sfW9IAvqH3mYmOmpoarbKbxRg/S9K0L1JYr8gJuzqs2Az3Imt8+/A4MhUs9MiImprwMIntnZB9U/4ch3I",
                    "password5"));

            log.info("Initialized database with sample users.");
        };
    }
}
