package net.ozwolf.mockserver.raml.specification;

import org.junit.Test;
import org.raml.model.Raml;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ClassPathSpecificationTest {
    @Test
    public void shouldLoadRamlFromClassPath() {
        ClassPathSpecification specification = new ClassPathSpecification("my-service", "apispecs-test/apispecs.raml");

        Raml raml = specification.getRaml();

        assertThat(raml.getResources().get("/hello/{name}").getDisplayName(), is("Hello Greeting"));
    }
}