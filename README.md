# 프로젝트
사용자에게 제공되는 웹페이지에서 개인폴더를 생성해 사진 및 파일을 업로드 다운로드 기능을 제공하는 서비스

## 상세기술
상세 기술 스펙 및 구현
### **Front-end**:
React: 사용자 인터페이스를 구축하고 사용자 경험을 향상시키는 프론트엔드 라이브러리.
MetaMask: 사용자는 MetaMask를 통해 안전하게 트랜잭션을 발생시킬 수 있습니다. 테스트 계정에는 로그인 시 자동으로 10,000 ETH가 지급됩니다.
### **Back-end**:
Spring Boot with HTTPS: 애플리케이션의 로직을 처리하고, HTTPS를 통한 안전한 데이터 전송을 지원합니다.
Hardhat Node on EC2: Ethereum 블록체인 개발을 위해 EC2에서 8545 포트에서 동작합니다. 이는 로드 밸런서를 통해 http://www.fufuanfox.com에서 접근 가능합니다.
### **인증**:
DID (Decentralized Identifiers): 사용자는 회원 가입 시 DID를 발급받아서 EC2(Hardhat Node)에 저장됩니다.
AWS Cognito: Cognito를 통해 로그인이 가능하며, 추후에는 Backend 인증 및 DID만으로도 로그인을 지원할 예정입니다.
Verifiable Credentials and Presentations: 사용자는 발급 받은 인증서를 서명하여 신뢰성 있는 인증 절차를 거칩니다.
### **데이터 보안**:
Password Salting and Hashing: 비밀번호는 유니크한 Salt 값과 SHA-256을 사용해 해시되어 AWS RDS에 저장됩니다.
AWS KMS for JWT: JWT 토큰 생성과 검증에 사용되는 키는 AWS KMS를 통해 관리됩니다. 키의 알고리즘은 RSA4096입니다.
### **데이터 저장**:
AWS RDS for User Info: 회원 정보는 AWS RDS에 안전하게 저장됩니다.
AWS S3 for Files: 사용자는 인증 후 자신의 폴더에 파일을 업로드하거나 다운로드할 수 있습니다. 이는 CDN을 통해 빠르게 제공됩니다.
AWS Content Delivery Network (CDN): 업로드된 파일과 이미지는 AWS S3에 저장되고, 이러한 리소스는 CDN을 통해 전달됩니다. CDN을 사용하면 사용자가 서버에 직접 접근하는 것보다 훨씬 빠르게 리소스를 불러올 수 있습니다.
### **캐시 전략**:
AWS Redis: 사용자 정보와 Refresh Token이 키-값 쌍으로 저장됩니다. TTL(Time To Live)은 Refresh Token의 만료 시간과 동기화되어, 토큰 만료 시 자동으로 캐시에서 제거되어 보안성이 향상됩니다.
### **로그**:
EC2 Logging: 메타마스크를 통한 트랜잭션 로그는 EC2에서 실시간으로 확인이 가능합니다.
### **추가 기능**:
SNS 로그인 (Kakao, Google, Apple): 각 플랫폼에서 1회용 토큰을 발급 받아 서버에서 인증합니다. 인증 후에는 JWT 형태로 개인 토큰을 발급받습니다.
### **인증과 토큰 관리**:
JWT (JSON Web Tokens): 사용자 인증을 위해 JWT를 사용합니다. Access Token과 Refresh Token의 두 가지 토큰을 발급해 관리합니다.<br>
Access Token: SNS 로그인 (Kakao, Google, Apple)에서 전달받은 userID를 키로 사용해 Payload에 포함하여 발급됩니다. 이 토큰은 사용자가 Resource 서버에 요청할 때 userID로 구분하여 처리됩니다.<br>
Refresh Token: 각 Device ID와 userID를 결합해 키로 사용합니다. 이 키와 Refresh Token의 값이 AWS Redis에 저장됩니다.<br>
Token Renewal: 사용자가 Refresh Token을 통해 Access Token을 재발급받을 경우, 사용자로부터 받은 Access Token에서 userID를 추출하고, 클라이언트의 Device ID를 함께 사용하여 Redis에서 Refresh Token을 검증합니다. 검증이 성공하면 새로운 Access Token을 발급합니다.<br>

## 사용 스마트컨트랙트 주소
Deploying contracts with the account: `0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266`<p>
SmartContract deployed to: `0xe7f1725E7734CE288F8367e1Bb143E90bb3F0512`

## 사용 언어
- TypeScript
- Java
- Solidity
- JavaScript

그 외 사용 라이브러리:
- openjdk 11
- spring boot 2.2.6
- gradle 6.3
- web3j 4.5.11
- aws sdk 1.11.819

인증:
- OAuth2.0
- JWT(JSON Web Token)
- AWS Cognito

데이터 베이스:
- MySQL
- AWS RDS
- Redis(캐시전략)
## 사용 기술
- **AWS**:
- AWS KMS, Encryption SDK, S3, EC2, CloudFront, Route53, VPC, LoadBalancer, IAM, Internet Gateway, Elastic IP, Certificate Manager (SSL), Notification Service (SNS) 
- **Node.js**: hardhat Node (EC2에서 동작 중)

기타:
- Git (버전 관리)
- SFTP
- DID (디지털 아이덴티티)
- Jasypt (보안)


## MetaMask 테스트용 계정
- Account: `0x1CBd3b2770909D4e10f157cABC84C7264073C9Ec (10000 ETH)`
- Private Key: `0x47c99abed3324a2707c28affff1267e45918ec8c3f20b8aa892e8b065d2942dd`
## MetaMask Network 
- RPC URL : `http://www.fufuanfox.com`
- Chain ID : `31337`
- 통화 기호 : `ETH`

#### 이 프로젝트는 ToyProject이므로, 몇 개의 소스코드 파일 업로드를 제외하고 구현하였습니다.

본 프로젝트는 아래의 3가지 Backend , Front, Hardhat 과 연결되어 있습니다.
- JiSeungGu/ToyProject-Backend(https://github.com/JiSeungGu/ToyProject-Backend/tree/master)
- JiSeungGu/ToyProject-hardhat(https://github.com/JiSeungGu/ToyProject-hardhat)
- JiSeungGu/ToyProject-Front(https://github.com/JiSeungGu/ToyProject-Front) 


