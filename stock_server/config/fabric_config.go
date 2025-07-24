package config

import (
	"crypto/x509"  
	"os"          
	"path"        
	// "github.com/hyperledger/fabric-gateway/pkg/client"
	"github.com/hyperledger/fabric-gateway/pkg/identity"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials"
)

const (
	MspID        = "Org1MSP"
	CryptoPath   = "../fabric-samples-main/test-network/organizations/peerOrganizations/org1.example.com"
	CertPath     = CryptoPath + "/users/User1@org1.example.com/msp/signcerts"
	KeyPath      = CryptoPath + "/users/User1@org1.example.com/msp/keystore"
	TLSCertPath  = CryptoPath + "/peers/peer0.org1.example.com/tls/ca.crt"
	PeerEndpoint = "dns:///localhost:7051"
	GatewayPeer  = "peer0.org1.example.com"
	ChannelName  = "mychannel"
	ChaincodeName = "basic" // 修改为部署的股票链码名称
)

func NewGrpcConnection() *grpc.ClientConn {
	cert, _ := os.ReadFile(TLSCertPath)
	certificate, _ := identity.CertificateFromPEM(cert)
	certPool := x509.NewCertPool()
	certPool.AddCert(certificate)
	transportCreds := credentials.NewClientTLSFromCert(certPool, GatewayPeer)

	conn, err := grpc.NewClient(PeerEndpoint, grpc.WithTransportCredentials(transportCreds))
	if err != nil {
		panic(err)
	}
	return conn
}

func NewIdentity() *identity.X509Identity {
	certPEM, _ := ReadFirstFile(CertPath)
	cert, _ := identity.CertificateFromPEM(certPEM)
	id, _ := identity.NewX509Identity(MspID, cert)
	return id
}

func NewSign() identity.Sign {
	keyPEM, _ := ReadFirstFile(KeyPath)
	key, _ := identity.PrivateKeyFromPEM(keyPEM)
	sign, _ := identity.NewPrivateKeySign(key)
	return sign
}

func ReadFirstFile(dirPath string) ([]byte, error) {
	dir, _ := os.Open(dirPath)
	files, _ := dir.Readdirnames(1)
	return os.ReadFile(path.Join(dirPath, files[0]))
}