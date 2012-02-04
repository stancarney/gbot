import javax.net.ssl.TrustManager
import javax.net.ssl.SSLContext
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.X509TrustManager
import javax.net.ssl.HttpsURLConnection

// subvert java's ssl implementation to allow self-signed certs
// without fucking around with keystores, etc
static def subvert() {
	def nullTrustManager = [
		checkClientTrusted: { chain, authType ->  },
		checkServerTrusted: { chain, authType ->  },
		getAcceptedIssuers: { null }
	]

	def nullHostnameVerifier = [
		verify: { hostname, session -> true }
	]

	SSLContext sc = SSLContext.getInstance("SSL")
	sc.init(null, [nullTrustManager as X509TrustManager] as TrustManager[], null)
	HttpsURLConnection.defaultSSLSocketFactory = sc.getSocketFactory()
	HttpsURLConnection.defaultHostnameVerifier = nullHostnameVerifier as HostnameVerifier
}
