import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GetPropertyValues {
    Properties prop;
    InputStream inputStream;

    public GetPropertyValues() {
    }

    public Properties getPropValues() throws IOException {
        try {
            Properties prop = new Properties();
            String propFileName = "secrets.properties";
            this.inputStream = this.getClass().getClassLoader().getResourceAsStream(propFileName);
            if (this.inputStream == null) {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
            prop.load(this.inputStream);
            return prop;
        } catch (Exception ex) {
            System.out.println("Exception: " + ex);
        } finally {
            assert this.inputStream != null;
            this.inputStream.close();
        }

        return this.prop;
    }
}
