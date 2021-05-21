package unittests.rsl.persistence;

import org.junit.*;
import rsl.persistence.RslPersistence;
import rsl.persistence.PersistenceFactory;
import rsl.util.Random;
import rsl.util.TempDir;
import unittests.rsl.persistence.DataClasses.Customer;
import unittests.rsl.persistence.DataClasses.Point;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class CountTests {

    private static final int customerCount = 20;
    private static final int pointCount = 50;

    private static String dbPath;
    private static RslPersistence db;

    @BeforeClass
    public static void setUp()
    {
        dbPath = TempDir.makeTempFile(Random.randomString(12, true), ".objectdb");
        db = PersistenceFactory.getObjectDBPersistance(dbPath);
    }

    @Before
    public void beforeTest()
    {
        db.emptyDatabase();

        db.startTransaction();
        for(int i = 0; i < pointCount; i++)
        {
            db.save(new Point(Random.randomInt(0, 500), Random.randomInt(0, 500)));
        }

        for(int i = 0; i < customerCount; i++)
        {
            db.save(new Customer(Random.randomString(5), Random.randomString(5)));
        }
        db.endTransaction();
    }

    @AfterClass
    public static void tearDown()
    {
        db.close();
        try {
            Files.delete(Paths.get(dbPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void countAll()
    {
        long count = db.countAll();
        Assert.assertEquals(pointCount + customerCount, count);
    }

    @Test
    public void countByClass()
    {
        long count = 0;

        count = db.countByClass(Point.class);
        Assert.assertEquals(pointCount, count);

        count = db.countByClass(Customer.class);
        Assert.assertEquals(customerCount, count);

    }







}
