////
//// Source code recreated from a .class file by IntelliJ IDEA
//// (powered by FernFlower decompiler)
////
//
//import java.security.NoSuchAlgorithmException;
//import java.util.ArrayList;
//import java.util.Date;
//
//public class Document {
//
//    private b a = new b();
//    private f b = new f();
//    private d c = new d();
//    private a d = new a();
//    private com.dreamsecurity.dsdid.didprops.service.a e = new com.dreamsecurity.dsdid.didprops.service.a();
//    private JsonDate f = new JsonDate("created");
//    private JsonDate g = new JsonDate("updated");
//    private ProofContainer h = new ProofContainer();
//
//    public DidDocument() {
//        this.e.deleteIfNull(true);
//        this.e.errorIfNull(false);
//        this.f.deleteIfNull(true);
//        this.f.errorIfNull(false);
//        this.g.deleteIfNull(true);
//        this.g.errorIfNull(false);
//        this.h.deleteIfNull(true);
//        this.h.errorIfNull(false);
//    }
//
//    public DidDocument(String var1) {
//        this.b.value(var1);
//        this.e.deleteIfNull(true);
//        this.e.errorIfNull(false);
//        this.f.deleteIfNull(true);
//        this.f.errorIfNull(false);
//        this.g.deleteIfNull(true);
//        this.g.errorIfNull(false);
//        this.h.deleteIfNull(true);
//        this.h.errorIfNull(false);
//    }
//
//    public b getContext() {
//        return this.a;
//    }
//
//    public void setContext(b var1) {
//        this.a = var1;
//    }
//
//    public String getDid() {
//        return (String)this.b.value();
//    }
//
//    public void setDid(String var1) {
//        this.b.value(var1);
//    }
//
//    public String getPublicKeyIdCandidate(String var1) {
//        String var2 = null;
//
//        for(int var3 = 0; var3 < this.c.size(); ++var3) {
//            DidPublicKeyPrimitive var4 = (DidPublicKeyPrimitive)this.c.get(var3);
//
//            DIDURL var6;
//            try {
//                var6 = DIDURL.getInstance(var4.getId());
//            } catch (Exception var5) {
//                var5.printStackTrace();
//                return null;
//            }
//
//            String var7;
//            if ((var7 = var6.getFragment()).startsWith(var1)) {
//                if (var2 == null) {
//                    var2 = var7;
//                } else if (var2.compareTo(var7) < 0) {
//                    var2 = var7;
//                }
//            }
//        }
//
//        if (var2 == null) {
//            var1 = var1 + "1";
//        } else {
//            var1 = var1 + (Integer.parseInt(var2.substring(var1.length())) + 1);
//        }
//
//        return (String)this.b.value() + "#" + var1;
//    }
//
//    public String getServiceIdCandidate(String var1) {
//        return (String)this.b.value() + "#" + var1;
//    }
//
//    public ArrayList<DidPublicKeyPrimitive> getPublicKeySet() {
//        ArrayList var1 = new ArrayList();
//
//        for(int var2 = 0; var2 < this.c.size(); ++var2) {
//            var1.add(this.c.get(var2));
//        }
//
//        return var1;
//    }
//
//    public DidPublicKeyPrimitive findPublicKey(String var1) throws NoSuchAlgorithmException {
//        for(int var2 = 0; var2 < this.c.size(); ++var2) {
//            if (((DidPublicKeyPrimitive)this.c.get(var2)).getId().equals(var1) && ((DidPublicKeyPrimitive)this.c.get(var2)).getType().equals("Ed25519Signature2018")) {
//                PublicKeyEd25519Signature2018 var5;
//                (var5 = new PublicKeyEd25519Signature2018()).setId(((DidPublicKeyPrimitive)this.c.get(var2)).getId());
//                var5.setType(((DidPublicKeyPrimitive)this.c.get(var2)).getType());
//                var5.setPublicKeyString(((DidPublicKeyPrimitive)this.c.get(var2)).getPublicKeyString());
//                var5.setController(((DidPublicKeyPrimitive)this.c.get(var2)).getController());
//                return var5;
//            }
//
//            if (((DidPublicKeyPrimitive)this.c.get(var2)).getId().equals(var1) && ((DidPublicKeyPrimitive)this.c.get(var2)).getType().equals("RsaSignature2018")) {
//                PublicKeyRsaSignature2018 var4;
//                (var4 = new PublicKeyRsaSignature2018()).setId(((DidPublicKeyPrimitive)this.c.get(var2)).getId());
//                var4.setType(((DidPublicKeyPrimitive)this.c.get(var2)).getType());
//                var4.setPublicKeyString(((DidPublicKeyPrimitive)this.c.get(var2)).getPublicKeyString());
//                var4.setController(((DidPublicKeyPrimitive)this.c.get(var2)).getController());
//                return var4;
//            }
//
//            if (((DidPublicKeyPrimitive)this.c.get(var2)).getId().equals(var1) && ((DidPublicKeyPrimitive)this.c.get(var2)).getType().equals("Secp256k1VerificationKey2019")) {
//                PublicKeySecp256k1VerificationKey2019 var3;
//                (var3 = new PublicKeySecp256k1VerificationKey2019()).setId(((DidPublicKeyPrimitive)this.c.get(var2)).getId());
//                var3.setType(((DidPublicKeyPrimitive)this.c.get(var2)).getType());
//                var3.setPublicKeyString(((DidPublicKeyPrimitive)this.c.get(var2)).getPublicKeyString());
//                var3.setController(((DidPublicKeyPrimitive)this.c.get(var2)).getController());
//                return var3;
//            }
//        }
//
//        return null;
//    }
//
//    public void addPublicKey(DidPublicKeyPrimitive var1) {
//        this.c.add(var1);
//    }
//
//    public void deletePublicKey(String var1) {
//    }
//
//    public JsonSerializable getAuthentication() {
//        return this.d;
//    }
//
//    public void addAuthentication(DidPublicKeyPrimitive var1) {
//        this.d.add(var1);
//    }
//
//    public JsonSerializable getService() {
//        return this.e;
//    }
//
//    public void addService(ServicePrimitive var1) {
//        this.e.add(var1);
//    }
//
//    public void addAuthentication(String var1) {
//        this.d.add(var1);
//    }
//
//    public String getCreated() {
//        return (String)this.f.value();
//    }
//
//    public void setCreated(Date var1) {
//        this.f.value(var1);
//    }
//
//    public String getUpdated() {
//        return (String)this.g.value();
//    }
//
//    public void setUpdated(Date var1) {
//        this.g.value(var1);
//    }
//
//    public Proof getProof() {
//        return this.h.get();
//    }
//
//    public void setProof(Proof var1) {
//        this.h.set(var1);
//    }
//
//    public void sign(Proof var1, byte[] var2) throws Exception {
//        if (var1 == null) {
//            throw new NullPointerException("proof is null.");
//        } else if (var2 == null) {
//            throw new NullPointerException("private key is null.");
//        } else {
//            var1 = (new ProofSigner()).sign(this.getToBeSignedJson(), var1, var2);
//            this.h.set(var1);
//        }
//    }
//
//    public boolean verify(byte[] var1) throws Exception {
//        if (this.h != null && this.h.get() != null) {
//            if (var1 == null) {
//                throw new NullPointerException("private key is null.");
//            } else {
//                return (new ProofSigner()).verify(this.getToBeSignedJson(), this.h.get(), var1);
//            }
//        } else {
//            throw new NullPointerException("proof is null.");
//        }
//    }
//}