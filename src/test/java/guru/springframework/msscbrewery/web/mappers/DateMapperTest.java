package guru.springframework.msscbrewery.web.mappers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DateMapperTest {

    @Test
    @DisplayName("Convert timestamp to offset date time")
    void shouldConvertTimestampToOffsetDateTime() {
        Timestamp now = Timestamp.from(Instant.now());
        OffsetDateTime nowAsOdt = new DateMapper().timeStamp2OffsetDateTime(now);
        assertEquals(now.getTime(), nowAsOdt.toInstant().toEpochMilli());
    }

    @Test
    @DisplayName("Convert offset date time to time stamp")
    void shouldConvertiOffsetTimeToTimeStamp() {
        OffsetDateTime now = OffsetDateTime.now();
        Timestamp nowAsTs = new DateMapper().offsetDateTime2TimeStamp(now);
        assertEquals(now.toInstant().toEpochMilli(), nowAsTs.getTime());
    }

}