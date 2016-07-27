package cz.cvut.kbss.jsonld.jackson.environment;

import cz.cvut.kbss.jsonld.jackson.environment.model.Employee;
import cz.cvut.kbss.jsonld.jackson.environment.model.Organization;
import cz.cvut.kbss.jsonld.jackson.environment.model.User;

import java.net.URI;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Generator {

    public static final String URI_BASE = "http://krizik.felk.cvut.cz/ontologies/jaxb-jsonld#";

    private static final Random RAND = new Random();

    private Generator() {
        throw new AssertionError();
    }

    /**
     * Returns a (pseudo)random positive integer between 1 (inclusive) and {@code max} (exclusive).
     *
     * @param max Upper bound
     * @return random integer
     */
    public static int randomCount(int max) {
        assert max > 1;
        int res;
        do {
            res = RAND.nextInt(max);
        } while (res < 1);
        return res;
    }

    public static boolean randomBoolean() {
        return RAND.nextBoolean();
    }

    public static User generateUser() {
        final User user = new User();
        setUserAttributes(user);
        return user;
    }

    public static Set<User> generateUsers() {
        final Set<User> users = new HashSet<>();
        for (int i = 0; i < randomCount(10); i++) {
            users.add(generateUser());
        }
        return users;
    }

    private static void setUserAttributes(User user) {
        final int number = RAND.nextInt();
        user.setUsername("user" + number);
        user.setFirstName("FirstName" + number);
        user.setLastName("LastName" + number);
        user.setUri(generateUri());
    }

    public static URI generateUri() {
        return URI.create(URI_BASE + RAND.nextInt());
    }

    public static Employee generateEmployee() {
        final Employee employee = new Employee();
        setUserAttributes(employee);
        final Organization company = generateOrganization();
        employee.setEmployer(company);
        return employee;
    }

    public static Organization generateOrganization() {
        final Organization org = new Organization();
        org.setUri(generateUri());
        org.setDateCreated(new Date());
        org.setName("Organization" + RAND.nextInt());
        org.setBrands(new HashSet<>());
        for (int i = 0; i < randomCount(10); i++) {
            org.getBrands().add("Brandy" + i);
        }
        return org;
    }
}