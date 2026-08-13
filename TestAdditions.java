// Add these two methods to any existing @Test class under src/test/java/...
// Make sure these two imports are present at the top of that file:
//   import org.testng.Assert;
//   import org.testng.annotations.Test;

// --- Task 2: read + print the ENVIRONMENT variable during test execution ---
@Test
public void printEnvironmentVariable() {
    String environment = System.getenv("ENVIRONMENT");
    System.out.println("ENVIRONMENT value inside test: " + environment);
}

// --- Task 3: intentional, deterministic failure to trigger retryOnFailure ---
@Test
public void intentionalFailureTest() {
    Assert.fail("Intentional failure for retry testing");
}
