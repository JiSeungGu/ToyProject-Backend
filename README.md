# 프로젝트
사용자에게 제공되는 웹페이지에서 개인폴더를 생성해 사진 및 파일을 업로드 다운로드 기능을 제공하는 서비스

## 상세기술
React로 제공되는 웹페이지에서 사용자는 회원가입 시 DID를 발급받게 되며, DID Document는 EC2 (hardhat Node)에 저장됩니다.
회원가입 정보는 AWS RDB에 저장되며, 비밀번호는 랜덤으로 생성된 Salt값을 사용합니다. 이때 해당 유저의 ID의 첫 글자를 가져와, ID+Salt값으로 새로운 Salt값을 생성하고, 이를 SHA256으로 해시합니다. 해시된 문자열 끝에는 ID를 제외한 salt가 추가로 붙어 저장됩니다.
Cognito 로그인은 가능하지만, Backend 인증 및 DID만으로의 로그인은 아직 구현되지 않았습니다. 로그인 시 AWS S3에 새로운 폴더가 생성되어 사용자에게 보여집니다. 다른 사용자의 폴더도 볼 수 있지만, 폴더에 접근하여 다운로드 및 업로드를 하기 위해서는 인증이 필요합니다.
사용자는 Holder의 역할을 하며, 웹페이지(Verifier)에게 관리자(Issuer)로부터 발급 받은 인증서(VC)를 Holder의 개인키를 이용하여 서명한 VP(Verifiable Presentation)를 제출하여 인증합니다. 인증 후 해당 사용자는 폴더에 접근하여 파일을 업로드 및 다운로드 할 수 있습니다.
업로드된 사진 및 파일은 S3에 저장되며 CDN을 통하여 사진을 불러 오게 되어 사용자에게 보여집니다.
hardhat Node는 EC2 8545포트에서 작동 중이며, [http://www.fufuanfox.com](http://www.fufuanfox.com) 로 접근 시 로드밸런서에서 8545 포트로 redirect 설정을 했습니다. 같은 서버에서 작동 중인 Spring Boot는 HTTPS로 접근 시 redirect 설정되어 있습니다.

웹페이지에서 회원가입 시 MetaMask를 이용하여 Transaction을 전송하며, 테스트 계정 로그인 시 10000 ETH가 지급됩니다.

## +(추가) SNS 로그인 기능 개발,
Kakao, Google, Apple 로그인 기능을 추가하였습니다.
KaKao, goole , apple 로그인 시 사용할 수 있는 1회용 토큰을 발급받고 서버에 전달합니다. 서버는 해당 토큰을 각 플랫폼 (kakao,google,apple)에 통신하여 인증하고 해당 유저의
정보를 받아옵니다. 이후 해당 유저의 정보를 이용하여 회원가입을 진행하게 됩니다. 처음 인증 후 server의 개인 토큰(toyproject용 토큰)을 jwt(ACCESSTOKEN, RERESH TOKEN)형태로 발급하게 되어 클라이언트가 리소스요청시 
헤더에 jwt를 담아서 보내고 AWS api gateway에서는 이를 검증하게 됩니다. jwt발급시 사용한 PRIVATE, 검증시 사용한 PUBLIC Key는 AWS KMS 키를 연결해 사용하고 RSA4096방식을 이용했으며
보안강화를 위해 유저정보와 RefreshToken을 키쌍으로 AWS REDIS에 저장하게 됩니다. 

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

## 사용 기술
- **AWS**: AWS KMS, Encryption SDK, S3, EC2, CloudFront, Route53(www.fufuanfox.com), VPC, LoadBalancer, IAM, InternetGateWay, Elastic IP, Certificate Manager(ssl), RDS, Notification Service(SNS)
- **Node.js**: hardhat Node (EC2에서 동작 중), Using Web3j Eth (테스트)

및 추가 테스트 용 기능:
- SFTP
- DID
- jasypt
- Cognito

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


