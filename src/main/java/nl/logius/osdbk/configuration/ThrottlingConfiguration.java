package nl.logius.osdbk.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "throttling")
public class ThrottlingConfiguration {

    private List<ThrottlingConfiguration.Afnemer> afnemers;

    public static class Afnemer {
        private String oin;
        private int throttleValue;

        public String getOin() {
            return oin;
        }

        public void setOin(String oin) {
            this.oin = oin;
        }

        public int getThrottleValue() {
            return throttleValue;
        }

        public void setThrottleValue(int throttleValue) {
            this.throttleValue = throttleValue;
        }
    }

    public List<Afnemer> getAfnemers() {
        return afnemers;
    }

    public void setAfnemers(List<Afnemer> afnemers) {
        this.afnemers = afnemers;
    }

}
