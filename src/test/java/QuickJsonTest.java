import org.json.simple.JSONObject;

public class QuickJsonTest {
    public static void main(String[] args) {
        System.out.println("Testing JSON.simple library...");

        try {
            // Simple test
            JSONObject obj = new JSONObject();
            obj.put("message", "Hello JSON!");
            obj.put("status", "success");

            String result = obj.toJSONString();
            System.out.println("✅ SUCCESS! Library is working.");
            System.out.println("Generated JSON: " + result);

            // Verify
            if (result.contains("Hello JSON") && result.contains("success")) {
                System.out.println("✅ Verification passed!");
                System.exit(0); // Success
            } else {
                System.out.println("❌ Verification failed!");
                System.exit(1); // Failure
            }
        } catch (Exception e) {
            System.out.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(1); // Failure
        }
    }
}