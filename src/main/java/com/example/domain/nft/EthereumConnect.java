package com.example.domain.nft;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.CommitmentPolicy;
import com.amazonaws.encryptionsdk.CryptoResult;
import com.amazonaws.encryptionsdk.kms.KmsMasterKey;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EthereumConnect {
    private static String AlchemyKey = "0fa6039c52614c1c9d71f6c162961c64";
    private static Web3j web3j = Web3j.build(new HttpService("https://goerli.infura.io/v3/" + AlchemyKey));
    static AwsServiceConnect awsserviceconnect;


    public static void main(String[] args) throws Exception {
//        Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().send();
//        System.out.println(web3ClientVersion.getWeb3ClientVersion());


//        ERC20Transfer();
//        ERC721OwnerOf();    }
    }

    public static String EthCreateWallet() throws Exception {
        System.out.println("key create");
        String password = "";

        ECKeyPair keyPair = Keys.createEcKeyPair();
        WalletFile walletFile = Wallet.createStandard(password, keyPair);
        System.out.println("walletFile :" + walletFile);
        String privateKey = keyPair.getPrivateKey().toString(16);
        String publicKey = keyPair.getPublicKey().toString(16);

        String waddress = "0x" + walletFile.getAddress();
        System.out.println("waddress  : " + waddress);
        System.out.println("publicKey  : " + publicKey);
        System.out.println("privateKey  : " + privateKey);
        System.out.println("privateKey.getBytes(StandardCharsets.UTF_8) :" + privateKey.getBytes(StandardCharsets.UTF_8));
        //Wallet  : 0x0C94A0E9E4a5A3e2829389b1572b8176209670c1
        //Private : 6fb4c3dd41ff6dcdd6bb372f1b2ba252c2da5f51f42212b848f858db7e35de01
        String cipherText = awsserviceconnect.encryptAndDecrypt_EncryptionSDK("6fb4c3dd41ff6dcdd6bb372f1b2ba252c2da5f51f42212b848f858db7e35de01".getBytes(StandardCharsets.UTF_8), "ETH", "wallet");
        System.out.println("cipherTexxt :" + cipherText);
        awsserviceconnect.Aws_UsingS3("0x0C94A0E9E4a5A3e2829389b1572b8176209670c1", cipherText, "ETH", "wallet");
    }

    public static String ERC721OwnerOf() throws ExecutionException, InterruptedException {
        String contractAddress = "0x7EF50423162d8732c974109b1D51d5954Fafb77D";

        //Function에 넣는 매개변수를 좀더 알아야할듯 구체적으로
        Function function = new Function(
                "ownerOf",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(1)),
                Collections.<TypeReference<?>>emptyList());
        String encodedFunction = FunctionEncoder.encode(function);

        //참고용
//        List<Type> inputParameters = new ArrayList<>();
//        inputParameters.add(new Uint256(1));
//
//        Function function = new Function("ownerOf",
//                inputParameters, Collections.<TypeReference<?>>emptyList());
//        String encodedFunction = FunctionEncoder.encode(function);

        EthCall response = web3j.ethCall(Transaction.createEthCallTransaction("0x0C94A0E9E4a5A3e2829389b1572b8176209670c1"
                , "0x7EF50423162d8732c974109b1D51d5954Fafb77D"
                , encodedFunction), DefaultBlockParameterName.LATEST).sendAsync().get();
        List<Type> someTypes = FunctionReturnDecoder.decode(
                response.getValue(), function.getOutputParameters());

        return "";
    }

    public static String ERC20Transfer() throws Exception {
        String privateKey = "6fb4c3dd41ff6dcdd6bb372f1b2ba252c2da5f51f42212b848f858db7e35de01";
        //자격 증명
        Credentials credentials = Credentials.create(privateKey);
        System.out.println("credentials.getAddress() : " + credentials.getAddress());

        /*
        TransactionReceipt transactionReceipt = Transfer.sendFunds(
        web3, credentials, "0x<address>|<ensName>",
        BigDecimal.valueOf(1.0), Convert.Unit.ETHER).send()

        BigInteger value = Convert.toWei("1.0", Convert.Unit.ETHER).toBigInteger();
        RawTransaction rawTransaction  = RawTransaction.createEtherTransaction(
             <nonce>, <gas price>, <gas limit>, <toAddress>, value);
        // send...
         */
        TransactionReceipt transactionReceipt = Transfer.sendFundsEIP1559(
                web3j,
                credentials,
                "0x0B6b5FA54eae8f691540e904CA3efD8A87fF73f5",
                BigDecimal.ONE.valueOf(0.001),
                Convert.Unit.ETHER,
                BigInteger.valueOf(8_000_000),      // gasLimit
                DefaultGasProvider.GAS_LIMIT,       // maxPriorityFeePerGas
                BigInteger.valueOf(3_100_000_000L)  // maxFeePerGase
        ).send();

        System.out.println("transactionReceipt.getTransactionHash()  :" + transactionReceipt.getTransactionHash());
        System.out.println("transactionReceipt.getType()  :" + transactionReceipt.getType());

        return "";
    }
}
