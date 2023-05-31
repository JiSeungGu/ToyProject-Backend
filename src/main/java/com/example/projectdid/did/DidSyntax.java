package com.example.projectdid.did;

import java.util.Arrays;

public class DidSyntax {
    public static final String DID_PREFIX = "did";


    public static final String DID_DOCUMENT_CONTEXT = "https://www.w3.org/ns/did/v1";
    public static final String DID_METHOD_SEPARATOR = ":";
    public static final String DID_PARAMETER_SEPARATOR = ";";
    public static final String DID_PARAMETER_VALUE_SEPARATOR = "=";
    public enum Method {
        TOYPROJECT("TOY");

        private String method;

        Method(final String methodName) {
            this.method = methodName;
        }
        public static Method get(final String methodName) {
            return Arrays.stream(values())
                    .filter(m -> m.method.equals(methodName)).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid DID method name: " + methodName));
        }

    }
}

