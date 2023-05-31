package com.example.projectdid.did;


import java.security.SecureRandom;

final class ThreadLocalSecureRandom {
    @SuppressWarnings("AnonymousHasLambdaAlternative")
    private static final ThreadLocal<SecureRandom> secureRandom =
            new ThreadLocal<SecureRandom>() {
                @Override
                protected SecureRandom initialValue() {
                    return new SecureRandom();
                }
            };

    private ThreadLocalSecureRandom() {}

    static SecureRandom current() {
//        System.out.println("secureRandom.get() :"+secureRandom.get());
        return secureRandom.get();

    }
}
