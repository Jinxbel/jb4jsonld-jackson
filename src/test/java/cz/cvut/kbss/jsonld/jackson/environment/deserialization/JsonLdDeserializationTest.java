/**
 * Copyright (C) 2017 Czech Technical University in Prague
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package cz.cvut.kbss.jsonld.jackson.environment.deserialization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.kbss.jsonld.jackson.JsonLdModule;
import cz.cvut.kbss.jsonld.jackson.environment.Environment;
import cz.cvut.kbss.jsonld.jackson.environment.model.Employee;
import cz.cvut.kbss.jsonld.jackson.environment.model.Organization;
import cz.cvut.kbss.jsonld.jackson.environment.model.User;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class JsonLdDeserializationTest {

    private static final URI HALSEY_URI = URI
            .create("http://krizik.felk.cvut.cz/ontologies/jb4jsonld#Catherine+Halsey");
    private static final URI LASKY_URI = URI
            .create("http://krizik.felk.cvut.cz/ontologies/jb4jsonld#Thomas+Lasky");
    private static final URI PALMER_URI = URI
            .create("http://krizik.felk.cvut.cz/ontologies/jb4jsonld#Sarah+Palmer");

    private static final Map<URI, User> USERS = initUsers();

    private static final URI ORG_URI = URI.create("http://krizik.felk.cvut.cz/ontologies/jb4jsonld#UNSC");
    private static final String ORG_NAME = "UNSC";
    private static final String[] ORG_BRANDS = {"Spartan-II", "Mjolnir IV"};

    private ObjectMapper objectMapper;

    private static Map<URI, User> initUsers() {
        final Map<URI, User> map = new HashMap<>();
        map.put(HALSEY_URI, new User(HALSEY_URI, "Catherine", "Halsey", "halsey@unsc.org", true));
        map.put(LASKY_URI, new User(LASKY_URI, "Thomas", "Lasky", "lasky@unsc.org", false));
        map.put(PALMER_URI, new User(PALMER_URI, "Sarah", "Palmer", "palmer@unsc.org", false));
        return map;
    }

    @Before
    public void setUp() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JsonLdModule());
    }

    @Test
    public void testDeserializeInstanceWithDataProperties() throws Exception {
        final String input = Environment.readData("objectWithDataProperties.json");
        final User result = objectMapper.readValue(input, User.class);
        assertNotNull(result);
        final User expected = USERS.get(HALSEY_URI);
        verifyUserAttributes(expected, result);
    }

    private void verifyUserAttributes(User expected, User actual) {
        assertEquals(expected.getUri(), actual.getUri());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getAdmin(), actual.getAdmin());
    }

    @Test
    public void testDeserializeInstanceWithSingularObjectProperty() throws Exception {
        final String input = Environment.readData("objectWithSingularReference.json");
        final Employee result = objectMapper.readValue(input, Employee.class);
        verifyUserAttributes(USERS.get(HALSEY_URI), result);
        assertNotNull(result.getEmployer());
        verifyOrganizationAttributes(result.getEmployer());
    }

    private void verifyOrganizationAttributes(Organization actual) {
        assertEquals(ORG_URI, actual.getUri());
        assertEquals(ORG_NAME, actual.getName());
        assertNotNull(actual.getDateCreated());
        for (String b : ORG_BRANDS) {
            assertTrue(actual.getBrands().contains(b));
        }
    }

    @Test
    public void testDeserializeCollectionOfInstances() throws Exception {
        final String input = Environment.readData("collectionOfInstances.json");
        final List<Employee> result = objectMapper.readValue(input, new TypeReference<List<Employee>>() {
        });
        assertNotNull(result);
        assertFalse(result.isEmpty());
        result.forEach(e -> {
            final User expected = USERS.get(e.getUri());
            verifyUserAttributes(expected, e);
        });
    }

    @Test
    public void testSupportForIgnoringUnknownProperties() throws Exception {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final String input = Environment.readData("objectWithUnknownProperty.json");
        final User result = objectMapper.readValue(input, User.class);
        assertNotNull(result);
        verifyUserAttributes(USERS.get(HALSEY_URI), result);
    }
}
