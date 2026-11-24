package it.unibo.mvc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * Encapsulates the concept of configuration.
 */
public final class Configuration {

    private final int max;
    private final int min;
    private final int attempts;

    private Configuration(final int max, final int min, final int attempts) {
        this.max = max;
        this.min = min;
        this.attempts = attempts;
    }

    /**
     * The maximum value.
     *
     * @return the maximum value
     */
    public int getMax() {
        return max;
    }

    /**
     * The minium value.
     *
     * @return the minimum value
     */
    public int getMin() {
        return min;
    }

    /**
     * The number of attempts.
     *
     * @return the number of attempts
     */
    public int getAttempts() {
        return attempts;
    }

    /**
     * Checks whether the configuration is consistent.
     *
     * @return true if the configuration is consistent
     */
    public boolean isConsistent() {
        return attempts > 0 && min < max;
    }

    /**
     * Pattern builder: used here because:
     * <br>
     * - all the parameters of the Configuration class have a default value, which
     * means that we would like to have all the possible combinations of
     * constructors (one with three parameters, three with two parameters, three
     * with a single parameter), which are way too many and confusing to use
     * <br>
     * - moreover, it would be impossible to provide all of them, because they are
     * all the same type, and only a single constructor can exist with a given
     * list of parameter types.
     * <br>
     * - the Configuration class has three parameters of the same type, and it is
     * unclear to understand, in a call to its constructor, which is which. By using
     * the builder, we emulate the so-called "named arguments".
     */
    public static class Builder {

        public static final String SEP = File.separator;
        private final String filePath = System.getProperty("src" + SEP + "main" + SEP + "resources" + SEP + "config.yml");

        private int currentMin;
        private int currentMax;
        private int currentAttempts;
        private boolean consumed;

        /**
         * Default constructor. Produces a builder with the default configuration.
         */

        public Builder() throws IOException {

            try (
                InputStream input = new FileInputStream(filePath);
                Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
                BufferedReader buffer = new BufferedReader(reader);
            ) {
                this.currentMin = readDefaultValue(buffer);
                this.currentMax = readDefaultValue(buffer);
                this.currentAttempts = readDefaultValue(buffer);
            }
        }

        private static int readDefaultValue(final BufferedReader reader) throws IOException {
            final String line = reader.readLine();
            if (line == null) {
                throw new IOException("file cannot be null");
            }
            final String[] parts = line.split(":");
            if (parts.length != 2) {
                throw new IOException("strange line not good: " + line);
            }
            return Integer.parseInt(parts[1].trim());
        }

        /**
         * Sets the minimum value.
         *
         * @param min the minimum value
         * @return this builder, for method chaining
         */
        public Builder withMin(final int min) {
            this.currentMin = min;
            return this;
        }

        /**
         * Sets the maximum value.
         *
         * @param max the maximum value
         * @return this builder, for method chaining
         */
        public Builder withMax(final int max) {
            this.currentMax = max;
            return this;
        }

        /**
         * Sets the attempts count.
         *
         * @param attempts the attempts count
         * @return this builder, for method chaining
         */
        public Builder withAttempts(final int attempts) {
            this.currentAttempts = attempts;
            return this;
        }

        /**
         * Builds the configuration.
         *
         * @return a configuration
         */
        public final Configuration build() {
            if (consumed) {
                throw new IllegalStateException("The builder can only be used once");
            }
            consumed = true;
            return new Configuration(currentMax, currentMin, currentAttempts);
        }
    }
}

