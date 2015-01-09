# !!! INCOMPLETE !!!

This project is in-development and is not yet ready for release.

# RAML Mock Server

A testing library that allows the validation of `MockServer` expectations against a RAML specification file.

While it is possible for someone to generate a mock server, it is also valuable to validate your own interactions against the other service's RAML API specification.

## Example Usage

```java
public class OtherServiceIntegrationTest {
    @Rule
    public MockServerRule server = new MockServerRule(5000, this);
    
    @ClassRule
    public final static RamlSpecificationsRule SPECIFICATIONS = new RamlSpecificationsRule()
            .withSpecification("my-service", new ClassPathRamlSpecification("apispecs/apispecs.raml"))
            .withSpecification("other-service", new FilePathRamlSpecification("target/specifications/other-service/apispecs.raml"))
            .withSpecification("remote-service", new RemoteRamlSpecification("http://remote.site.com/apispecs.zip", new ZipArchiveRemoteResourceHandler("target/specifications/remote-service", "apispecs.raml")));
    
    @Test
    public void shouldInteractCorrectlyWithOtherService() {
        String expectedHelloJohnResponse = fixture("fixtures/hello-john-response.json");
        
        new MockServiceClient("localhost", 5000)
            .when(
                request()
                    .withPath("/hello/John")
                    .withMethod("GET")
                    .withHeaders(
                        new Header(HttpHeaders.ACCEPT, "application/json")
                    )
            )
            .respond(
                response()
                    .withStatusCode(200)
                    .withHeaders(
                        new Header(HttpHeaders.CONTENT_TYPE, "application/json")
                    )
                    .withBody(expectedHelloJohnResponse)
            );
            
        Client client = Client.create(new DefaultClientConfig());
        
        String helloJohnResponse = client.resource(URI.create("http://localhost:5000/hello/John")).accept("application/json").get(String.class);
        
        JSONAssert.assert(expectedHelloJohnResponse, helloJohnResponse, false);
        
        SPECIFICATIONS.get("other-service").obeyedBy(new MockServiceClient("localhost", 5000));
    }
}
```