package com.example.projectdid.did;

import com.example.projectdid.DidDocumentBase;
import com.example.projectdid.RSA.CreateKeyPair;
import com.example.projectdid.RSA.RsaDid;
import com.example.projectdid.proof.Ed25519PresentationProof;
import com.example.projectdid.utils.Iso8601InstantTypeAdapter;
import com.example.projectdid.vc.CredentialSubject;
import com.example.projectdid.proof.Ed25519CredentialProof;
import com.example.projectdid.vc.VCDocument;
import com.example.projectdid.vp.VPDocument;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.Base58;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.gson.*;
import com.nimbusds.jose.util.Base64URL;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.threeten.bp.Clock;
import org.threeten.bp.Instant;

import javax.json.Json;
import javax.lang.model.element.Element;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static com.example.projectdid.proof.Ed25519PresentationProof.SignSubString;
import static com.example.projectdid.proof.Ed25519PresentationProof.getPropertyJson;


public class DidWorkflowTest {
    private static final CreateKeyPair rsaKey = new CreateKeyPair();
    public static void main(String[] args) throws Exception, PrivateKey.BadKeyException {
//        RSAmain();
        DidWorkflowTest();
    }
    public static void RSAmain() throws Exception {
        KeyPair keyPair = rsaKey.genRSAKeyPair();
        RsaDid Rsadid = new RsaDid("SEUNGGUDID",keyPair.getPublic(), keyPair.getPrivate());

        System.out.println("RSA 공개키  :"+Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
        System.out.println("RSA 개인키  :"+Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
//        String prikey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCX1gktzdEMEZUvw+M2LjhfTt9jam444y2SqkXl9tgJpmGS8744AGcw+6wyT5bxwAq9vHbE4Q7dshSmPzDVZzCdFEJJjLQdH+RvT8VvUVRFe7gnz91r7wLNROHkRW+U2+0s78w0gw3yU9p2dUatjxJTyFcJfxuF5BukmDiciwIw9fr0Yo1RYIK1l6iSf3mAgu7mwcQ9DL33dP30x/ZF1kjTNXWF7qQh+FuBMh+hKzDBzOOQXMrapJk0An1ccGJhO4OWZXqVIipsKtNtZFeTGltqgJdhUkGMcskr/FyjopE2AfbV78oeqjzfYjzMENasCMWolvYhxUhV3sNBJ+NNLhldAgMBAAECggEAbfSOSSFXVgoVo7WUAVSPdlXD6FbW41OBWUj9vX95C+P1nVnuUtfX4t0e4oBIq2GLZcPprsHc5idkVNBGKQTFa7uUFXdTqBPEdeeq7uZSxM7Hme0qSFOaD9TmUU0HdSGfLH+rvgt6OhcHiel7+Yw2lX3rqfDmmZuLeTLTLGpud81NAR9GgjkeZtbfdjVzswnudpNpBnp0Tm+8hREf28EENQUmHkf3JVRfmN90ifUvGRChK/X0vTNWBwvDEXYNC/xSYr/JUEa+tfO03QPysyVi3AzOrf5Vce1GSZoGGH7gBUsKxv33S9QRe26amqa60dWDyyM+PFdJBuoRCZj56OhmcQKBgQDJ7MNyIP9X512oJTk9NdcP9Up2lBBC/XDbb5e087hbYQw0cMr2hRwenaV6navs8ifCFyTQchoy4CE1Ayg7GKj+yUQXUbnvkMjCDejAg9tU2A1bRwqk70ozCqPJ/Hou3s2FLIsXyzGrcGAp5twdZ0g4HU1DmV57ZSVv9ru/UIpuswKBgQDAf1392+lJh7WIpvxDMYFAAt4ompOE9idfyr2o4ZJJlhl8nS/RW3dBwiIYKFF0BYAYGzdp1OqypqLaBhQZNHTAqIaUQyBYC63I355fSlIspWVNsZDNAfJ+fSwKn7VyFxuAbim9Ciivuqc/ydGYZ7mjyRZArZywr3YSsZwg3qhfrwKBgEEQ+BHIrD8pILbT99PPi5Nq2SnIoEzV2g37sYjvmBJp/ULQrbYuQldjOTV/pSzfAwy55HT+r46BRnIMqGQSmDIxrK1O+nlF0EsnrD6MvppiXDtzcuye5uv3m7u0jbKYvOUS62cpZMH3niUibP9UjqL2XjVQMG/Wse+YM/t7+n+dAoGBAKc0exIs2OCTzq1aTriW4awWUZ53VjgiEGV2l3OALC9a/9xsNMLnbhliZXcSXl4nAPLvgRyeYxa3A7HZoc+a7ucB+5QBErEnw9Y91zTyO5qoN+xalmpcjKQuWfkIbPUi+TJ/fiXEEz9BJFGWHkcBohCQdJHNu9MFNcxbFkC+5wXlAoGBAKeIt4aXvNU0bqm75VM48KK6FKLIuYAqpwgoU1Rhul5CuBvdoCbMSx7AbJzXCeb2DCtOKlAbxxYq+qmfA2kX0ztPvIHz8rHnd4xrTYeWiyt1uW3tO62lI+s0ezbAzGcaMs4ImozQAZK/diKCmiUt+EJkP7wianGN9kdS4w43jUg4";
//        String jangPrikey = "MIIEpQIBAAKCAQEAxoePg70UF3NmmHjgmX7tF+Q96XgNTYx/vSIJXqBM2y9toDZ+IQnF3CWdy+8aLw1v21q5d1reJTlHCi7h6xFlIfxSt3EsRS6nVsOhGkkoJu5kieH6O3yEGMGYy3WFuwABtqUntx+cN4fr0rdZpZ3DQM6ycbL3e5HnZJ34f8S98vCzKzVVkw3IthZyz12KafLM8DL3royedkN1qmI4mPvlTokv9JHKGuHkWS6XTAOvVfAHIOy985h+cM3m53424VJDiOWmBJeMGqJ/tmblDiqlmtUjTcHJDZeKZqFfP0frIZj/FpNkjtquS4VCBXhEmBgW1onBkPUBEhsrDEO3Ibh3MwIDAQABAoIBAFYG3u7wi1jAFliCpJyO7hE4w4U8EtOkSqP7sECzSKp/6kqO4mfRks3a9P4uPVPfSDWuoghwtSs/HyPv/dB9KKwp6PP5k/vkg/mqatB3m/EGw0UmSWbmhV0u4tVjNxA5qARrm4KULX64pL0Z2zxTWQznpFZ1fR0sM68bbtd6uu6ejwZKCo1ZUAPE9EMJzezDXG5b3Ht22CU6iggWRHsenumuKwfxa/ekom9CnbJ6ShqQSAFPoinRKCDPCyOYJgIznes2dM6Jd0s5g0l9hX4s2+/VQ65YEPiB/Py4l6oZC5hWh3I0zTw/tAroujgxMh/jLZ5ZQoH3Po3ar5xdI8WVF1ECgYEA+/VoSK4Qa0hgj0V1/i7TuH48Pfydwy3Vg4IzSkneSCk1TOqChPVK83P0HGl0rg+1Ti5Iv5Dr4S3BK1Tr4qgQcwjVTy4wiptvMHfJsOQtBoEuobrI4L6z0oVPPWvOnBOjNWJwIgGwhbhk7Iyu7I1pmcwZ/xE+waj2pMTG4RO4Q5ECgYEAybbDP5tdUAqLzdc4xhhEvtYfNhDmAyYvne2j26+ccswVMp1EkcrkqMn1fMptP8z3b+k4GUD4b69CTDW912MJ14rK0sBwwIpuB8EsdpvL3n5M+TEczPPrYXXO1gn4MQcwZeRvffvlRWb6xN/eZ1DvEG7fljC15V0GYnkUOJBVpIMCgYEA99tEiSricAc1Jfu4xSGfmUBlVr0pHRX7knEEr7HW/rm6+z0+xDjhUHjizQhYhdpiCtSxL/IKeOufuVbC7adS9zbbUBcqHVAmo1FTkbYFLP7rtqGedqhjEVeeyddB2nve8+cwU+PBmOn6LlF+yqmaFneXozl9uNjUOOZylnvlqmECgYEAxw39FWrbJZ6SofmuRAY06OHigPciFJ99Q+r71VIyIDL3BDCgNwMyrMJH5/LW5qv8zconGvy7bXNVCGHMSp2oW7TSPDG90rOIq0xc9Vr/Tzvx98MYecSHVnDsQNoruy4t+4722ytV8CwUcW2+StRaZiwTpmHBTmuDXm+/naYJbcMCgYEAtvyW7LUepy0rfqEjCPJQiGhfBs1wK/RB6vtJe4Og/uey5rPvuFYL144tcrA6gPxxsXiiQyKksyLJpBYJYH/MeMM3+zT7zbrAisEJsgw21DUajkz5R1NFobzSMnsnAYe9pQe8aR1pJYqCiQzkocvqYoWasb3OxwRm701VAg1R0iU=";
//        String jangPubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3SEN2U6pY5Q/+yigGWU4WX00bmGtkjCqFNgTLLisfxspEiuFQVfuwXuLsFspC8429bYbdJLoHB+NI7n0lw4e3M5GtR/pNtQ9Y+gv/QzQ4ndTe/ylnJD8KT6RcqGKLmt04ze2a699vffGPFwmYXaW0aEjZzFOrJbk0xBtsfPZrjv6wciC6fwdg9yOAONHBFLup1/6yjyfmcsktgkPTtS2R9vzrqxbUlYSacKnHWa4zu5om4NzIKnpwGODVZAS5v48t7yk4ViCQ3t+6p9eyvPaj62l2dC5LuuBjjtb8gzvHVXrJWMFkmqqcaIfM5UE7JDdbI+Apxw3Q5KgU2kyw7G4rwIDAQAB";
//        byte[] pubkey64 = Base64.getDecoder().decode("MIIBCgKCAQEAxoePg70UF3NmmHjgmX7tF+Q96XgNTYx/vSIJXqBM2y9toDZ+IQnF3CWdy+8aLw1v21q5d1reJTlHCi7h6xFlIfxSt3EsRS6nVsOhGkkoJu5kieH6O3yEGMGYy3WFuwABtqUntx+cN4fr0rdZpZ3DQM6ycbL3e5HnZJ34f8S98vCzKzVVkw3IthZyz12KafLM8DL3royedkN1qmI4mPvlTokv9JHKGuHkWS6XTAOvVfAHIOy985h+cM3m53424VJDiOWmBJeMGqJ/tmblDiqlmtUjTcHJDZeKZqFfP0frIZj/FpNkjtquS4VCBXhEmBgW1onBkPUBEhsrDEO3Ibh3MwIDAQAB");
//        System.out.println("DID   :"+Base58.encode(Hashing.sha256().hashBytes(pubkey64).asBytes()));

        //Base58로 encode된 공개키를 decode후에 공개키를 표현하기위해 base64 To String
//        byte[] publicKeyBytes2 = Base58.decode("2TuPVgMCHJy5atawrsADEzjP7MCVbyyCA89UW6Wvjp9HrAWSfRiyf4qPJsx3GaDW1UBq6f8cotpRZDJrD8aACCmpWRgXYMYjWYNAVrFS9eP71mgo1uRT9eoNL1jaac5nnBVtdLDQp1CZjm7FsoWAAXABhQkiDbB3dwcUtVUyW59UTsxM1XNuHfnk2kTRKSPUrbbABJBxuSviBv1K6CbZMrZoyoDSU8Dhqu7S1vR6PBzCfZMb3epDZBYvADKYvrqShbVS6t4e7EfdEYbVzQLo53BRaMs5VhtmgLiHc3XHfqJjWWztcu6S6Apkj1ZyN3ZwwwkDeVamfaKPmDhPm4CQs4KsrWsF4mqTjxJCtT7nEfJWAVXZEdP3ofeVvvN2df2AWreyDsHcstXmftuRGk");
//        System.out.println(Base64.getEncoder().encodeToString(publicKeyBytes2));

//        String publicKeyBytes2 = Base58.encode(pubkey64);
//        System.out.println("encode : "+publicKeyBytes2);
//        byte[] publicKeyBytes3 = Base58.decode(publicKeyBytes2);
//        System.out.println(Base64.getEncoder().encodeToString(publicKeyBytes3));
//        java.security.PublicKey puKey2 = rsaKey.getPublicKeyFromBase64String(jangPubKey);
//        System.out.println(puKey2.getFormat());
        //안드로이드 RSA 키
        //java.security.PublicKey puKey2 = rsaKey.getPublicKeyFromBase64String("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxbMK2DoutTS5EDZzSS6e3Hg6DBdsFALvXhFSGNwN0blfQfm2hSbxv1vMM5BAjcFxxyChPYFsgjw1dPlWQ7PvREWMwEgYxqQFmQjZKph+8hrXTj/vKCdnXvs2G4pRyaa33J2JJGv5FyF6R5QkB/M4BO2jBvYXzLWlYJe9dL3UD3c1d3UDtYs1/mAKgE3fpF23jTPKjIWcIDaMtGJ9VlabEdYuEHS1E9H0tShNEMbGHLLRPjnudR5UIhYTwidEsguYahq7r+WYnKgxF5CMWhlM2XJwEHuhaAmBuOIO7zPWAhU8JYGSSazcWk404bbbZNI/rBYJQips6kXx7kST5Y0cgQIDAQAB");
        //java.security.PrivateKey prKey2 = rsaKey.getPrivateKeyFromBase64String("MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDFswrYOi61NLkQNnNJLp7ceDoMF2wUAu9eEVIY3A3RuV9B+baFJvG/W8wzkECNwXHHIKE9gWyCPDV0+VZDs+9ERYzASBjGpAWZCNkqmH7yGtdOP+8oJ2de+zYbilHJprfcnYkka/kXIXpHlCQH8zgE7aMG9hfMtaVgl710vdQPdzV3dQO1izX+YAqATd+kXbeNM8qMhZwgNoy0Yn1WVpsR1i4QdLUT0fS1KE0QxsYcstE+Oe51HlQiFhPCJ0SyC5hqGruv5ZicqDEXkIxaGUzZcnAQe6FoCYG44g7vM9YCFTwlgZJJrNxaTjThtttk0j+sFglCKmzqRfHuRJPljRyBAgMBAAECggEAAqnNLjlHbo2/pfwjzLM19OiiOI46uKDK/niLg3X4cjA1/C6r4qqSBGbNTw8FDZvS53C1N13GW1FKE5G106rpgOESercl7UwhnqyHt2sPP730k1NpaoAwZG9MLO4NuMKYFL7jDXRuaj07V1pDq38/bJKSeQoo6ysxx4W3ycoyYIRvpNdjW9QEwZ1MW9tlXlfGXpWXsAt8wYFByeaKB2O9xuxcJg/jubWjvRO95XrbyO4wAwUnA7EWQH4cfN5IhPO9FFFgE4otOGX8Ldv/BN2O+jfPTgkIAWzw8u9nHEYvwBw2pBfB0CqWoBLFfBmpUWNM1x0MzDctTzXzaw/wUBtLiQKBgQD0xVmOd/0dIs+0M/Oo74kvhJsfpXI4SK8/WIafSOUhwJEp5224j4xmCNG5N3KccQjhsl31Ora2AH/bs/J28lElEpDGmV+NjkRD3xSJqguLxUHV3cM49LZyk7DviQtX/vp9p27F9dpLg9ZYw1Dy7zoXSbbflrO4t/MKL4Zxoe1IiQKBgQDOxN98kl5D4OBD/FJRv6A9jeGM41P22tK3QgeZf0idCzRABhNoWo5s3bW6mnEeUIPb11jBfe+8HoJmBTYa2MV/CGR5aF1M9uqXZpwotnufb4RbwZw7zxOYCweSiPno9U42REFv0MnRj6OEu4PVhmoGHw9XHeWt9W3MTbw/wS7GOQKBgA8IY9StJ913OtxtaGSSuZEyirhp19b/F8xDKplzx6bIBeJV9VYlctD9b6v5bM+Q3aPG6aBbB8erxi5/IcAOZ89oEX4xAz5VY3nrpH5D9EuURNPWt9uyo9Xrni1H9GtmHIpFEzeTPuHuaeavsDQLXzz46QNYZRdOLN4ORyqJcGJJAoGBAL9ZvqFgK1CSCbmhBuWUe0y7wuJJdujadWsW9kV9X8vGFKKzfL50H55sR8PbLZgqVvzae88FJxlm+rJDXGDX/12ifKlGhiG12EAou5eJ2TohT9JCiL1o0+8/NdDMZsrKSYk6/XvnMv52+ZLanhS7Ad+MWvv6NRYaLXSG9BHkS88xAoGAKPwXNjf1ujFczgjJDaYCtgyeYTTSQrdPWuQOIIz0iQwpHtjBXEsLTPD6teietYb/kllD4KDa+IyRRklcJYxbfzyiCG/Rgw3DlSMYes8+uTpIbrxNKOJgQAfm11rlCxxQdfUA+pFqdc3aOGuzYRQHLyt4xfE+y9KiuQAysn8cBjY=");


        //========================================================================================================
        DidDocumentBase doc = Rsadid.generateDidDocument();
        System.out.println(doc.toRSAJson());

        Map<String, Object> claims = new HashMap<>();
        claims.put("name", "Ji Seung Gu");
        claims.put("state","employed");

        VCDocument vc = new VCDocument();
        /*
        RSA 공개키  :MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl9YJLc3RDBGVL8PjNi44X07fY2puOOMtkqpF5fbYCaZhkvO+OABnMPusMk+W8cAKvbx2xOEO3bIUpj8w1WcwnRRCSYy0HR/kb0/Fb1FURXu4J8/da+8CzUTh5EVvlNvtLO/MNIMN8lPadnVGrY8SU8hXCX8bheQbpJg4nIsCMPX69GKNUWCCtZeokn95gILu5sHEPQy993T99Mf2RdZI0zV1he6kIfhbgTIfoSswwczjkFzK2qSZNAJ9XHBiYTuDlmV6lSIqbCrTbWRXkxpbaoCXYVJBjHLJK/xco6KRNgH21e/KHqo832I8zBDWrAjFqJb2IcVIVd7DQSfjTS4ZXQIDAQAB
        RSA 개인키  :MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCX1gktzdEMEZUvw+M2LjhfTt9jam444y2SqkXl9tgJpmGS8744AGcw+6wyT5bxwAq9vHbE4Q7dshSmPzDVZzCdFEJJjLQdH+RvT8VvUVRFe7gnz91r7wLNROHkRW+U2+0s78w0gw3yU9p2dUatjxJTyFcJfxuF5BukmDiciwIw9fr0Yo1RYIK1l6iSf3mAgu7mwcQ9DL33dP30x/ZF1kjTNXWF7qQh+FuBMh+hKzDBzOOQXMrapJk0An1ccGJhO4OWZXqVIipsKtNtZFeTGltqgJdhUkGMcskr/FyjopE2AfbV78oeqjzfYjzMENasCMWolvYhxUhV3sNBJ+NNLhldAgMBAAECggEAbfSOSSFXVgoVo7WUAVSPdlXD6FbW41OBWUj9vX95C+P1nVnuUtfX4t0e4oBIq2GLZcPprsHc5idkVNBGKQTFa7uUFXdTqBPEdeeq7uZSxM7Hme0qSFOaD9TmUU0HdSGfLH+rvgt6OhcHiel7+Yw2lX3rqfDmmZuLeTLTLGpud81NAR9GgjkeZtbfdjVzswnudpNpBnp0Tm+8hREf28EENQUmHkf3JVRfmN90ifUvGRChK/X0vTNWBwvDEXYNC/xSYr/JUEa+tfO03QPysyVi3AzOrf5Vce1GSZoGGH7gBUsKxv33S9QRe26amqa60dWDyyM+PFdJBuoRCZj56OhmcQKBgQDJ7MNyIP9X512oJTk9NdcP9Up2lBBC/XDbb5e087hbYQw0cMr2hRwenaV6navs8ifCFyTQchoy4CE1Ayg7GKj+yUQXUbnvkMjCDejAg9tU2A1bRwqk70ozCqPJ/Hou3s2FLIsXyzGrcGAp5twdZ0g4HU1DmV57ZSVv9ru/UIpuswKBgQDAf1392+lJh7WIpvxDMYFAAt4ompOE9idfyr2o4ZJJlhl8nS/RW3dBwiIYKFF0BYAYGzdp1OqypqLaBhQZNHTAqIaUQyBYC63I355fSlIspWVNsZDNAfJ+fSwKn7VyFxuAbim9Ciivuqc/ydGYZ7mjyRZArZywr3YSsZwg3qhfrwKBgEEQ+BHIrD8pILbT99PPi5Nq2SnIoEzV2g37sYjvmBJp/ULQrbYuQldjOTV/pSzfAwy55HT+r46BRnIMqGQSmDIxrK1O+nlF0EsnrD6MvppiXDtzcuye5uv3m7u0jbKYvOUS62cpZMH3niUibP9UjqL2XjVQMG/Wse+YM/t7+n+dAoGBAKc0exIs2OCTzq1aTriW4awWUZ53VjgiEGV2l3OALC9a/9xsNMLnbhliZXcSXl4nAPLvgRyeYxa3A7HZoc+a7ucB+5QBErEnw9Y91zTyO5qoN+xalmpcjKQuWfkIbPUi+TJ/fiXEEz9BJFGWHkcBohCQdJHNu9MFNcxbFkC+5wXlAoGBAKeIt4aXvNU0bqm75VM48KK6FKLIuYAqpwgoU1Rhul5CuBvdoCbMSx7AbJzXCeb2DCtOKlAbxxYq+qmfA2kX0ztPvIHz8rHnd4xrTYeWiyt1uW3tO62lI+s0ezbAzGcaMs4ImozQAZK/diKCmiUt+EJkP7wianGN9kdS4w43jUg4
        RSA DID    :443Jiqe3H9WbESyHJTAYxiudBH22YoeogzSH9gEfBkhv
        publicKeyBase58 : 2TuPVgMCHJy5atawrsADEzjP7MCVbyyCA89UW6Wvjp9HrAWSfRiyf4qPJsx3GaDW1UBq6f8cotpRZDJrD8aACCmpWRgXYMYjWYNAVrFS9eP71mgo1uRT9eoNL1jaac5nnBVtdLDQp1CZjm7FsoWAAXABhQkiDbB3dwcUtVUyW59UTsxM1XNuHfnk2kTRKSPUrbbABJBxuSviBv1K6CbZMrZoyoDSU8Dhqu7S1vR6PBzCfZMb3epDZBYvADKYvrqShbVS6t4e7EfdEYbVzQLo53BRaMs5VhtmgLiHc3XHfqJjWWztcu6S6Apkj1ZyN3ZwwwkDeVamfaKPmDhPm4CQs4KsrWsF4mqTjxJCtT7nEfJWAVXZEdP3ofeVvvN2df2AWreyDsHcstXmftuRGk
        */
        vc.setIssuer("did:TOYPROJECT:443Jiqe3H9WbESyHJTAYxiudBH22YoeogzSH9gEfBkhv");
        Instant instant = Instant.now().plusSeconds(32400);
        vc.setIssuanceDate(instant);
        vc.setValidFrom(instant);
        vc.setValidUntil(instant.plusSeconds(2592000));
        vc.addCredentialSubject(
                //3. VC Subject 정의
                new CredentialSubject("BlockChainDevelopment","JM","JiSeungGu"
                        ,"01047628287","Y")
        );
        //Issuer의 개인키를 가져와서 VC서명
//        java.security.PrivateKey RSAprKey = rsaKey.getPrivateKeyFromBase64String(jangPrikey);
//        System.out.println("RSA 개인키  :"+Base64.getEncoder().encodeToString(RSAprKey.getEncoded()));

        //5. Ed25519CredentialProof
        Ed25519CredentialProof proof = new Ed25519CredentialProof("did:TOYPROJECT:443Jiqe3H9WbESyHJTAYxiudBH22YoeogzSH9gEfBkhv");

        //toNormalizedJson 설명 :
//        proof.sign(null,RSAprKey, vc.toNormalizedJson(true));
        proof.sign(null,keyPair.getPrivate(), vc.toNormalizedJson(true));
        vc.setProof(proof);
        System.out.println("===VC Document===");
        System.out.println(vc.toNormalizedJson(true));
        System.out.println(vc.toNormalizedJson(false));
        System.out.println("=================");

        java.security.PublicKey puKey = rsaKey.getPublicKeyFromBase64String(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
//      proof.unsign(null,puKey,vc.toNormalizedJson(false));
        Boolean VCVerify= proof.unsign(null,puKey,vc.toNormalizedJson(false));
        System.out.println("[ 검증 결과 :"+VCVerify+" ]");
    }
    public static void DidWorkflowTest() throws PrivateKey.BadKeyException, Exception {
        // DID:base58 -> byte -> hash -> publickey 순서인듯
        PrivateKey privateKey = Did.generateDidRootKey();
        System.out.println("공개키  :"+Base64.getEncoder().encodeToString(privateKey.getPublicKey().toBytes()));
        System.out.println("DID   :"+Base58.encode(Hashing.sha256().hashBytes(privateKey.getPublicKey().toBytes()).asBytes()));
        Did did = new Did("TOYPROJECT",privateKey.getPublicKey(),privateKey);
        DidDocumentBase doc = did.generateDidDocument();

        // 개인키 앞에 prefix로 붙은 이유는 무엇인지 ?
        // Ed25519 key Create
        Ed25519KeyPairGenerator keyPairGenerator = new Ed25519KeyPairGenerator();
        keyPairGenerator.init(new Ed25519KeyGenerationParameters(new SecureRandom()));
        AsymmetricCipherKeyPair keyPair = keyPairGenerator.generateKeyPair();
        Ed25519PrivateKeyParameters privateKeyEd25519 = (Ed25519PrivateKeyParameters) keyPair.getPrivate();
        Ed25519PublicKeyParameters publicKeyEd25510 = (Ed25519PublicKeyParameters) keyPair.getPublic();

        System.out.println("개인키 prefix : 302e020100300506032b657004220420");
        System.out.println("개인키        :"+privateKey.toString());
        System.out.println("공개키        :"+Base64.getEncoder().encodeToString(privateKey.getPublicKey().toBytes()));
        System.out.println(doc.toJson());
        System.out.println("Document");

        // VC Create
        // 1. Issuer에 전달할 Claims(기본 정보 생성)
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", "Ji Seung Gu");
        claims.put("state","employed");


        //2. VC를 발급해주기 위해서 issuer의 DID를 넣어야 하지만 임시데이터 사용
        VCDocument vc = new VCDocument();
        // holder DID :
        // issuer DID : did:TOYPROJECT:3dMT3qwyrubqubDvDDF8CH7xTLtSEEV74N31CBM3Nq1i
        /*
        공개키  :mN5R54z5vPEHQgjOXpUhpCPyuHHVuKlSavSYZT6Qu9I=
        DID   :3dMT3qwyrubqubDvDDF8CH7xTLtSEEV74N31CBM3Nq1i
        개인키  :302e020100300506032b65700422042047f1a92ec0191e0dfb1603618ee23a955074a940c74cd00a5452999742595877

        publicKeyBase58 : BR5czSwfAmzbt8Hafz9PnKJ9SFUmuAF5pTEXdu12zZUC
        */
        vc.setIssuer("did:TOYPROJECT:3PJAi2TLxTK9XG75U1a5k3b5K5YpkywoYYKFhucTLHFL");

        // Instant와 LocalDate
        // issuanceDate 시간을 넣을때 처음에는 Instant 사용했지만 Instant는 항상 UTC(+00:00)을 기준으로 하기때문에 LocalTime과는 차이가 있을수 있었음
        // 우리나라의 경우 시간대가 '+09:00'이므로 LocalDateTime을 사용하는게 맞음 but, 이미 Instant로 class가 잡혀있어서 적용이 안됨, 일단 instant시간을 추가해줬음
        // VC 발급일 부터  만료 기한은 한달(30일)
        Instant instant = Instant.now().plusSeconds(32400);
        vc.setIssuanceDate(instant);
        vc.setValidFrom(instant);
        vc.setValidUntil(instant.plusSeconds(2592000));
        vc.addCredentialSubject(
        //3. VC Subject 정의
                new CredentialSubject("BlockChainDevelopment","JM","JiSeungGu"
                ,"01047628287","Y")
        );

        //vc.setSchema는 생략 왜? -> 없으니까

        //4. Issuer의 개인키를 가져와서 VC서명
        PrivateKey VCPrivateKey = PrivateKey.fromString("302e020100300506032b65700422042047f1a92ec0191e0dfb1603618ee23a955074a940c74cd00a5452999742595877");
        System.out.println("issuer의 공개키 :"+Base64.getEncoder().encodeToString(VCPrivateKey.getPublicKey().toBytes()));

        //5. Ed25519CredentialProof
        Ed25519CredentialProof proof = new Ed25519CredentialProof("did:TOYPROJECT:3PJAi2TLxTK9XG75U1a5k3b5K5YpkywoYYKFhucTLHFL");

        //toNormalizedJson 설명 :
        proof.sign(VCPrivateKey,null, vc.toNormalizedJson(true));
        vc.setProof(proof);
        System.out.println("===VC Document===");
        System.out.println(vc.toNormalizedJson(false));
        System.out.println("=================");

        // ** VC에 대한 Hash 값 디비 저장해야함 그러므로 VC HASH return 이 기능으로  있어야함.
        // ** proofvalue 서명 풀어봐야함. -> 풀었음
        //      0. relayServer 측에서 도큐먼트 조회후 공개키를 가져와야하는데 공개키는 Document 형태 안에서 Base58로 암호화 되어 있음 풀어주는 과정이 필요
        //      0 - 1 도큐먼트에서 추출
        byte[] publicKeyBytes = Base58.decode("BR5czSwfAmzbt8Hafz9PnKJ9SFUmuAF5pTEXdu12zZUC");
        PublicKey publicKey =  PublicKey.fromBytes(publicKeyBytes);
        System.out.println("publickey :"+Base64.getEncoder().encodeToString(publicKey.toBytes()));

        //      1. VC양식에서 proofValue(= 서명 데이터) 포함한 문서 전달
        //      2. 전달된 문서에서 서명되어있는 ProofValue값 , ProofValue를 제외한 Json Data,  VCBase Json Data로 나눔
        //      3. ProofValue를 제외한 JsonData 와 VCBase Json Data를 이용하여 jwsSigningInput생성
        //      4. ProofValue(  ProofValue에는 jwsheader가 붙여져 있기때문에 제거 후)와 jwsSigningInput, PublicKey를 이용해서 UnSign
//                 proof.unsign(VCPrivateKey.getPublicKey(),vc.toNormalizedJson(false));
        Boolean VCVerify= proof.unsign(publicKey,null,vc.toNormalizedJson(false));
        System.out.println("[ VC 검증 결과 :"+VCVerify+" ]");

        //VP Request - Metadium 양식 없는데?..

        System.out.println("[===== VP Start =====]");

        // VP 시작
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Instant.class, Iso8601InstantTypeAdapter.getInstance())
                .create();
        VPDocument vp = new VPDocument();
        JsonParser parser = new JsonParser();
        //여기서는 vc false가 맞는게 proof까지 있는 데이터가 와야하니까 false로 넣어줘야함
        // 다만 holder측에서는 vc클래스가 없기때문에 String형태로 넘겨줄수 있게 구현해야함. element형식으로
        JsonElement element = parser.parse(vc.toNormalizedJson(false));
        gson.toJson(element);
        vp.addVC(element);
//        vp.addVC(element);

        //TODO challenge update
        Ed25519PresentationProof VPproof = new Ed25519PresentationProof(doc.getId(),"99612b24-63d9-11ea-b99f-4f66f3e4f81a");
        VPproof.sign(privateKey,vp.toNormalizedJson(true));
        vp.setProof(VPproof);

//        System.out.println("VP 출력 ");
//        System.out.println(vp.toNormalizedJson(false));


        //TODO 블록체인 Document PubkeyBase58 추출 후 String 변환
        JsonElement HolderPupelement = getPropertyJson(doc.toJson(),"publicKey");
        HolderPupelement = getPropertyJson(HolderPupelement.getAsJsonArray().get(0).toString(),"publicKeyBase58");
        String HolderPupKey = SignSubString(HolderPupelement.toString()).replaceAll("\"","");

        byte[] publicKeyBytesVP = Base58.decode(HolderPupKey);
        PublicKey publicKeyVP =  PublicKey.fromBytes(publicKeyBytesVP);

        Boolean VPVerify= VPproof.unsign(publicKeyVP,vp.toNormalizedJson(false));
        System.out.println("[ VP 검증 결과 :"+VPVerify+" ]");

        //** Challenge 추가
        String challenge = UUID.randomUUID().toString();
    }
}



